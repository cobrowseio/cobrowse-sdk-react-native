#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTUtils.h>

@import CobrowseIO;


@protocol RCTCobrowseIODelegate <NSObject>

@optional

-(nonnull NSArray<UIView*>*) cobrowseRedactedViewsForViewController: (nonnull UIViewController*) vc;
-(nonnull NSArray<UIView*>*) cobrowseUnredactedViewsForViewController: (nonnull UIViewController*) vc;

@end


@interface RCTCobrowseIO: RCTEventEmitter <RCTBridgeModule, CobrowseIODelegate>

@property (class, nullable) id<RCTCobrowseIODelegate> delegate;

@end

