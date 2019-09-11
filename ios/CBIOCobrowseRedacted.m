#import "CBIOCobrowseRedacted.h"

@implementation CBIOCobrowseRedactedManager

RCT_EXPORT_MODULE(CBIOCobrowseRedacted)

+(NSMutableSet *)redactedViews {
    static NSMutableSet* views;
    if (!views) views = [NSMutableSet set];
    return views;
}

- (UIView *)view {
    UIView* view =  [[UIView alloc] init];
    [CBIOCobrowseRedactedManager.redactedViews addObject:view];
    return view;
}

@end
