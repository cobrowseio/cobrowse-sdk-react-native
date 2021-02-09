#import "CBIOCobrowseRedacted.h"
@import React;

@interface CBIORedactedView : RCTView
@end
@implementation CBIORedactedView
@end


@implementation CBIOCobrowseRedactedManager

RCT_EXPORT_MODULE(CBIOCobrowseRedacted)

+(NSMutableSet *)redactedViews {
    static NSMutableSet* views;
    if (!views) views = [NSMutableSet set];
    return views;
}

- (UIView *)view {
    UIView* view = [[CBIORedactedView alloc] init];
    [CBIOCobrowseRedactedManager.redactedViews addObject:view];
    return view;
}

@end
