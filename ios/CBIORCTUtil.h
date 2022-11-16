#import <Foundation/Foundation.h>
@import CobrowseIO;

@interface CBIORCTUtil : NSObject

+(NSString*) remoteControl: (CBIORemoteControlState) state;
+(NSString*) fullDeviceState: (CBIOFullDeviceState) state;

@end
