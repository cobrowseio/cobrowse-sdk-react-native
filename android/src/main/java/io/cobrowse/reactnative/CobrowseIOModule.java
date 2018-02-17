package io.cobrowse.reactnative;

import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import io.cobrowse.core.CobrowseIO;
import io.cobrowse.core.Session;

public class CobrowseIOModule extends ReactContextBaseJavaModule implements Session.Listener {

    CobrowseIOModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private void init() {
        if (getReactApplicationContext().getCurrentActivity() != null)
            CobrowseIO.instance().start(getReactApplicationContext().getCurrentActivity()).setListener(this);
    }

    public String getName() {
        return "CobrowseIO";
    }

    @Override
    public void sessionDidUpdate(Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("session_updated", Utility.convert(session));
    }

    @Override
    public void sessionDidEnd(Session session) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("session_ended", Utility.convert(session));
    }


    @ReactMethod
    public void api(String api) {
        CobrowseIO.instance().api = api;
    }

    @ReactMethod
    public void license(String license) {
        CobrowseIO.instance().license = license;
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
