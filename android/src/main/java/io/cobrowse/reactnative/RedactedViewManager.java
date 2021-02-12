package io.cobrowse.reactnative;

import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import java.util.WeakHashMap;

import androidx.annotation.NonNull;

public class RedactedViewManager extends ViewGroupManager<RedactedView> {

    static WeakHashMap<View, String> redactedViews = new WeakHashMap<>();

    @Override
    @NonNull
    public String getName() {
        return "CBIOCobrowseRedacted";
    }

    @NonNull
    @Override
    protected RedactedView createViewInstance(@NonNull ThemedReactContext reactContext) {
        RedactedView view = new RedactedView(reactContext);
        redactedViews.put(view, null);
        return view;
    }

    @Override
    public void onDropViewInstance(@NonNull RedactedView view) {
        super.onDropViewInstance(view);
        redactedViews.remove(view);
    }

}
