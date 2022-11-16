#import "CBIORCTUtil.h"

@implementation CBIORCTUtil

+(NSString *) remoteControl: (CBIORemoteControlState)state {
    switch(state) {
        case kCBIORemoteControlStateOn: return @"on";
        case kCBIORemoteControlStateOff: return @"off";
        case kCBIORemoteControlStateRejected: return @"rejected";
        case kCBIORemoteControlStateRequested: return @"requested";
        default: return @"off";
    }
}

+ (NSString *)fullDeviceState:(CBIOFullDeviceState)state {
    switch(state) {
        case kCBIOFullDeviceStateOn: return @"on";
        case kCBIOFullDeviceStateOff: return @"off";
        case kCBIOFullDeviceStateRejected: return @"rejected";
        case kCBIOFullDeviceStateRequested: return @"requested";
        default: return @"off";
    }
}

@end
