#import "RCTCBIOTreeUtils.h"
#import <React/RCTRootView.h>

@implementation RCTCBIOTreeUtils

+(NSArray*) allParents: (UIView*) root {
    NSMutableArray* parents = [NSMutableArray array];
    UIView* target = root;
    while (target.superview) {
        [parents addObject:target.superview];
        target = target.superview;
    }
    return parents;
}

@end
