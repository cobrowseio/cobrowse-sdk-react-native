#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "CBIOSession+Bridging.h"
#import <React/RCTUtils.h>

#define SESSION_UPDATED "session_updated"
#define SESSION_ENDED "session_ended"

@import CobrowseIO;

@interface RCTCobrowseIO: RCTEventEmitter <RCTBridgeModule, CobrowseIODelegate>
@end 

@implementation RCTCobrowseIO {
    bool hasListeners;
}

RCT_EXPORT_MODULE();

- (instancetype)init {
    self = [super init];
    if (self) {
        [CobrowseIO.instance setDelegate:self];
    }
    return self;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

-(NSDictionary *)constantsToExport {
    return @{
        @"SESSION_UPDATED": @SESSION_UPDATED,
        @"SESSION_ENDED": @SESSION_ENDED
    };
}

-(void)startObserving {
    hasListeners = YES;
}

-(void)stopObserving {
    hasListeners = NO;
}

-(NSArray<NSString *> *)supportedEvents {
    return self.constantsToExport.allValues;
}

-(void)cobrowseSessionDidUpdate:(CBIOSession *)session {
    if (hasListeners) [self sendEventWithName:@SESSION_UPDATED body:[session toDict]];
}

-(void)cobrowseSessionDidEnd:(CBIOSession *)session {
    if (hasListeners) [self sendEventWithName:@SESSION_ENDED body:[session toDict]];
}

RCT_EXPORT_METHOD(license: (NSString*) license) {
    CobrowseIO.instance.license = license;
}

RCT_EXPORT_METHOD(api: (NSString*) api) {
    CobrowseIO.instance.api = api;
}

RCT_EXPORT_METHOD(customData: (NSDictionary*) customData) {
    CobrowseIO.instance.customData = customData;
}

RCT_EXPORT_METHOD(deviceToken: (NSString*) token) {
    CobrowseIO.instance.device.token = token;
}

RCT_EXPORT_METHOD(currentSession: (RCTResponseSenderBlock) callback) {
    CBIOSession* session = CobrowseIO.instance.currentSession;
    callback(@[[NSNull null], session ? [session toDict] : [NSNull null]]);
}

RCT_EXPORT_METHOD(createSession: (RCTResponseSenderBlock) callback) {
    [CobrowseIO.instance createSession:^(NSError *err, CBIOSession *session) {
        callback(@[err?err.localizedDescription:[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

RCT_EXPORT_METHOD(loadSession: (NSString*) idOrCode callback: (RCTResponseSenderBlock) callback) {
    [CobrowseIO.instance getSession:idOrCode callback:^(NSError *err, CBIOSession *session) {
        callback(@[err?err.localizedDescription:[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

RCT_EXPORT_METHOD(activateSession: (RCTResponseSenderBlock) callback) {
    CBIOSession* current = CobrowseIO.instance.currentSession;
    if (!current) callback(@[RCTMakeError(@"no current session", nil, nil)]);
    [current activate:^(NSError *err, CBIOSession *session) {
        callback(@[err?err.localizedDescription:[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

RCT_EXPORT_METHOD(endSession: (RCTResponseSenderBlock) callback) {
    CBIOSession* current = CobrowseIO.instance.currentSession;
    if (!current) callback(@[RCTMakeError(@"no current session", nil, nil)]);
    [current end:^(NSError *err, CBIOSession *session) {
        callback(@[err?err.localizedDescription:[NSNull null], session ? [session toDict] : [NSNull null]]);
    }];
}

@end
