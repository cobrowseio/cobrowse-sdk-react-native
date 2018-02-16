package io.cobrowse.reactnative;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FBSDKPackage implements ReactPackage {

    public static final String VERSION_TO_RELEASE = "ReactNative-v0.7.0";

    private CallbackManager mCallbackManager;
    public FBSDKPackage(CallbackManager callbackManager) {
        mCallbackManager = callbackManager;
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        InternalSettings.setCustomUserAgent(VERSION_TO_RELEASE);
        return Arrays.<NativeModule>asList(
                new FBAccessTokenModule(reactContext),
                new FBAppEventsLoggerModule(reactContext),
                new FBAppInviteDialogModule(reactContext, mCallbackManager),
                new FBGameRequestDialogModule(reactContext, mCallbackManager),
                new FBGraphRequestModule(reactContext),
                new FBLoginManagerModule(reactContext, mCallbackManager),
                new FBMessageDialogModule(reactContext, mCallbackManager),
                new FBShareAPIModule(reactContext),
                new FBShareDialogModule(reactContext, mCallbackManager)
        );
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(
                new FBLikeViewManager(),
                new FBLoginButtonManager(reactContext, mCallbackManager),
                new FBSendButtonManager(),
                new FBShareButtonManager()
        );
    }

    // Deprecated in RN 0.47.0
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }
}
