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
#import <objc/runtime.h>

#define SESSION_LOADED "session.loaded"
#define SESSION_UPDATED "session.updated"
#define SESSION_ENDED "session.ended"
#define SESSION_REQUESTED "session.requested"

static id<RCTCobrowseIODelegate> _Nullable _delegate;

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

+(id<RCTCobrowseIODelegate>)delegate {
    return _delegate;
}

+(void)setDelegate:(id<RCTCobrowseIODelegate>)delegate {
    _delegate = delegate;
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

-(NSSet<UIView*>*) redactedViews: (UIViewController*) vc {
    NSMutableSet* views = [CBIOCobrowseRedactedManager.redactedViews mutableCopy];
    if ([RCTCobrowseIO.delegate respondsToSelector:@selector(cobrowseRedactedViewsForViewController:)]) {
        [views addObjectsFromArray: [RCTCobrowseIO.delegate cobrowseRedactedViewsForViewController:vc]];
    }
    return views;
}

-(NSSet<UIView*>*) unredactedViews: (UIViewController*) vc {
    NSMutableSet* views = [NSMutableSet set];
    @synchronized(unredactedTags) {
        for (id tag in unredactedTags) {
            UIView* v = [self.bridge.uiManager viewForReactTag: tag];
            if (v != nil) [views addObject:v];
        }
    }
    if ([RCTCobrowseIO.delegate respondsToSelector:@selector(cobrowseUnredactedViewsForViewController:)]) {
        [views addObjectsFromArray: [RCTCobrowseIO.delegate cobrowseUnredactedViewsForViewController:vc]];
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
    NSSet* redacted = [self redactedViews: vc];
    NSSet* unredacted = [self unredactedViews: vc];
    
    // Project all unredactions to the root to get the full set of unredacted nodes
    NSMutableSet* projectedUnredacted = [NSMutableSet set];
    for (id view in unredacted) {
        [projectedUnredacted addObject:view];
        [projectedUnredacted addObjectsFromArray: [RCTCBIOTreeUtils allParents: view]];
    }
    
    // Work out which nodes are explicitly redacted but need to be unredacted
    // due to a nested unredaction tag (these redactions will be moved towards the
    // leaves of the view hierarchy instead)
    NSMutableSet* redactedProjectedUnredactions = [redacted mutableCopy];
    [redactedProjectedUnredactions intersectSet: projectedUnredacted];
    
    // Start to build the set of redactions we should actually apply. We start off with
    // the set of explicitly redacted nodes
    NSMutableSet* toRedact = [redacted mutableCopy];
    
    // remove any redactions that have unredacted decendants (i.e. any that appear in the
    // projection of the unredaction). These are the redacted nodes we need to move towards
    // the leaves of the tree
    [toRedact minusSet: redactedProjectedUnredactions];
    
    // Work out the set of all nodes that are siblings of any projected unredacted node
    NSMutableSet* projectedUnredactionSiblings = [NSMutableSet set];
    for (UIView* view in projectedUnredacted) {
        [projectedUnredactionSiblings addObjectsFromArray: view.superview.subviews];
    }
    
    // Subtract the projected unredactions from the set of all siblings of projected
    // unredactions. i.e. subtract things that should be definitely unredacted to leave
    // a set we're not sure yet whether to redact or not
    NSMutableSet* potentiallyRedactedUnredactedSiblings = [projectedUnredactionSiblings mutableCopy];
    [potentiallyRedactedUnredactedSiblings minusSet:projectedUnredacted];
    
    // for each node we're not sure about yet, check if the closest redacted or unredacted ancestor
    // is a redacted node, if so this descendant should also be redacted
    for (UIView* view in potentiallyRedactedUnredactedSiblings) {
        UIView* closest = [RCTCBIOTreeUtils closest:^BOOL(UIView *v) {
            return [redacted containsObject:v] || [unredacted containsObject:v];
        } from: view];
        if ([redacted containsObject: closest]) [toRedact addObject: view];
    }
    
    // Remove any empty RCTViews from the redacted set, they're often used for wrapping
    // or sizing other elements, and do not usually need to be redacted
    // If it's absolutely necessary they are redacted, they can always be replaced with
    // a <Redacted> tag instead
    for (UIView* v in [toRedact copy])
        if ([v isKindOfClass:RCTView.class] && v.subviews.count == 0) [toRedact removeObject: v];
    
    return toRedact.allObjects;
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

void handleFullDeviceOverwrite(id self, SEL _cmd, CBIOSession* session) {
    // no-op function which will overwrite the default full device UI when enabled so
    // it can be handled on RN side
}

RCT_EXPORT_METHOD(overwriteFullControlUI) {
    BOOL success = class_addMethod([self class], @selector(cobrowseHandleFullDeviceRequest:), (IMP) handleFullDeviceOverwrite, "v@:");
}

@end
