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

+(NSMutableSet*) findAllClosest: (BOOL (^)(UIView* view))predicate under: (UIView*) root {
    NSMutableSet* found = [NSMutableSet set];
    if (predicate(root)) [found addObject:root];
    else {
        for (UIView* child in root.subviews) {
            [found addObjectsFromArray: [self findAllClosest:predicate under:child].allObjects];
        }
    }
    return found;
}

@end
