package io.cobrowse.reactnative;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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
import java.util.Set;

import androidx.annotation.NonNull;
import io.cobrowse.CobrowseIO;
import io.cobrowse.Session;

public class CobrowseIOModule extends ReactContextBaseJavaModule implements CobrowseIO.Delegate, CobrowseIO.SessionRequestDelegate, CobrowseIO.RedactionDelegate {

    private static final String SESSION_UPDATED = "session_updated";
    private static final String SESSION_ENDED = "session_ended";
    private static final String SESSION_REQUESTED = "session_requested";

    private final HashSet<Integer> unredactedTags = new HashSet<>();
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
                .emit(SESSION_UPDATED, Conversion.convert(session));
    }

    @Override
    public void sessionDidEnd(@NonNull Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_ENDED, Conversion.convert(session));
    }

    @Override
    public void handleSessionRequest(@NonNull Activity activity, @NonNull Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_REQUESTED, Conversion.convert(session));
    }

    @ReactMethod
    public void setUnredactedTags(final ReadableArray reactTags, final Promise promise) {
        synchronized (unredactedTags) {
            unredactedTags.clear();
            for (int i = 0; i < reactTags.size(); i++)
                unredactedTags.add(reactTags.getInt(i));
            promise.resolve(null);
        }
    }

    private Set<View> unredactedViews() {
        synchronized (unredactedTags) {
            HashSet<View> unredacted = new HashSet<>();
            for (Integer i : unredactedTags) {
                try {
                   unredacted.add(nodeManager.resolveView(i));
                } catch (Exception e) {
                   Log.i("CobrowseIO", "Failed to find unredacted view for tag " + i + ", error = " + e.getMessage());
                }
            }
            return unredacted;
        }
    }

    @Override
    public List<View> redactedViews(@NonNull final Activity activity) {
        HashSet<View> redacted = new HashSet<>();
        // By default everything is redacted from the DecorView down in
        // each activity
        redacted.add(activity.getWindow().getDecorView());

        // Now we can actually start working out what should be unredacted
        // First work out the set of all parents of unredact()'ed nodes
        HashSet<View> unredactedParents = new HashSet<>();
        for (View unredacted : unredactedViews()) {
            unredactedParents.addAll(TreeUtils.allParents(unredacted));
        }

        // Then work out the set of all direct children of any unredacted parents
        // This should give us the set including unredacted nodes, their siblings,
        // and all their parents.
        for (View parent : unredactedParents) {
            redacted.addAll(TreeUtils.directChildren(parent));
        }

        // Then we can subtract the set of unredacted parents to find just the
        // set of unredacted nodes that are leaves of the parent subtree
        redacted.removeAll(unredactedParents);

        // Finally we can subtract the set of unredacted views to get the minimal
        // set of redactions that will redact everything that's not explicitly unredacted
        // whilst allowing the unredacted views to be visible
        redacted.removeAll(unredactedViews());

        // Any explicitly redacted views surroudned by <Redacted> tags take precedence, so
        // re-add any tagged as such that the process above might have removed
        redacted.addAll(RedactedViewManager.redactedViews.keySet());

        return new ArrayList<>(redacted);
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
        promise.resolve(Conversion.convert(CobrowseIO.instance().currentSession()));
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
                        else promise.resolve(Conversion.convert(session));
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
                        else promise.resolve(Conversion.convert(session));
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
                        else promise.resolve(Conversion.convert(session));
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
