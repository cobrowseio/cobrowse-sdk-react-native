#import <Foundation/Foundation.h>
#import "CobrowseIO/CBIOSession.h"

@interface CBIORCTUtil : NSObject

+(NSString*) remoteControl: (CBIORemoteControlState) state;
+(NSString*) fullDeviceState: (CBIOFullDeviceState) state;

@end
