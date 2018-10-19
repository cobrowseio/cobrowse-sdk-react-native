#import <UIKit/UIKit.h>
#import <React/RCTTouchHandler.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTBridge+Private.h>
#import <React/RCTTouchEvent.h>
#import <React/RCTUtils.h>
#import <React/UIView+React.h>
@import CobrowseIO;

@import CobrowseIO;

@implementation RCTTouchHandler (Touch)

- (void) cobrowseTouchesBegan:(NSSet<CBIOTouch*> *)touches withEvent:(CBIOTouchEvent *)event {
    [self cobrowseDispatchEvent:@"touchStart" withTouches: touches withEvent:event];
}

- (void) cobrowseTouchesMoved:(NSSet<CBIOTouch*> *)touches withEvent:(CBIOTouchEvent *)event {
    [self cobrowseDispatchEvent:@"touchMove" withTouches: touches withEvent:event];
}

- (void) cobrowseTouchesEnded:(NSSet<CBIOTouch*> *)touches withEvent:(CBIOTouchEvent *)event {
    [self cobrowseDispatchEvent:@"touchEnd" withTouches: touches withEvent:event];
}

-(void) cobrowseDispatchEvent: (NSString*) eventName withTouches:(NSSet<CBIOTouch*> *)touches withEvent: (CBIOTouchEvent*) event {
    // no multi-touch support yet
    if (touches.allObjects.count > 1) return;
    
    CBIOTouch* touch = touches.allObjects.firstObject;
    NSNumber* reactTag = self.view.reactTag;
    
    CGPoint viewCoords = [touch.target convertPoint:event.position fromView: nil];
    
    NSMutableDictionary *reactTouch = [NSMutableDictionary dictionary];
    reactTouch[@"target"] = touch.target.reactTag;
    reactTouch[@"identifier"] = @(1);
    reactTouch[@"pageX"] = @(RCTSanitizeNaNValue(roundf(event.position.x), @"touchEvent.pageX"));
    reactTouch[@"pageY"] = @(RCTSanitizeNaNValue(roundf(event.position.y), @"touchEvent.pageY"));
    reactTouch[@"locationX"] = @(RCTSanitizeNaNValue(roundf(viewCoords.x), @"touchEvent.locationX"));
    reactTouch[@"locationY"] = @(RCTSanitizeNaNValue(roundf(viewCoords.y), @"touchEvent.locationY"));
    reactTouch[@"timestamp"] =  @([NSProcessInfo.processInfo systemUptime] * 1000); // in ms, for JS
    
    RCTTouchEvent* reactEvent = [[RCTTouchEvent alloc] initWithEventName:eventName
                                                         reactTag:reactTag
                                                     reactTouches:@[reactTouch]
                                                   changedIndexes:@[@(0)]
                                                    coalescingKey:1];
    
    RCTEventDispatcher* dispatcher = [[RCTBridge currentBridge] moduleForClass:[RCTEventDispatcher class]];
    [dispatcher sendEvent: reactEvent];
}

@end
