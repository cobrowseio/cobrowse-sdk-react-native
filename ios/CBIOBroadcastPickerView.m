#import <ReplayKit/ReplayKit.h>
#import "CBIOBroadcastPickerView.h"

@import CobrowseIO;

@implementation CBIOBroadcastPickerView

//NSString* _buttonColor;

RCT_EXPORT_MODULE(CBIOBroadcastPickerView)
//RCT_EXPORT_VIEW_PROPERTY(buttonColor, NSString)

- (UIView *)view
{
    if (@available(iOS 12.0, *)) {
        RPSystemBroadcastPickerView* systemPicker = [[RPSystemBroadcastPickerView alloc] initWithFrame: CGRectMake(0, 0, 50, 50)];
        systemPicker.showsMicrophoneButton = NO;
        
        NSString* broadcastExtension = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CBIOBroadcastExtension"];
        systemPicker.preferredExtension = broadcastExtension;

        return systemPicker;
    } else {
        // Fallback on earlier versions
        UIView* fallback = [[UIView alloc] initWithFrame: CGRectMake(0, 0, 50, 50)];

        return fallback;
    }
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

- (UIColor *) colorWithHexString: (NSString *) hexString
{
    NSString *colorString = [[hexString stringByReplacingOccurrencesOfString: @"#" withString: @""] uppercaseString];

    NSLog(@"colorString :%@",colorString);
    CGFloat alpha, red, blue, green;

    // #RGB
    alpha = 1.0f;
    red   = [self colorComponentFrom: colorString start: 0 length: 2];
    green = [self colorComponentFrom: colorString start: 2 length: 2];
    blue  = [self colorComponentFrom: colorString start: 4 length: 2];

    return [UIColor colorWithRed: red green: green blue: blue alpha: alpha];
}


- (CGFloat) colorComponentFrom: (NSString *) string start: (NSUInteger) start length: (NSUInteger) length {
    NSString *substring = [string substringWithRange: NSMakeRange(start, length)];
    NSString *fullHex = length == 2 ? substring : [NSString stringWithFormat: @"%@%@", substring, substring];
    unsigned hexComponent;
    [[NSScanner scannerWithString: fullHex] scanHexInt: &hexComponent];
    return hexComponent / 255.0;
}
//
//- (NSString *)buttonColor {
//    return _buttonColor;
//}
//
//- (void)setButtonColor:(NSString *)buttonColor {
//    _buttonColor = buttonColor;
//
//    NSLog(@"Button color %@", _buttonColor);
//
//    UIColor* color = [self colorWithHexString:_buttonColor];
//    NSLog(@"Actual color %@", color);
//
//    UIButton* button = self.view.subviews.firstObject;
//    if ([button isKindOfClass:UIButton.class]) button.imageView.tintColor = color;
//}

@end
