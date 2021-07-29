package io.cobrowse.reactnative;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import io.cobrowse.Agent;
import io.cobrowse.Session;

final class Conversion {

    static WritableMap convert(Session session) {
        WritableMap map = Arguments.createMap();
        if (session == null) return null;
        map.putString("code", session.code());
        map.putString("state", session.state());
        map.putString("id", session.id());
        map.putBoolean("full_device", session.fullDevice());

        Agent agent = session.agent();
        if (agent != null) {
            WritableMap agentMap = Arguments.createMap();
            agentMap.putString("name", agent.name);
            agentMap.putString("id", agent.id);
            map.putMap("agent", agentMap);
        }

        return map;
    }

    static String convert(Error error) {
        if (error == null) return null;
        return error.getMessage();
    }

}
