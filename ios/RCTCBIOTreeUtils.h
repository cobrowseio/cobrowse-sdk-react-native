#import <Foundation/Foundation.h>

@interface RCTCBIOTreeUtils : NSObject

+(NSArray*) allParents: (UIView*) root;

+(UIView*) closest: (BOOL (^)(UIView* view))predicate from: (UIView*) root;

@end
