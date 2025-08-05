package io.cobrowse.reactnative;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

import io.cobrowse.Agent;
import io.cobrowse.Session;
import io.cobrowse.Session.RemoteControlState;
import io.cobrowse.Session.FullDeviceState;

import java.util.HashMap;

final class Conversion {

  static WritableMap convert(Session session) {
    WritableMap map = Arguments.createMap();
    if (session == null) return null;
    map.putString("code", session.code());
    map.putString("state", session.state());
    map.putString("id", session.id());
    map.putString("full_device", Conversion.fullDeviceState(session.fullDevice()));
    map.putString("remote_control", Conversion.remoteControl(session.remoteControl()));

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

  static String remoteControl(RemoteControlState state) {
    switch (state) {
      case On:
        return "on";
      case Requested:
        return "requested";
      case Rejected:
        return "rejected";
      case Off:
        return "off";
    }

    return "off";
  }

  static RemoteControlState remoteControl(String state) {
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

  static String fullDeviceState(FullDeviceState state) {
    switch (state) {
      case On:
        return "on";
      case Requested:
        return "requested";
      case Rejected:
        return "rejected";
      case Off:
        return "off";
    }

    return "off";
  }

  static FullDeviceState fullDeviceState(String state) {
    switch (state) {
      case "on":
        return FullDeviceState.On;
      case "requested":
        return FullDeviceState.Requested;
      case "rejected":
        return FullDeviceState.Rejected;
      case "off":
        return FullDeviceState.Off;
    }

    return FullDeviceState.Off;
  }

  public static HashMap<String, String> readableMapToStringHashMap(ReadableMap readableMap) {
    HashMap<String, String> hashMap = new HashMap<>();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();

    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      ReadableType type = readableMap.getType(key);

      switch (type) {
        case Null:
          hashMap.put(key, null);
          break;
        case Boolean:
          hashMap.put(key, String.valueOf(readableMap.getBoolean(key)));
          break;
        case Number:
          hashMap.put(key, String.valueOf(readableMap.getDouble(key)));
          break;
        case String:
          hashMap.put(key, readableMap.getString(key));
          break;
        default:
          // Skip Map and Array types
          break;
      }
    }

    return hashMap;
  }
}
