#import <React/RCTBridgeModule.h>
#import "CBIOSession+Bridging.h"
#import <React/RCTUtils.h>

@import CobrowseIO;

@interface RCTCobrowseIO: RCTEventEmitter <RCTBridgeModule>
@end

@implementation RCTCobrowseIO

RCT_EXPORT_MODULE();

- (instancetype)init {
    return self;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

-(NSArray<NSString *> *)supportedEvents {
    return @[@"activated", "updated", "ended"];
}

RCT_EXPORT_METHOD(createSession: (RCTResponseSenderBlock) callback) {
    [CobrowseIO.instance createSession:^(NSError *err, CBIOSession *session) {
        callback(@[err?@"Error":[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

RCT_EXPORT_METHOD(loadSession: (NSString*) idOrCode callback: (RCTResponseSenderBlock) callback) {
    [CobrowseIO.instance getSession:idOrCode callback:^(NSError *err, CBIOSession *session) {
        callback(@[err?@"Error":[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

RCT_EXPORT_METHOD(activateSession: (RCTResponseSenderBlock) callback) {
    CBIOSession* current = CobrowseIO.instance.currentSession;
    if (!current) callback(@[RCTMakeError(@"no current session", nil, nil)]);
    [current activate:^(NSError *err, CBIOSession *session) {
        callback(@[err?@"Error":[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

RCT_EXPORT_METHOD(endSession: (RCTResponseSenderBlock) callback) {
    CBIOSession* current = CobrowseIO.instance.currentSession;
    if (!current) callback(@[RCTMakeError(@"no current session", nil, nil)]);
    [current end:^(NSError *err, CBIOSession *session) {
        callback(@[err?@"Error":[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

@end
