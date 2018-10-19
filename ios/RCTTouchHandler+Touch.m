#import <UIKit/UIKit.h>
#import <UIKit/UIGestureRecognizerSubclass.h>
#import <React/RCTTouchHandler.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTBridge+Private.h>
#import <React/RCTTouchEvent.h>
#import <React/RCTUtils.h>
#import <React/UIView+React.h>
@import CobrowseIO;

@import CobrowseIO;

static uint16_t _coalescingKey = 0;

@implementation RCTTouchHandler (Touch)

- (void) cobrowseTouchesBegan:(NSSet<CBIOTouch*> *)touches withEvent:(CBIOTouchEvent *)event {
    _coalescingKey++;
    [self cobrowseDispatchEvent:@"touchStart" withTouches: touches withEvent:event];
    _coalescingKey++;
    if (self.state == UIGestureRecognizerStatePossible)
        self.state = UIGestureRecognizerStateBegan;
}

- (void) cobrowseTouchesMoved:(NSSet<CBIOTouch*> *)touches withEvent:(CBIOTouchEvent *)event {
    [self cobrowseDispatchEvent:@"touchMove" withTouches: touches withEvent:event];
    self.state = UIGestureRecognizerStateChanged;
}

- (void) cobrowseTouchesEnded:(NSSet<CBIOTouch*> *)touches withEvent:(CBIOTouchEvent *)event {
    _coalescingKey++;
    [self cobrowseDispatchEvent:@"touchEnd" withTouches: touches withEvent:event];
    _coalescingKey++;
    self.state = UIGestureRecognizerStateEnded;
}

- (void) cobrowseTouchesCancelled:(NSSet<CBIOTouch*> *)touches withEvent:(CBIOTouchEvent *)event {
    self.state = UIGestureRecognizerStateCancelled;
}

-(void) cobrowseDispatchEvent: (NSString*) eventName withTouches:(NSSet<CBIOTouch*> *)touches withEvent: (CBIOTouchEvent*) event {
    // no multi-touch support yet
    if (touches.allObjects.count > 1) return;
    
    CBIOTouch* touch = touches.allObjects.firstObject;
    NSNumber* reactTag = self.view.reactTag;
    
    UIView *targetView = touch.target;
    while (targetView) {
        if (targetView.reactTag && targetView.userInteractionEnabled) break;
        targetView = targetView.superview;
    }
    
    CGPoint viewCoords = [targetView convertPoint:event.position fromView: nil];
    
    NSMutableDictionary *reactTouch = [NSMutableDictionary dictionary];
    reactTouch[@"target"] = targetView.reactTag;
    reactTouch[@"identifier"] = @(1);
    reactTouch[@"pageX"] = @(RCTSanitizeNaNValue(event.position.x, @"touchEvent.pageX"));
    reactTouch[@"pageY"] = @(RCTSanitizeNaNValue(event.position.y, @"touchEvent.pageY"));
    reactTouch[@"locationX"] = @(RCTSanitizeNaNValue(viewCoords.x, @"touchEvent.locationX"));
    reactTouch[@"locationY"] = @(RCTSanitizeNaNValue(viewCoords.y, @"touchEvent.locationY"));
    reactTouch[@"timestamp"] =  @([NSProcessInfo.processInfo systemUptime] * 1000); // in ms, for JS
    
    RCTTouchEvent* reactEvent = [[RCTTouchEvent alloc] initWithEventName:eventName
                                                         reactTag:reactTag
                                                     reactTouches:@[reactTouch]
                                                   changedIndexes:@[@(0)]
                                                    coalescingKey:_coalescingKey];
    
    RCTEventDispatcher* dispatcher = [[RCTBridge currentBridge] moduleForClass:[RCTEventDispatcher class]];
    [dispatcher sendEvent: reactEvent];
}

@end
