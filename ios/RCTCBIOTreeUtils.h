#import <Foundation/Foundation.h>

@interface RCTCBIOTreeUtils : NSObject

+(NSArray*) allParents: (UIView*) root;

+(NSArray*) reactParents: (UIView*) root;

+(NSMutableSet*) findAllClosest: (BOOL (^)(UIView* view))predicate under: (UIView*) root;

+(bool) isReactView: (UIView*) view;

+(bool) hasAnyParent: (UIView*) view matches: (NSSet*) matches;

@end
