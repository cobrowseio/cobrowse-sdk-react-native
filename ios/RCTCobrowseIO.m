#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "CBIOSession+Bridging.h"
#import <React/RCTUtils.h>

@import CobrowseIO;

@interface RCTCobrowseIO: RCTEventEmitter <RCTBridgeModule, CobrowseIODelegate>
@end

@implementation RCTCobrowseIO

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

-(NSArray<NSString *> *)supportedEvents {
    return @[@"updated", @"ended"];
}

-(void)cobrowseSessionDidUpdate:(CBIOSession *)session {
    NSLog(@"session did update %@", session);
    [self sendEventWithName:@"updated" body:[session toDict]];
}

-(void)cobrowseSessionDidEnd:(CBIOSession *)session {
    NSLog(@"session did end %@", session);
    [self sendEventWithName:@"ended" body:[session toDict]];
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
