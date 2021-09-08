#import <Foundation/Foundation.h>

@interface RCTCBIOTreeUtils : NSObject

+(NSArray*) allParents: (UIView*) root;

+(NSMutableSet*) findAllClosest: (BOOL (^)(UIView* view))predicate under: (UIView*) root;

@end
