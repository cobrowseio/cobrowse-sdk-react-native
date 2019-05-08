package io.cobrowse.reactnative;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.views.view.ReactViewGroup;

import java.util.WeakHashMap;

import javax.annotation.Nonnull;

public class Redacted extends ViewGroupManager<ViewGroup> {

    static WeakHashMap<View, String> redactedViews = new WeakHashMap<>();

    @Override
    @NonNull
    public String getName() {
        return "CBIOCobrowseRedacted";
    }

    @Nonnull
    @Override
    protected ViewGroup createViewInstance(@Nonnull ThemedReactContext reactContext) {
        ViewGroup view = new ReactViewGroup(reactContext);
        view.setAlpha(1);
        redactedViews.put(view, null);
        return view;
    }

}
