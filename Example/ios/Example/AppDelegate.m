#import "AppDelegate.h"

#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

#ifdef FB_SONARKIT_ENABLED
#import <FlipperKit/FlipperClient.h>
#import <FlipperKitLayoutPlugin/FlipperKitLayoutPlugin.h>
#import <FlipperKitUserDefaultsPlugin/FKUserDefaultsPlugin.h>
#import <FlipperKitNetworkPlugin/FlipperKitNetworkPlugin.h>
#import <SKIOSNetworkPlugin/SKIOSNetworkAdapter.h>
#import <FlipperKitReactPlugin/FlipperKitReactPlugin.h>

static void InitializeFlipper(UIApplication *application) {
  FlipperClient *client = [FlipperClient sharedClient];
  SKDescriptorMapper *layoutDescriptorMapper = [[SKDescriptorMapper alloc] initWithDefaults];
  [client addPlugin:[[FlipperKitLayoutPlugin alloc] initWithRootNode:application withDescriptorMapper:layoutDescriptorMapper]];
  [client addPlugin:[[FKUserDefaultsPlugin alloc] initWithSuiteName:nil]];
  [client addPlugin:[FlipperKitReactPlugin new]];
  [client addPlugin:[[FlipperKitNetworkPlugin alloc] initWithNetworkAdapter:[SKIOSNetworkAdapter new]]];
  [client start];
}
#endif

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
#ifdef FB_SONARKIT_ENABLED
  InitializeFlipper(application);
#endif

  RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:launchOptions];
  RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:bridge
                                                   moduleName:@"Example"
                                            initialProperties:nil];

  if (@available(iOS 13.0, *)) {
      rootView.backgroundColor = [UIColor systemBackgroundColor];
  } else {
      rootView.backgroundColor = [UIColor whiteColor];
  }

  RCTCobrowseIO.delegate = self;

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  return YES;
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index"];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

-(UIView *)findViewByAccessibilityLabel:(UIView *)parent :(NSString *)accessibilityLabel {
  if (parent.subviews.count > 0) {
    for (UIView *subView in parent.subviews) {
      NSLog(@"View:::: Class:%@, Tag: %ld, Description:%@, Hash:%lu", [subView class], [subView tag], [subView accessibilityLabel], [subView hash]);

      if ([subView.accessibilityLabel isEqualToString:accessibilityLabel] == YES) {
        NSLog(@"Found UIAlertController! with Class:%@, Tag: %ld, Description:%@, Hash:%lu", [subView class], [subView tag], [subView description], [subView hash]);
        return subView;
      }
      
      UIView *result = [self findViewByAccessibilityLabel:subView:accessibilityLabel];
      if (result) {
        return result;
      }
    }
  }
  
  return nil;
}

-(NSArray<UIView *> *)cobrowseRedactedViewsForViewController:(UIViewController *)vc {
  UIView *devMenu = [self findViewByAccessibilityLabel:self.window:@"React Native Debug Menu"];
  
  if (devMenu) {
    return @[self.window.rootViewController.view, devMenu];
  }

  return @[self.window.rootViewController.view];
}

-(NSArray<UIView *> *)cobrowseUnredactedViewsForViewController:(UIViewController *)vc {
  UIView *bundlerConfig = [self findViewByAccessibilityLabel:self.window:@"Configure Bundler"];
  
  if (bundlerConfig) {
    return @[bundlerConfig];
  }
  return @[];
}

@end
