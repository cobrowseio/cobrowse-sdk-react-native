package io.cobrowse.reactnative;

import android.app.Activity;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class CobrowseIO {
  public interface Delegate {
  }

  public interface RedactionDelegate extends Delegate {
    @Nullable
    List<View> redactedViews(@NonNull final Activity activity);

    @Nullable
    List<View> unredactedViews(@NonNull final Activity activity);
  }
}
