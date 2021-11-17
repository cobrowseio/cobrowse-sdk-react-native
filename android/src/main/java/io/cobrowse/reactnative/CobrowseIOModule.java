package io.cobrowse.reactnative;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.cobrowse.CobrowseIO;
import io.cobrowse.Session;
import io.cobrowse.CobrowseAccessibilityService;

public class CobrowseIOModule extends ReactContextBaseJavaModule
  implements CobrowseIO.Delegate, CobrowseIO.SessionRequestDelegate,
    CobrowseIO.RedactionDelegate, CobrowseIO.RemoteControlRequestDelegate {

    private static final String SESSION_UPDATED = "session.updated";
    private static final String SESSION_ENDED = "session.ended";
    private static final String SESSION_REQUESTED = "session.requested";

    CobrowseIOModule(ReactApplicationContext reactContext) {
        super(reactContext);
        CobrowseIO.instance().setDelegate(this);
    }

    @NonNull
    public String getName() {
        return "CobrowseIO";
    }

    @Override
    public void sessionDidUpdate(@NonNull Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_UPDATED, Utility.convert(session));
    }

    @Override
    public void sessionDidEnd(@NonNull Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_ENDED, Utility.convert(session));
    }

    @Override
    public void handleSessionRequest(@NonNull Activity activity, @NonNull Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_REQUESTED, Utility.convert(session));
    }

    @Override
    public void handleRemoteControlRequest(@NonNull Activity activity, @NonNull Session session) {
      // no-op, this will be handed on the JS side via an "updated" event handler
      // this stub just disables the default native prompt in the SDK
    }

    @Override
    public List<View> redactedViews(@NonNull final Activity activity) {
        return new ArrayList<>(RedactedViewManager.redactedViews.keySet());
    }

    @ReactMethod
    public void start() {
        final Activity activity = getReactApplicationContext().getCurrentActivity();
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CobrowseIO.instance().start(activity);
                }
            });
        else
            Log.w("CobrowseIO", "Activity was null during start() call.");
    }

    @ReactMethod
    public void stop() {
        CobrowseIO.instance().stop();
    }

    @ReactMethod
    public void api(String api) {
        CobrowseIO.instance().api(api);
    }

    @ReactMethod
    public void license(String license) {
        CobrowseIO.instance().license(license);
    }

    @ReactMethod
    public void customData(ReadableMap customData) {
        CobrowseIO.instance().customData(customData.toHashMap());
    }

    @ReactMethod
    public void deviceToken(String token) {
        if (getReactApplicationContext().getCurrentActivity() != null)
            CobrowseIO.instance().setDeviceToken(
                getReactApplicationContext().getCurrentActivity().getApplication(),
                token);
    }

    @ReactMethod
    public void currentSession(final Promise promise) {
        promise.resolve(Utility.convert(CobrowseIO.instance().currentSession()));
    }

    @ReactMethod
    public void createSession(final Promise promise) {
        Handler handler = new Handler(getReactApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                CobrowseIO.instance().createSession(new io.cobrowse.Callback<Error, Session>() {
                    @Override
                    public void call(Error error, Session session) {
                        if (error != null) promise.reject("cbio_create_session_failed", error);
                        else promise.resolve(Utility.convert(session));
                    }
                });
            }
        });
    }

    @ReactMethod
    public void getSession(final String idOrCode, final Promise promise) {
        Handler handler = new Handler(getReactApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                CobrowseIO.instance().getSession(idOrCode, new io.cobrowse.Callback<Error, Session>() {
                    @Override
                    public void call(Error error, Session session) {
                        if (error != null) promise.reject("cbio_get_session_failed", error);
                        else promise.resolve(Utility.convert(session));
                    }
                });
            }
        });
    }

    @ReactMethod
    public void activateSession(final Promise promise) {
        Handler handler = new Handler(getReactApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Session current = CobrowseIO.instance().currentSession();
                if (current == null) {
                    promise.reject("no current session", new Error("No session"));
                    return;
                }
                current.activate(new io.cobrowse.Callback<Error, Session>() {
                    @Override
                    public void call(Error error, Session session) {
                        if (error != null) promise.reject("cbio_activate_session_failed", error);
                        else promise.resolve(Utility.convert(session));
                    }
                });
            }
        });
    }

    @ReactMethod
    public void endSession(final Promise promise) {
        Handler handler = new Handler(getReactApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Session current = CobrowseIO.instance().currentSession();
                if (current == null) {
                    promise.resolve(null);
                    return;
                }
                current.end(new io.cobrowse.Callback<Error, Session>() {
                    @Override
                    public void call(Error error, Session session) {
                        if (error != null) promise.reject("cbio_end_session_failed", error);
                        else promise.resolve(null);
                    }
                });
            }
        });
    }

    @ReactMethod
    public void updateSession (final ReadableMap options, final Promise promise) {
        Handler handler = new Handler(getReactApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Session current = CobrowseIO.instance().currentSession();

                if (current == null) {
                    promise.resolve(null);
                    return;
                }

                if (options.hasKey("full_device")) {
                    boolean fullDevice = options.getBoolean("full_device");

                    current.setFullDevice(fullDevice, new io.cobrowse.Callback<Error, Session>() {
                        @Override
                        public void call(Error error, Session session) {
                            if (error != null) promise.reject("cbio_full_device_failed", error);
                            else promise.resolve(null);
                        }
                    });

                    return;
                }

                if (options.hasKey("remote_control")) {
                    String remoteControl = options.getString("remote_control");

                    current.setRemoteControl(Utility.remoteControl(remoteControl), new io.cobrowse.Callback<Error, Session>() {
                        @Override
                        public void call(Error error, Session session) {
                            if (error != null) promise.reject("cbio_remote_control_failed", error);
                            else promise.resolve(null);
                        }
                    });
                }
            }
        });
    }

    @ReactMethod
    public void accessibilityServiceShowSetup (final Promise promise) {
        CobrowseAccessibilityService.showSetup(getReactApplicationContext());
        promise.resolve(null);
    }

    @ReactMethod
    public void accessibilityServiceIsRunning(final Promise promise) {
        promise.resolve(CobrowseAccessibilityService.isRunning(getReactApplicationContext()));
    }
}
