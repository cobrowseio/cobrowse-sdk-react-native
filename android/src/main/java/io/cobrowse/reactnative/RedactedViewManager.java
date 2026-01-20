package io.cobrowse.reactnative;

import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

import java.util.WeakHashMap;

import androidx.annotation.NonNull;

public class RedactedViewManager extends ReactViewManager {

    static WeakHashMap<View, String> redactedViews = new WeakHashMap<>();

    @Override
    @NonNull
    public String getName() {
        return "CBIOCobrowseRedacted";
    }

    @Override
    @NonNull
    public ReactViewGroup createViewInstance(@NonNull ThemedReactContext reactContext) {
      RedactedView view = new RedactedView(reactContext);
      redactedViews.put(view, null);
      return view;
    }

    @Override
    public void onDropViewInstance(@NonNull ReactViewGroup view) {
      if (view instanceof RedactedView) {
        RedactedView redactedView = (RedactedView) view;
        redactedViews.remove(redactedView);
      }

      super.onDropViewInstance(view);
    }
}