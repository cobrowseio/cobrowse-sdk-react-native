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

public class CommonDelegates implements CobrowseIOCommonDelegates {

  private static final String SESSION_LOADED = "session.loaded";
  private static final String SESSION_UPDATED = "session.updated";
  private static final String SESSION_ENDED = "session.ended";
  private static final String SESSION_REQUESTED = "session.requested";

  private final HashSet<Integer> unredactedTags = new HashSet<>();
  private ReactApplicationContext reactApplicationContext;
  private NativeViewHierarchyManager nodeManager;

  public CommonDelegates(ReactApplicationContext context) {
    this.reactApplicationContext = context;
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
      if (CobrowseIOModule.delegate instanceof io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) {
        List<View> views = ((io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) CobrowseIOModule.delegate).unredactedViews(activity);
        if (views != null) unredacted.addAll(views);
      }
      return unredacted;
    }
  }

  private Set<View> getRedactedViews(@NonNull final Activity activity) {
    HashSet<View> redacted = new HashSet<>(RedactedViewManager.redactedViews.keySet());
    if (CobrowseIOModule.delegate instanceof io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) {
      List<View> views = ((io.cobrowse.reactnative.CobrowseIO.RedactionDelegate) CobrowseIOModule.delegate).redactedViews(activity);
      if (views != null) redacted.addAll(views);
    }
    return redacted;
  }

  @Override
  public void handleFullDeviceRequest(@NonNull Activity activity, @NonNull Session session) {
    // no-op, this will be handed on the JS side via an "updated" event handler
    // this stub just disables the default native prompt in the SDK
  }

  @Override
  public List<View> redactedViews(@NonNull Activity activity) {
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
