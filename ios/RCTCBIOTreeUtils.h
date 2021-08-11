#import <Foundation/Foundation.h>

@interface RCTCBIOTreeUtils : NSObject

+(NSArray*) allParents: (UIView*) root;

+(NSMutableSet*) findAllClosest: (Class) type under: (UIView*) root;

@end
