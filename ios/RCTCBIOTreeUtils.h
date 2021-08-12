#import <Foundation/Foundation.h>

@interface RCTCBIOTreeUtils : NSObject

+(NSArray*) allParents: (UIView*) root until: (Class) type;

+(NSMutableSet*) findAllClosest: (Class) type under: (UIView*) root;

@end
