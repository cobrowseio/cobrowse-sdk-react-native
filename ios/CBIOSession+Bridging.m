@import CobrowseIO;

@implementation CBIOSession (Bridging)

-(NSDictionary*) toDict {
    return @{
        @"id": self.id ? self.id : NSNull.null,
        @"code": self.code ? self.code : NSNull.null,
        @"state": self.state ? self.state : NSNull.null,
        @"approved": [NSNumber numberWithBool: self.isApproved],
        @"agent": self.hasAgent ? @{
            name: self.agent.name,
            id: self.agent.id
        } : NSNull.null
    };
}

@end
