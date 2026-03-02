#import <Foundation/Foundation.h>
#import "CobrowseSDK/CBIOTouch.h"
#import "CobrowseSDK/CBIOTouchEvent.h"

#if __has_include("RCTSurfaceTouchHandler.h")
  #import "RCTSurfaceTouchHandler.h"
#elif __has_include(<React/RCTSurfaceTouchHandler.h>)
  #import <React/RCTSurfaceTouchHandler.h>
#elif __has_include(<React-RCTFabric/RCTSurfaceTouchHandler.h>)
  #import <React-RCTFabric/RCTSurfaceTouchHandler.h>
#else
  #define CBIO_NO_SURFACE_TOUCH_HANDLER 1
#endif

#ifndef CBIO_NO_SURFACE_TOUCH_HANDLER

@interface RCTSurfaceTouchHandler (Touch)
@end

#endif
