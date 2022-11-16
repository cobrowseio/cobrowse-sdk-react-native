#import <React/RCTConvert.h>
#import "CBIOFullDeviceStateEnum.h"


@implementation RCTConvert (FullDeviceState)
  RCT_ENUM_CONVERTER(CBIOFullDeviceState, (@{ @"kCBIOFullDeviceStateOn" : @(kCBIOFullDeviceStateOn),
                                               @"kCBIOFullDeviceStateOff" : @(kCBIOFullDeviceStateOff),
                                               @"kCBIOFullDeviceStateRejected" : @(kCBIOFullDeviceStateRejected),
                                            @"kCBIOFullDeviceStateRequested" : @(kCBIOFullDeviceStateRequested),
                                           }),
                     kCBIOFullDeviceStateOff, integerValue)
@end

