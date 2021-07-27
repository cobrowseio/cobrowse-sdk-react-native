package io.cobrowse.reactnative;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.cobrowse.CobrowseIO;
import io.cobrowse.Session;

public class CobrowseIOModule extends ReactContextBaseJavaModule implements CobrowseIO.Delegate, CobrowseIO.SessionRequestDelegate, CobrowseIO.RedactionDelegate {

    private static final String SESSION_UPDATED = "session_updated";
    private static final String SESSION_ENDED = "session_ended";
    private static final String SESSION_REQUESTED = "session_requested";

    private final HashSet<Integer> redactedTags = new HashSet<>();
    private NativeViewHierarchyManager nodeManager;

    CobrowseIOModule(ReactApplicationContext reactContext) {
        super(reactContext);
        CobrowseIO.instance().setDelegate(this);
    }

    private void findNodeManager() {
        if (nodeManager != null) return;
        final UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
        assert uiManager != null;
        uiManager.prependUIBlock(new UIBlock() {
            @Override
            public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {
                nodeManager = nativeViewHierarchyManager;
            }
        });
    }

    @NonNull
    public String getName() {
        return "CobrowseIO";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("SESSION_UPDATED", SESSION_UPDATED);
        constants.put("SESSION_ENDED", SESSION_ENDED);
        constants.put("SESSION_REQUESTED", SESSION_REQUESTED);
        return constants;
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

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void setRedactedTagsSync(final ReadableArray reactTags) {
        synchronized (redactedTags) {
            redactedTags.clear();
            for (int i = 0; i < reactTags.size(); i++)
                redactedTags.add(reactTags.getInt(i));
        }
    }

    @ReactMethod
    public void setRedactedTags(final ReadableArray reactTags) {
        setRedactedTagsSync(reactTags);
    }

    private static String getNativeId(View view) {
        Object tag = view.getTag(R.id.view_tag_native_id);
        return tag instanceof String ? (String) tag : null;
    }

    private static void findAllViews(View root, String nativeId, List<View> collector) {
        String tag = getNativeId(root);
        if (tag != null && tag.equals(nativeId)) {
            collector.add(root);
        }

        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findAllViews(viewGroup.getChildAt(i), nativeId, collector);
            }
        }
    }

    @Override
    public List<View> redactedViews(@NonNull final Activity activity) {
        synchronized (redactedTags) {
            List<View> views = new ArrayList<>();

            // try out nativeID method of redaction
            findAllViews(activity.getWindow().getDecorView().getRootView(), "cobrowse-redacted", views);

            // try out _nativeTag method of redaction
            for (Integer i : redactedTags) {
                try {
                    views.add(nodeManager.resolveView(i));
                } catch (Exception e) {
                    Log.i("CobrowseIO", "Failed to find redacted view for tag " + i + ", error = " + e.getMessage());
                }
            }
            return views;
        }
    }

    @ReactMethod
    public void start() {
        findNodeManager();
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
    public void loadSession(final String idOrCode, final Promise promise) {
        Handler handler = new Handler(getReactApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                CobrowseIO.instance().getSession(idOrCode, new io.cobrowse.Callback<Error, Session>() {
                    @Override
                    public void call(Error error, Session session) {
                        if (error != null) promise.reject("cbio_load_session_failed", error);
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

}
