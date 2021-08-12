#import "RCTCBIOTreeUtils.h"
#import <React/RCTRootView.h>

@implementation RCTCBIOTreeUtils

+(NSArray*) allParents: (UIView*) root until: (Class) type {
    NSMutableArray* parents = [NSMutableArray array];
    UIView* target = root;
    while (target.superview && ![target.superview isKindOfClass:type]) {
        [parents addObject:target.superview];
        target = target.superview;
    }
    return parents;
}

+(NSMutableSet*) findAllClosest: (Class) type under: (UIView*) root {
    NSMutableSet* found = [NSMutableSet set];
    if ([root isKindOfClass: type]) [found addObject:root];
    else {
        for (UIView* child in root.subviews) {
            [found addObjectsFromArray: [self findAllClosest:type under:child].allObjects];
        }
    }
    return found;
}

@end
