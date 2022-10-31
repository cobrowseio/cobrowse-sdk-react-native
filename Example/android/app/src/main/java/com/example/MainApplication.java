package com.example;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.inspector.WindowInspector;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.cobrowse.reactnative.CobrowseIO;
import io.cobrowse.reactnative.CobrowseIOModule;

public class MainApplication extends Application implements ReactApplication, CobrowseIO.RedactionDelegate {

  private final ReactNativeHost mReactNativeHost =
      new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          @SuppressWarnings("UnnecessaryLocalVariable")
          List<ReactPackage> packages = new PackageList(this).getPackages();
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // packages.add(new MyReactNativePackage());
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
      };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
    initializeFlipper(this, getReactNativeHost().getReactInstanceManager());

    CobrowseIOModule.delegate = this;
  }

  /**
   * Loads Flipper in React Native templates. Call this in the onCreate method with something like
   * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
   *
   * @param context
   * @param reactInstanceManager
   */
  private static void initializeFlipper(
      Context context, ReactInstanceManager reactInstanceManager) {
    if (BuildConfig.DEBUG) {
      try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
        Class<?> aClass = Class.forName("com.example.ReactNativeFlipper");
        aClass
            .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
            .invoke(null, context, reactInstanceManager);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  @Nullable
  @Override
  public List<View> redactedViews(@NonNull Activity activity) {
    ArrayList<View> redacted = new ArrayList<View>() {{
      add(activity.getWindow().getDecorView());
    }};

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      List<View> globalWindowViews = WindowInspector.getGlobalWindowViews();

      for(int i=0; i < globalWindowViews.size(); i++){
        ArrayList<View> changeLocationViews = new ArrayList<>();
        globalWindowViews.get(i).findViewsWithText(changeLocationViews, "Change Bundle Location", View.FIND_VIEWS_WITH_TEXT);
        if (changeLocationViews.size() > 0) {
          for (int j = 0; j < changeLocationViews.size(); j++) {
            redacted.add((View) changeLocationViews.get(j).getParent());
          }
        }
      }
    }

    return redacted;
  }

  @Nullable
  @Override
  public List<View> unredactedViews(@NonNull Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      List<View> globalWindowViews = WindowInspector.getGlobalWindowViews();
      ArrayList<View> unredacted = new ArrayList<>();

      for(int i=0; i < globalWindowViews.size(); i++){
        ArrayList<View> configureBundlerViews = new ArrayList<>();
        globalWindowViews.get(i).findViewsWithText(configureBundlerViews, "Change Bundle Location", View.FIND_VIEWS_WITH_TEXT);
        if (configureBundlerViews.size() > 0) {
          for (int j = 0; j < configureBundlerViews.size(); j++) {
            unredacted.add((View) configureBundlerViews.get(j));
          }
        }
      }

      return unredacted;
    }

    return null;
  }
}
