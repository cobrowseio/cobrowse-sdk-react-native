package io.cobrowse.reactnative;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.views.view.ReactViewGroup;

import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

public class Redacted extends ViewGroupManager<ReactViewGroup> {

    static class RedactedView extends ReactViewGroup {

        RedactedView(Context context) {
            super(context);
        }

        @Override
        public void setBackground(Drawable drawable) {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    static WeakHashMap<View, String> redactedViews = new WeakHashMap<>();

    @Override
    @NonNull
    public String getName() {
        return "CBIOCobrowseRedacted";
    }

    @Nonnull
    @Override
    protected ReactViewGroup createViewInstance(@Nonnull ThemedReactContext reactContext) {
        ReactViewGroup view = new RedactedView(reactContext);
        redactedViews.put(view, null);
        return view;
    }

    @Override
    public void onDropViewInstance(@NonNull ReactViewGroup view) {
        redactedViews.remove(view);
    }
}
