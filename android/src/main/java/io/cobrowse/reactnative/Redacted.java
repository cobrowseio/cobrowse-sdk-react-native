package io.cobrowse.reactnative;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

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
        ViewGroup view = new LinearLayout(reactContext.getBaseContext());
        view.setAlpha(1);
        redactedViews.put(view, null);
        return view;
    }

}
