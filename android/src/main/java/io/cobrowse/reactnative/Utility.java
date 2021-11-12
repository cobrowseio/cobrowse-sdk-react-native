package io.cobrowse.reactnative;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import io.cobrowse.Agent;
import io.cobrowse.Session;
import io.cobrowse.Session.RemoteControlState;

final class Utility {

    static WritableMap convert(Session session) {
        WritableMap map = Arguments.createMap();
        if (session == null) return null;
        map.putString("code", session.code());
        map.putString("state", session.state());
        map.putString("id", session.id());
        map.putBoolean("full_device", session.fullDevice());
        map.putString("remote_control", Utility.remoteControl(session.remoteControl()));

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

    static String remoteControl (RemoteControlState state) {
        switch (state) {
            case RemoteControlState.On:
                return "on";
            case RemoteControlState.Requested:
                return "requested";
            case RemoteControlState.Rejected:
                return "rejected";
            case RemoteControlState.Off:
                return "off";
        }

        return "off";
    }

    static RemoteControlState remoteControl (String state) {
        switch (state) {
            case "on":
                return RemoteControlState.On;
            case "requested":
                return RemoteControlState.Requested;
            case "rejected":
                return RemoteControlState.Rejected;
            case "off":
                return RemoteControlState.Off;
        }

        return RemoteControlState.Off;
    }

}
