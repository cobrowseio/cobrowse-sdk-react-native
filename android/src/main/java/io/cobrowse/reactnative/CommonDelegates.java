package io.cobrowse.reactnative;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.core.util.Predicate;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.cobrowse.Session;

class CommonDelegates implements CobrowseIOCommonDelegates {

  private static final String SESSION_LOADED = "session.loaded";
  private static final String SESSION_UPDATED = "session.updated";
  private static final String SESSION_ENDED = "session.ended";
  private static final String SESSION_REQUESTED = "session.requested";

  private final HashSet<Integer> unredactedTags = new HashSet<>();
  private ReactApplicationContext reactApplicationContext;
  private NativeViewHierarchyManager nodeManager;

  CommonDelegates(ReactApplicationContext context) {
    this.reactApplicationContext = context;
  }

  @Override
  public void handleFullDeviceRequest(@NonNull Activity activity, @NonNull Session session) {
    // no-op, this will be handed on the JS side via an "updated" event handler
    // this stub just disables the default native prompt in the SDK
  }

  @Override
  public List<View> redactedViews(@NonNull Activity activity) {
    HashSet<View> redacted = new HashSet<>(RedactedViewManager.redactedViews.keySet());
    if (CobrowseIOModule.delegate instanceof io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) {
      List<View> views = ((io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) CobrowseIOModule.delegate).redactedViews(activity);
      if (views != null) redacted.addAll(views);
    }

    return new ArrayList<>(redacted);
  }

  @Override
  public List<View> unredactedViews(@NonNull Activity activity) {
    synchronized (unredactedTags) {
      HashSet<View> unredacted = new HashSet<>();
      for (Integer i : unredactedTags) {
        try {
          unredacted.add(nodeManager.resolveView(i));
        } catch (Exception e) {
          Log.i("CobrowseIO", "Failed to find unredacted view for tag " + i + ", error = " + e.getMessage());
        }
      }
      if (CobrowseIOModule.delegate instanceof io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) {
        List<View> views = ((io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) CobrowseIOModule.delegate).unredactedViews(activity);
        if (views != null) unredacted.addAll(views);
      }
      
      return new ArrayList<>(unredacted);
    }
  }

  @Override
  public void handleRemoteControlRequest(@NonNull Activity activity, @NonNull Session session) {
    // no-op, this will be handed on the JS side via an "updated" event handler
    // this stub just disables the default native prompt in the SDK
  }

  @Override
  public void sessionDidLoad(@NonNull Session session) {
    reactApplicationContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(SESSION_LOADED, Conversion.convert(session));
  }

  @Override
  public void handleSessionRequest(@NonNull Activity activity, @NonNull Session session) {
    reactApplicationContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(SESSION_REQUESTED, Conversion.convert(session));
  }

  @Override
  public void sessionDidUpdate(@NonNull Session session) {
    reactApplicationContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(SESSION_UPDATED, Conversion.convert(session));
  }

  @Override
  public void sessionDidEnd(@NonNull Session session) {
    reactApplicationContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(SESSION_ENDED, Conversion.convert(session));
  }

  public void findNodeManager() {
    if (nodeManager != null) return;
    final UIManagerModule uiManager = reactApplicationContext.getNativeModule(UIManagerModule.class);
    assert uiManager != null;
    uiManager.prependUIBlock(new UIBlock() {
      @Override
      public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {
        nodeManager = nativeViewHierarchyManager;
      }
    });
  }

  public void setUnredactedTags(final ReadableArray reactTags, final Promise promise) {
    synchronized (unredactedTags) {
      unredactedTags.clear();
      for (int i = 0; i < reactTags.size(); i++)
        unredactedTags.add(reactTags.getInt(i));
      promise.resolve(null);
    }
  }
}
