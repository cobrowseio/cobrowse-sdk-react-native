package io.cobrowse.reactnative;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import io.cobrowse.core.Session;

final class Utility {

    static WritableMap convert(Session session) {
        WritableMap map = Arguments.createMap();
        if (session == null) return null;
        map.putString("code", session.code());
        map.putString("state", session.state());
        map.putString("id", session.id());
        map.putBoolean("approved", session.isApproved());
        return map;
    }

    static String convert(Error error) {
        if (error == null) return null;
        return error.getMessage();
    }

}
