#import <React/RCTBridgeDelegate.h>
#import <RCTCobrowseIO.h>
#import <UIKit/UIKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate, RCTBridgeDelegate, RCTCobrowseIODelegate>

@property (nonatomic, strong) UIWindow *window;

@end
