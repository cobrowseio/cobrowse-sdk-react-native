#import "CBIORCTUtil.h"
#import "CobrowseSDK/CBIOAgent.h"

@implementation CBIOSession (Bridging)

-(NSDictionary*) toDict {
    return @{
        @"id": self.id ? self.id : NSNull.null,
        @"code": self.code ? self.code : NSNull.null,
        @"state": self.state ? self.state : NSNull.null,
        @"full_device": [CBIORCTUtil fullDeviceState: self.fullDevice],
        @"remote_control": [CBIORCTUtil remoteControl: self.remoteControl],
        @"agent": self.hasAgent ? @{
            @"name": self.agent.name,
            @"id": self.agent.id
        } : NSNull.null
    };
}

@end
