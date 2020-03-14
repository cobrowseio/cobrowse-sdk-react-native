#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "CBIOSession+Bridging.h"
#import "CBIOCobrowseRedacted.h"
#import <React/RCTUtils.h>
#import <React/RCTView.h>
#import <React/RCTBridge.h>
#import "RCTCobrowseIO.h"

#define SESSION_UPDATED "session_updated"
#define SESSION_ENDED "session_ended"
#define SESSION_REQUESTED "session_requested"

@import CobrowseIO;

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

-(dispatch_queue_t) methodQueue {
  return dispatch_get_main_queue();
}

-(NSDictionary *)constantsToExport {
    return @{
        @"SESSION_UPDATED": @SESSION_UPDATED,
        @"SESSION_ENDED": @SESSION_ENDED,
        @"SESSION_REQUESTED": @SESSION_REQUESTED
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

-(void)cobrowseHandleSessionRequest:(CBIOSession *)session {
    if (hasListeners) [self sendEventWithName:@SESSION_REQUESTED body:[session toDict]];
}

-(NSArray<UIView *> *)cobrowseRedactedViewsForViewController:(UIViewController *)vc {
    return [CBIOCobrowseRedactedManager.redactedViews allObjects];
}

RCT_EXPORT_METHOD(start) {
    [CobrowseIO.instance start];
}

RCT_REMAP_METHOD(stop,
                 stopWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    [CobrowseIO.instance stop:^(NSError *err) {
        if (err) return reject(@"cbio_stop_failed", err.localizedDescription, err);
        else resolve(NSNull.null);
    }];
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
    CobrowseIO.instance.device.token = [token dataUsingEncoding:NSUTF16StringEncoding];
}

RCT_REMAP_METHOD(currentSession,
                 currentSessionWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    CBIOSession* session = CobrowseIO.instance.currentSession;
    NSDictionary* dict = [session toDict];
    resolve(dict);
}

RCT_REMAP_METHOD(createSession,
                 createSessionWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    [CobrowseIO.instance createSession:^(NSError *err, CBIOSession *session) {
        if (err) return reject(@"cbio_create_session_failed", err.localizedDescription, err);
        return resolve([session toDict]);
    }];
}

RCT_REMAP_METHOD(getSession,
                 getSessionWithIdOrCode: (NSString*) idOrCode
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    [CobrowseIO.instance getSession:idOrCode callback:^(NSError *err, CBIOSession *session) {
        if (err) return reject(@"cbio_load_session_failed", err.localizedDescription, err);
        return resolve([session toDict]);
    }];
}

RCT_REMAP_METHOD(activateSession,
                 activateSessionWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    CBIOSession* current = CobrowseIO.instance.currentSession;
    if (!current) return reject(@"cbio_activate_session_failed", @"There is no active session to activate", nil);
    [current activate:^(NSError *err, CBIOSession *session) {
        if (err) return reject(@"cbio_activate_session_failed", err.localizedDescription, err);
        return resolve([session toDict]);
    }];
}

RCT_REMAP_METHOD(endSession,
                 endSessionWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    CBIOSession* current = CobrowseIO.instance.currentSession;
    if (!current) return reject(@"cbio_end_session_failed", @"There is no active session to end", nil);
    [current end:^(NSError *err, CBIOSession *session) {
        if (err) return reject(@"cbio_end_session_failed", err.localizedDescription, err);
        return resolve([session toDict]);
    }];
}

@end
