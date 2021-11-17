@import CobrowseIO;

@interface CBIOSession (Bridging)

-(NSDictionary*) toDict;

@end

@interface CBIOSession (Protected)

-(void) update:(NSDictionary*) state callback: (CBErrorSessionBlock)callback;

@end
