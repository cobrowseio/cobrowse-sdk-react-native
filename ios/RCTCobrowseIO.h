#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTUtils.h>

@import CobrowseIO;

@interface RCTCobrowseIO: RCTEventEmitter <RCTBridgeModule, CobrowseIODelegate>

@end
