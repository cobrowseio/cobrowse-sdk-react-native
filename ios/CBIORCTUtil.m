#import "CBIORCTUtil.h"

@implementation CBIORCTUtil

+(NSString *) remoteControl: (CBIORemoteControlState)state {
    switch(state) {
        case CBIORemoteControlStateOn: return @"on";
        case CBIORemoteControlStateOff: return @"off";
        case CBIORemoteControlStateRejected: return @"rejected";
        case CBIORemoteControlStateRequested: return @"requested";
        default: return @"off";
    }
}

+ (NSString *)fullDeviceState:(CBIOFullDeviceState)state {
    switch(state) {
        case CBIOFullDeviceStateOn: return @"on";
        case CBIOFullDeviceStateOff: return @"off";
        case CBIOFullDeviceStateRejected: return @"rejected";
        case CBIOFullDeviceStateRequested: return @"requested";
        default: return @"off";
    }
}

@end
