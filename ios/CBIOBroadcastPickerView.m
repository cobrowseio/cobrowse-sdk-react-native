#import <ReplayKit/ReplayKit.h>
#import "CBIOBroadcastPickerView.h"

@implementation CBIOBroadcastPickerView

RCT_EXPORT_MODULE(CBIOBroadcastPickerView)

- (UIView *)view
{
    NSString* broadcastExtension = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CBIOBroadcastExtension"];
    NSString* warningMsg = @"";
    
    if (!broadcastExtension) {
        warningMsg = @"CBIOBroadcastExtension not set on Info.plist.";
    } else if (@available(iOS 12.0, *)) {
        RPSystemBroadcastPickerView* systemPicker = [[RPSystemBroadcastPickerView alloc] initWithFrame: CGRectMake(0, 0, 50, 50)];
        systemPicker.showsMicrophoneButton = NO;
        systemPicker.preferredExtension = broadcastExtension;
        
        return systemPicker;
    } else {
        // Fallback on earlier versions
        warningMsg = @"This version of iOS doesn't support RPSystemBroadcastPickerView.";
    }
    
    RCTLogWarn(@"%@", warningMsg);
    return [[UIView alloc] initWithFrame: CGRectMake(0, 0, 0, 0)];
}

RCT_CUSTOM_VIEW_PROPERTY(buttonColor, NSString, UIView)
{
    UIButton* button = view.subviews.firstObject;
    if ([button isKindOfClass:UIButton.class]) button.imageView.tintColor = [self hexStringToColor:json];
}

- hexStringToColor:(NSString *)stringToConvert
{
    NSString *noHashString = [stringToConvert stringByReplacingOccurrencesOfString:@"#" withString:@""];
    NSScanner *stringScanner = [NSScanner scannerWithString:noHashString];
    
    unsigned hex;
    if (![stringScanner scanHexInt:&hex]) return nil;
    int r = (hex >> 16) & 0xFF;
    int g = (hex >> 8) & 0xFF;
    int b = (hex) & 0xFF;
    
    return [UIColor colorWithRed:r / 255.0f green:g / 255.0f blue:b / 255.0f alpha:1.0f];
}

@end
