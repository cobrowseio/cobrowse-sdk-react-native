#import "RCTCBIOTreeUtils.h"
#import <React/RCTRootView.h>
#import <React/RCTView.h>

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

+(NSArray*) reactParents: (UIView*) root {
    NSArray* allParents = [self allParents: root];
    NSMutableArray* reactParents = [allParents mutableCopy];
    for (id view in allParents) {
        if ([self isReactView:view]) break;
        [reactParents removeObject: view];
    }
    return reactParents;
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

+(bool) isReactView: (UIView*) view {
    return [view isKindOfClass:RCTRootView.class] || [view isKindOfClass: RCTView.class];
}

@end
