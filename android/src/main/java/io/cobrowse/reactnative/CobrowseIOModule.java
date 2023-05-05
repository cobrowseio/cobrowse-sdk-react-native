package io.cobrowse.reactnative;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UnexpectedNativeTypeException;

import java.lang.reflect.Proxy;

import io.cobrowse.CobrowseAccessibilityService;
import io.cobrowse.CobrowseIO;
import io.cobrowse.Session;
import io.cobrowse.Session.FullDeviceState;

public class CobrowseIOModule extends ReactContextBaseJavaModule {

  private CobrowseIOCommonDelegates delegates;

  CobrowseIOModule(ReactApplicationContext reactContext) {
    super(reactContext);

    delegates = new CommonDelegates(reactContext);
    CobrowseIO.instance().setDelegate(delegates);
  }

  public static io.cobrowse.reactnative.CobrowseIO.Delegate delegate;

  @NonNull
  public String getName() {
    return "CobrowseIO";
  }

  @ReactMethod
  public void setUnredactedTags(final ReadableArray reactTags, final Promise promise) {
    delegates.setUnredactedTags(reactTags, promise);
  }

  @ReactMethod
  public void start() {
    delegates.findNodeManager();
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
  public void capabilities(ReadableArray capabilities) {
    String[] capabilitiesArray = new String[capabilities.size()];
    for (int i = 0; i < capabilities.size(); i++) {
      try {
        capabilitiesArray[i] = capabilities.getString(i);
      } catch (UnexpectedNativeTypeException e) {
        Log.w("CobrowseIO", "Failed to process capabilities: " + e.getMessage());
      }
    }
    CobrowseIO.instance().capabilities(capabilitiesArray);
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
  public void updateSession(final ReadableMap options, final Promise promise) {
    Handler handler = new Handler(getReactApplicationContext().getMainLooper());
    handler.post(new Runnable() {
      public void run() {
        Session current = CobrowseIO.instance().currentSession();

        if (current == null) {
          promise.resolve(null);
          return;
        }

        if (options.hasKey("full_device")) {
          try {
            String fullDeviceStr = options.getString("full_device");
            FullDeviceState fullDeviceState = Conversion.fullDeviceState(fullDeviceStr);

            current.setFullDeviceState(fullDeviceState, new io.cobrowse.Callback<Error, Session>() {
              @Override
              public void call(Error error, Session session) {
                if (error != null) promise.reject("cbio_full_device_failed", error);
                else promise.resolve(null);
              }
            });
          } catch (UnexpectedNativeTypeException ref) {
            boolean fullDeviceBool = options.getBoolean("full_device");

            current.setFullDevice(fullDeviceBool, new io.cobrowse.Callback<Error, Session>() {
              @Override
              public void call(Error error, Session session) {
                if (error != null) promise.reject("cbio_full_device_failed", error);
                else promise.resolve(null);
              }
            });
          }

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
  public void accessibilityServiceShowSetup(final Promise promise) {
    CobrowseAccessibilityService.showSetup(getReactApplicationContext());
    promise.resolve(null);
  }

  @ReactMethod
  public void accessibilityServiceIsRunning(final Promise promise) {
    promise.resolve(CobrowseAccessibilityService.isRunning(getReactApplicationContext()));
  }

  @ReactMethod
  public void overwriteSessionIndicator() {
    CobrowseIOCommonDelegates delegatesWithSessionControls = (CobrowseIOCommonDelegates) Proxy.newProxyInstance(
      CobrowseIOCommonDelegates.class.getClassLoader(),
      new Class[] { CobrowseIOCommonDelegates.class, CobrowseIO.SessionControlsDelegate.class },
      new DynamicSessionControlInvocationHandler(this.delegates));

    CobrowseIO.instance().setDelegate(delegatesWithSessionControls);
  }
}
