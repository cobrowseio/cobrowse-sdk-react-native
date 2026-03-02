#import <UIKit/UIKit.h>
#import <UIKit/UIGestureRecognizerSubclass.h>
#import <objc/runtime.h>
#import "RCTSurfaceTouchHandler+Touch.h"

// Check for RCTSurfaceTouchHandler availability
#if __has_include("RCTSurfaceTouchHandler.h")
  #import "RCTSurfaceTouchHandler.h"
#elif __has_include(<React/RCTSurfaceTouchHandler.h>)
  #import <React/RCTSurfaceTouchHandler.h>
#elif __has_include(<React-RCTFabric/RCTSurfaceTouchHandler.h>)
  #import <React-RCTFabric/RCTSurfaceTouchHandler.h>
#else
  #define CBIO_NO_SURFACE_TOUCH_HANDLER 1
#endif

#ifndef CBIO_NO_SURFACE_TOUCH_HANDLER

// Check for Fabric C++ headers
#if __has_include(<react/renderer/components/view/TouchEventEmitter.h>)
  #import <react/renderer/components/view/TouchEventEmitter.h>
#elif __has_include(<React-Fabric/react/renderer/components/view/TouchEventEmitter.h>)
  #import <React-Fabric/react/renderer/components/view/TouchEventEmitter.h>
#elif __has_include("react/renderer/components/view/TouchEventEmitter.h")
  #import "react/renderer/components/view/TouchEventEmitter.h"
#else
  #define CBIO_NO_FABRIC_HEADERS 1
#endif

#ifndef CBIO_NO_FABRIC_HEADERS
#include <exception>
#endif

#pragma mark - Logging

static BOOL _supportWarningLogged = NO;

static void logSupportWarning(NSString *issue) {
    if (_supportWarningLogged) return;
    _supportWarningLogged = YES;

    NSLog(@"[Cobrowse WARNING] Fabric touch dispatch failed: %@. "
          @"Please contact support@cobrowse.io with your React Native version.", issue);
}

#pragma mark - Helper Functions

#ifndef CBIO_NO_FABRIC_HEADERS

// Walk up from the hit-tested view to find the nearest ancestor that
// responds to touchEventEmitterAtPoint: (the "provider").
//
// In Fabric, touchEventEmitterAtPoint: is a simple getter that returns
// the view's own _eventEmitter, so the provider IS the target — its tag
// and coordinate space are used directly for the Touch struct.
//
// Simultaneously check whether we are inside a Fabric component subtree
// at all, so we warn if the emitter API has been removed or renamed in 
// a future RN version.
static UIView * _Nullable findProvider(UIView *startView, BOOL *outSawFabric) {
    SEL sel = NSSelectorFromString(@"touchEventEmitterAtPoint:");

    static Protocol *componentViewProtocol = NSProtocolFromString(@"RCTComponentViewProtocol");

    UIView *provider = nil;
    *outSawFabric = NO;

    for (UIView *v = startView; v; v = v.superview) {
        if (!*outSawFabric && componentViewProtocol && [v conformsToProtocol:componentViewProtocol]) *outSawFabric = YES;
        if (!provider && [v respondsToSelector:sel]) {
          provider = v;
          break;
        }
    }

    return provider;
}

// Get the touch event emitter from a provider view.
// In current Fabric, touchEventEmitterAtPoint: ignores the point
// parameter and simply returns the view's own emitter. We still
// pass the point for forward-compatibility.
static facebook::react::SharedTouchEventEmitter getEventEmitter(UIView *provider, CGPoint screenPoint) {
    SEL sel = NSSelectorFromString(@"touchEventEmitterAtPoint:");

    CGPoint localPoint = [provider convertPoint:screenPoint fromView:nil];

    typedef facebook::react::SharedTouchEventEmitter (*GetEmitterIMP)(id, SEL, CGPoint);
    GetEmitterIMP impl = (GetEmitterIMP)[provider methodForSelector:sel];

    if (!impl) {
        return nullptr;
    }

    return impl(provider, sel, localPoint);
}

static facebook::react::Touch createTouch(CGPoint screenPoint,
                                           CGPoint localPoint,
                                           NSInteger targetTag) {
    facebook::react::Touch touch{};
  
    facebook::react::Point screen = {
        static_cast<facebook::react::Float>(screenPoint.x),
        static_cast<facebook::react::Float>(screenPoint.y)
    };
    touch.pagePoint = screen;
    touch.screenPoint = screen;
    touch.offsetPoint = {
        static_cast<facebook::react::Float>(localPoint.x),
        static_cast<facebook::react::Float>(localPoint.y)
    };
    
    touch.identifier = 1;
    touch.target = static_cast<facebook::react::Tag>(targetTag);

    NSTimeInterval nowEpoch = [[NSDate date] timeIntervalSince1970];
    NSTimeInterval nowUptime = [[NSProcessInfo processInfo] systemUptime];
    touch.timestamp = (nowEpoch - nowUptime) + nowUptime;

    touch.force = 1.0;

    return touch;
}

typedef NS_ENUM(NSInteger, CBIOFabricTouchPhase) {
    CBIOFabricTouchPhaseStart,
    CBIOFabricTouchPhaseMove,
    CBIOFabricTouchPhaseEnd,
    CBIOFabricTouchPhaseCancel
};

#endif // !CBIO_NO_FABRIC_HEADERS

#pragma mark - Category Implementation

@implementation RCTSurfaceTouchHandler (Touch)

- (BOOL)cobrowseDispatchEvent:(CBIOFabricTouchPhase)phase
                  withTouches:(NSSet<CBIOTouch *> *)touches
                    withEvent:(CBIOTouchEvent *)event {
#ifndef CBIO_NO_FABRIC_HEADERS
    // No multi-touch support yet
    if (touches.count != 1) return NO;

    CBIOTouch *touch = touches.anyObject;
    UIView *hitView = touch.target;
    CGPoint screenPoint = event.position;

    BOOL sawFabric = NO;
    UIView *provider = findProvider(hitView, &sawFabric);

    if (!provider) {
        if (sawFabric) {
            logSupportWarning(@"Fabric component view detected, but no ancestor responds to touchEventEmitterAtPoint");
        }
        return NO;
    }

    // Get the emitter. ObjC messaging is wrapped in @try/@catch.
    facebook::react::SharedTouchEventEmitter emitter;
    @try {
        emitter = getEventEmitter(provider, screenPoint);
    } @catch (NSException *e) {
        logSupportWarning([NSString stringWithFormat:
            @"Exception getting emitter: %@", e.reason]);
        return NO;
    }

    if (!emitter) {
        return NO;
    }

    NSInteger targetTag = provider.tag;
    CGPoint localPoint = [provider convertPoint:screenPoint fromView:nil];

    facebook::react::Touch t = createTouch(screenPoint, localPoint, targetTag);

    facebook::react::TouchEvent touchEvent{};
    facebook::react::Touches touchSet;
    touchSet.insert(t);
    facebook::react::Touches emptySet;

    switch (phase) {
        case CBIOFabricTouchPhaseStart:
        case CBIOFabricTouchPhaseMove:
            touchEvent.touches = touchSet;
            touchEvent.changedTouches = touchSet;
            touchEvent.targetTouches = touchSet;
            break;

        case CBIOFabricTouchPhaseEnd:
        case CBIOFabricTouchPhaseCancel:
            touchEvent.touches = emptySet;
            touchEvent.changedTouches = touchSet;
            touchEvent.targetTouches = emptySet;
            break;

        default:
            return NO;
    }

#if defined(__cpp_exceptions)
    try {
#endif
        switch (phase) {
            case CBIOFabricTouchPhaseStart:  emitter->onTouchStart(touchEvent);  break;
            case CBIOFabricTouchPhaseMove:   emitter->onTouchMove(touchEvent);   break;
            case CBIOFabricTouchPhaseEnd:    emitter->onTouchEnd(touchEvent);    break;
            case CBIOFabricTouchPhaseCancel: emitter->onTouchCancel(touchEvent); break;
            default: return NO;
        }
        return YES;
#if defined(__cpp_exceptions)
    } catch (const std::exception &e) {
        logSupportWarning([NSString stringWithFormat:
            @"C++ exception dispatching touch: %s", e.what()]);
        return NO;
    } catch (...) {
        logSupportWarning(@"Unknown C++ exception dispatching touch");
        return NO;
    }
#endif

#else
    return NO;
#endif
}

- (void)cobrowseTouchesBegan:(NSSet<CBIOTouch *> *)touches withEvent:(CBIOTouchEvent *)event {
    if (![self cobrowseDispatchEvent:CBIOFabricTouchPhaseStart withTouches:touches withEvent:event]) return;

    if (self.state == UIGestureRecognizerStatePossible) {
        self.state = UIGestureRecognizerStateBegan;
    }
}

- (void)cobrowseTouchesMoved:(NSSet<CBIOTouch *> *)touches withEvent:(CBIOTouchEvent *)event {
    if (![self cobrowseDispatchEvent:CBIOFabricTouchPhaseMove withTouches:touches withEvent:event]) return;

    self.state = UIGestureRecognizerStateChanged;
}

- (void)cobrowseTouchesEnded:(NSSet<CBIOTouch *> *)touches withEvent:(CBIOTouchEvent *)event {
    [self cobrowseDispatchEvent:CBIOFabricTouchPhaseEnd withTouches:touches withEvent:event];
    self.state = UIGestureRecognizerStateEnded;
}

- (void)cobrowseTouchesCancelled:(NSSet<CBIOTouch *> *)touches withEvent:(CBIOTouchEvent *)event {
    [self cobrowseDispatchEvent:CBIOFabricTouchPhaseCancel withTouches:touches withEvent:event];
    self.state = UIGestureRecognizerStateCancelled;
}

@end

#endif // !CBIO_NO_SURFACE_TOUCH_HANDLER
