#import "RCTCBIOTreeUtils.h"

@implementation RCTCBIOTreeUtils

+(NSArray*) allParents: (UIView*) root {
    NSMutableArray* parents = [NSMutableArray array];
    UIView* target = root.superview;
    while (target) {
        [parents insertObject:target atIndex:0];
        target = target.superview;
    }
    return parents;
}

+(UIView*) closest: (BOOL (^)(UIView* view))predicate from: (UIView*) root {
    if (predicate(root)) return root;
    else if (!root.superview) return nil;
    else return [self closest:predicate from:root.superview];
}

@end
