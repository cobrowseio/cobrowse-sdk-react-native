package io.cobrowse.reactnative;

/**
 * This is a {@link NativeModule} that allows JS to use AcessToken in Facebook Android SDK.
 */
public class FBAccessTokenModule extends ReactContextBaseJavaModule{

    public FBAccessTokenModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public String getName() {
        return "FBAccessToken";
    }

    /**
     * Get {@link AccessToken} of the current session.
     * @param callback Use callback to pass the current access token back to JS.
     */
    @ReactMethod
    public void getCurrentAccessToken(Callback callback) {
        //Return the accessToken object as a ReactMap.
        callback.invoke(AccessToken.getCurrentAccessToken() == null
                ? null
                : Utility.accessTokenToReactMap(AccessToken.getCurrentAccessToken()));
    }

    /**
     * Set {@link AccessToken} for the current session.
     * @param accessTokenMap must satisfy the requirements in
     *                       <a href="https://developers.facebook.com/docs/reference/android/current/class/AccessToken/">
     *                       Facebook AccessToken</a>
     */
    @ReactMethod
    public void setCurrentAccessToken(ReadableMap accessTokenMap) {
        AccessToken accessToken = Utility.buildAccessToken(accessTokenMap);
        AccessToken.setCurrentAccessToken(accessToken);
    }

    /**
     * Updates the current access token with up to date permissions, and extends the expiration
     * date, if extension is possible.
     * @param promise use promise to pass result back to JS.
     */
    @ReactMethod
    public void refreshCurrentAccessTokenAsync(final Promise promise) {
        AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
            @Override
            public void OnTokenRefreshed(AccessToken accessToken) {
                promise.resolve(Utility.accessTokenToReactMap(accessToken));
            }

            @Override
            public void OnTokenRefreshFailed(FacebookException exception) {
                promise.reject(exception);
            }
        });
    }
}
