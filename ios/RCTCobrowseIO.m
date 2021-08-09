#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "CBIOSession+Bridging.h"
#import "CBIOCobrowseRedacted.h"
#import <React/RCTUtils.h>
#import <React/RCTView.h>
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import "RCTCobrowseIO.h"
#import "RCTCBIOTreeUtils.h"

#define SESSION_UPDATED "session_updated"
#define SESSION_ENDED "session_ended"
#define SESSION_REQUESTED "session_requested"

@import CobrowseIO;

@implementation RCTCobrowseIO {
    bool hasListeners;
    NSMutableSet* unredactedTags;
}

RCT_EXPORT_MODULE();

- (instancetype)init {
    self = [super init];
    if (self) {
        [CobrowseIO.instance setDelegate:self];
        unredactedTags = [NSMutableSet set];
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

-(NSSet<UIView*>*) unredactedViews {
    NSMutableSet* views = [NSMutableSet set];
    @synchronized(unredactedTags) {
        for (id tag in unredactedTags) {
            UIView* v = [self.bridge.uiManager viewForReactTag: tag];
            if (v != nil) [views addObject:v];
        }
    }
    return views;
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
    NSMutableSet* redacted = [NSMutableSet set];
    NSSet* unredacted = self.unredactedViews;
    
    // By default everything is redacted for view controllers that contain a
    // react root view. We look for a RCTRootView as if we were to always
    // redact vc.view this would lead to isntances where windows not managed by
    // react native could not be unredacted. A simple example of this is the
    // overlay window that cobrowse adds to render its annotations. This window
    // sits on top of all the other windows and would always be redacted,
    // effecivley redacting the entire screen all the time.
    // TODO: is this broad enough? What if RCTRootView is a child of vc.view?
    if ([vc.view isKindOfClass:RCTRootView.class]) [redacted addObject: vc.view];

    // Now we can actually start working out what should be unredacted
    // First work out the set of all parents of unredact()'ed nodes
    NSMutableSet* unredactedParents = [NSMutableSet set];
    for (id view in unredacted)
        [unredactedParents addObjectsFromArray: [RCTCBIOTreeUtils allParents: view]];

    // Then work out the set of all direct children of any unredacted parents
    // This should give us the set including unredacted nodes, their siblings,
    // and all their parents.
    for (UIView* parent in unredactedParents) [redacted addObjectsFromArray: parent.subviews];

    // Then we can subtract the set of unredacted parents to find just the
    // set of unredacted nodes that are leaves of the parent subtree
    for (id v in unredactedParents) [redacted removeObject:v];

    // Finally we can subtract the set of unredacted views to get the minimal
    // set of redactions that will redact everything that's not explicitly unredacted
    // whilst allowing the unredacted views to be visible
    for (id v in unredacted) [redacted removeObject:v];

    // Any explicitly redacted views surroudned by <Redacted> tags take precedence, so
    // re-add any tagged as such that the process above might have removed
    for (id v in CBIOCobrowseRedactedManager.redactedViews.allObjects) [redacted addObject: v];
    
    return redacted.allObjects;;
}

- (bool) cobrowseShouldCaptureWindow:(UIWindow *)window {
    if ([NSStringFromClass(window.class) containsString:@"RemoteKeyboard"]) {
        return false;
    } else  {
        return true;
    }
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

RCT_REMAP_METHOD(setUnredactedTags,
                 setUnredactedTags: (NSArray*) reactTags
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    @synchronized(unredactedTags) {
        [unredactedTags removeAllObjects];
        [unredactedTags addObjectsFromArray:reactTags];
    }
    [UIApplication.sharedApplication.keyWindow setNeedsDisplay];
    resolve(nil);
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
    if (!current) return resolve(nil);
    [current end:^(NSError *err, CBIOSession *session) {
        if (err) return reject(@"cbio_end_session_failed", err.localizedDescription, err);
        return resolve([session toDict]);
    }];
}

@end
