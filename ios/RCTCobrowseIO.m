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

#define SESSION_LOADED "session.loaded"
#define SESSION_UPDATED "session.updated"
#define SESSION_ENDED "session.ended"
#define SESSION_REQUESTED "session.requested"

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

-(void)startObserving {
    hasListeners = YES;
}

-(void)stopObserving {
    hasListeners = NO;
}

-(NSArray<NSString *> *)supportedEvents {
    return @[@SESSION_LOADED, @SESSION_UPDATED, @SESSION_ENDED, @SESSION_REQUESTED];
}

-(void)cobrowseSessionDidLoad:(CBIOSession *)session {
    if (hasListeners) [self sendEventWithName:@SESSION_LOADED body:[session toDict]];
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

- (void)cobrowseHandleRemoteControlRequest:(CBIOSession *)session {
    // no-op, this will be handed on the JS side via an "updated" event handler
    // this stub just disables the default native prompt in the SDK
}

-(NSArray<UIView *> *)cobrowseRedactedViewsForViewController:(UIViewController *)vc {
    NSMutableSet* redacted = [NSMutableSet set];
    NSSet* unredacted = self.unredactedViews;

    // By default everything managed by react is redacted for view controllers
    // that contain react views. If we were to always redact vc.view this would lead
    // to instances where windows that do not contain a RN context could
    // not be unredacted.
    // A simple example of this is the overlay window that cobrowse adds to
    // render its annotations. This window sits on top of all the other windows
    // and would always be redacted, effecivley redacting the entire screen all
    // the time.
    // To get around this, we only redact views below RCTViews or RCTRootViews
    // (not inclusive), as then it's always possible to add an unredact()'ed component
    // around a subview in the react tree to make parent visible.
    NSSet* rootViews = [RCTCBIOTreeUtils findAllClosest:^BOOL(UIView *view) {
        return [RCTCBIOTreeUtils isReactView: view];
    } under: vc.view];
    for (UIView* v in rootViews) [redacted addObjectsFromArray: v.subviews];

    // Now we can actually start working out what should be unredacted
    // First work out the set of all parents of unredact()'ed nodes
    // that are inside a react view (ignoring any nested unredaction)
    NSMutableSet* unredactedParents = [NSMutableSet set];
    for (id view in unredacted) {
        if (![RCTCBIOTreeUtils hasAnyParent:view matches:unredacted])
            [unredactedParents addObjectsFromArray: [RCTCBIOTreeUtils reactParents: view]];
    }
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
    for (UIView* v in unredacted) [redacted removeObject:v];

    // Remove any empty RCTViews from the redacted set, they're often used for wrapping
    // or sizing other elements, and do not usually need to be redacted
    // If it's absolutely necessary they are redacted, they can always be replaced with
    // a <Redacted> tag instead
    for (UIView* v in [redacted copy])
        if ([v isKindOfClass:RCTView.class] && v.subviews.count == 0) [redacted removeObject: v];

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

-(void) forceRedactionUpdate {
    // TODO: expose an API for forcing redaction updates?
    static UIView* v;
    if (!v) v = [[UIView alloc] init];
    [UIApplication.sharedApplication.keyWindow addSubview: v];
    [v removeFromSuperview];
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
    [self forceRedactionUpdate];
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
        if (err) return reject(@"cbio_get_session_failed", err.localizedDescription, err);
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

RCT_REMAP_METHOD(updateSession,
                 updaetSession: (NSDictionary*) state
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    CBIOSession* current = CobrowseIO.instance.currentSession;
    if (!current) return resolve(nil);
    [current update: state callback: ^(NSError *err, CBIOSession *session) {
        if (err) return reject(@"cbio_update_session_failed", err.localizedDescription, err);
        return resolve([session toDict]);
    }];
}

@end
