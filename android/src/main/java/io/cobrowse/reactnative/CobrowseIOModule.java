package io.cobrowse.reactnative;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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
import com.facebook.react.views.view.ReactViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.core.util.Predicate;
import io.cobrowse.CobrowseIO;
import io.cobrowse.Session;
import io.cobrowse.CobrowseAccessibilityService;

public class CobrowseIOModule extends ReactContextBaseJavaModule
  implements CobrowseIO.Delegate, CobrowseIO.SessionRequestDelegate,
    CobrowseIO.SessionLoadDelegate, CobrowseIO.RedactionDelegate,
    CobrowseIO.RemoteControlRequestDelegate {

    private static final String SESSION_LOADED = "session.loaded";
    private static final String SESSION_UPDATED = "session.updated";
    private static final String SESSION_ENDED = "session.ended";
    private static final String SESSION_REQUESTED = "session.requested";

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
    public void sessionDidLoad(@NonNull Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_LOADED, Conversion.convert(session));
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

    @Override
    public void handleRemoteControlRequest(@NonNull Activity activity, @NonNull Session session) {
        // no-op, this will be handed on the JS side via an "updated" event handler
        // this stub just disables the default native prompt in the SDK
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

    private Set<View> getUnredactedViews(@NonNull final Activity activity) {
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

    private Set<View> getRedactedViews(@NonNull final Activity activity) {
      HashSet<View> redacted = new HashSet<>();
      redacted.addAll(RedactedViewManager.redactedViews.keySet());
      return redacted;
    }

    @Override
    public List<View> redactedViews(@NonNull final Activity activity)  {
      final Set<View> redacted = this.getRedactedViews(activity);
      final Set<View> unredacted = this.getUnredactedViews(activity);

      // Project all unredactions to the root to get the full set of unredacted nodes
      HashSet<View> projectedUnredacted = new HashSet<>();
      for (View view : unredacted) {
          projectedUnredacted.add(view);
          projectedUnredacted.addAll(TreeUtils.allParents(view));
      }

      // Work out which nodes are explicitly redacted but need to be unredacted
      // due to a nested unredaction tag (these redactions will be moved towards the
      // leaves of the view hierarchy instead)
      HashSet<View> redactedProjectedUnredactions = new HashSet<>(redacted);
      redactedProjectedUnredactions.retainAll(projectedUnredacted);

      // Start to build the set of redactions we should actually apply. We start off with
      // the set of explicitly redacted nodes
      HashSet<View> toRedact = new HashSet<>(redacted);

      // remove any redactions that have unredacted decendants (i.e. any that appear in the
      // projection of the unredaction). These are the redacted nodes we need to move towards
      // the leaves of the tree
      toRedact.removeAll(redactedProjectedUnredactions);

      // Work out the set of all nodes that are siblings of any projected unredacted node
      HashSet<View> projectedUnredactionSiblings = new HashSet<>();
      for (View view : projectedUnredacted) {
          ViewParent parent = view.getParent();
          if (parent instanceof ViewGroup)
            projectedUnredactionSiblings.addAll(TreeUtils.directChildren((ViewGroup) parent));
      }

      // Subtract the projected unredactions from the set of all siblings of projected
      // unredactions. i.e. subtract things that should be definitely unredacted to leave
      // a set we're not sure yet whether to redact or not
      HashSet<View> potentiallyRedactedUnredactedSiblings = new HashSet<View>(projectedUnredactionSiblings);
      potentiallyRedactedUnredactedSiblings.removeAll(projectedUnredacted);

      // for each node we're not sure about yet, check if the closest redacted or unredacted ancestor
      // is a redacted node, if so this descendant should also be redacted
      for (View view : potentiallyRedactedUnredactedSiblings) {
      View closest = TreeUtils.closest(view, new Predicate<View>() {
          @Override
          public boolean test(View view) {
            return redacted.contains(view) || unredacted.contains(view);
          }
        });
        if (redacted.contains(closest)) toRedact.add(view);
      }

      // Remove any empty ViewGroup from the redacted set, they're often used for wrapping
      // or sizing other elements, and do not usually need to be redacted
      // If it's absolutely necessary they are redacted, they can always be replaced with
      // a <Redacted> tag instead
      for (View v : new HashSet<>(toRedact)) {
        if (v instanceof ViewGroup && ((ViewGroup) v).getChildCount() == 0) {
          toRedact.remove(v);
        }
      }

      return new ArrayList<>(toRedact);
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
    public void getSession(final String idOrCode, final Promise promise) {
        Handler handler = new Handler(getReactApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                CobrowseIO.instance().getSession(idOrCode, new io.cobrowse.Callback<Error, Session>() {
                    @Override
                    public void call(Error error, Session session) {
                        if (error != null) promise.reject("cbio_get_session_failed", error);
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

                    current.setRemoteControl(Conversion.remoteControl(remoteControl), new io.cobrowse.Callback<Error, Session>() {
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
