package io.cobrowse.reactnative;

import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

import io.cobrowse.core.CobrowseIO;
import io.cobrowse.core.Session;
import io.cobrowse.core.KeyEvent;
import io.cobrowse.core.Touch;

public class CobrowseIOModule extends ReactContextBaseJavaModule implements Session.Delegate {

    public static final String SESSION_UPDATED = "session_updated";
    public static final String SESSION_ENDED = "session_ended";

    CobrowseIOModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private void init() {
        CobrowseIO.instance().setDelegate(this);
    }

    public String getName() {
        return "CobrowseIO";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("SESSION_UPDATED", SESSION_UPDATED);
        constants.put("SESSION_ENDED", SESSION_ENDED);
        return constants;
    }

    @Override
    public void sessionDidUpdate(Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_UPDATED, Utility.convert(session));
    }

    @Override
    public void sessionDidEnd(Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(SESSION_ENDED, Utility.convert(session));
    }

    @Override
    public boolean shouldAllowKeyEvent(KeyEvent event, Session session) {
        return true;
    }

    @Override
    public boolean shouldAllowTouch(Touch event, Session session) {
        return true;
    }

    @ReactMethod
    public void start() {
        CobrowseIO.instance().start(getReactApplicationContext().getCurrentActivity());
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
    public void currentSession(final Callback callback) {
        callback.invoke(null, Utility.convert(CobrowseIO.instance().currentSession()));
    }

    @ReactMethod
    public void createSession(final Callback callback) {
        init();
        CobrowseIO.instance().createSession(new io.cobrowse.core.Callback<Error, Session>() {
            @Override
            public void call(Error error, Session session) {
                callback.invoke(Utility.convert(error), Utility.convert(session));
            }
        });
    }

    @ReactMethod
    public void loadSession(String idOrCode, final Callback callback) {
        init();
        CobrowseIO.instance().getSession(idOrCode, new io.cobrowse.core.Callback<Error, Session>() {
            @Override
            public void call(Error error, Session session) {
                callback.invoke(Utility.convert(error), Utility.convert(session));
            }
        });
    }

    @ReactMethod
    public void activateSession(final Callback callback) {
        Session current = CobrowseIO.instance().currentSession();
        if (current == null) {
            callback.invoke("no current session");
            return;
        }
        current.activate(new io.cobrowse.core.Callback<Error, Session>() {
            @Override
            public void call(Error error, Session session) {
                callback.invoke(Utility.convert(error), Utility.convert(session));
            }
        });
    }

    @ReactMethod
    public void endSession(final Callback callback) {
        Session current = CobrowseIO.instance().currentSession();
        if (current == null) {
            callback.invoke("no current session");
            return;
        }
        current.end(new io.cobrowse.core.Callback<Error, Session>() {
            @Override
            public void call(Error error, Session session) {
                callback.invoke(Utility.convert(error), Utility.convert(session));
            }
        });
    }

}
