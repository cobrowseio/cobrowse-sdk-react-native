package io.cobrowse.reactnative;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.facebook.react.ReactRootView;
import com.facebook.react.views.view.ReactViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.core.util.Predicate;

class TreeUtils {

    public static Set<View> directChildren(View root) {
        HashSet<View> children = new HashSet<>();
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                children.add(viewGroup.getChildAt(i));
            }
        }
        return children;
    }

    public static List<View> allParents(View root) {
        ArrayList<View> parents = new ArrayList<>();
        ViewParent target = root.getParent();
        while (target != null) {
            if (target instanceof View) parents.add(0, (View) target);
            target = target.getParent();
        }
        return parents;
    }

    public static List<View> reactParents(View root) {
      List<View> parents = allParents(root);
      ArrayList<View> reactParents = new ArrayList<>(parents);
      for (View v : parents) {
        if (isReactView(v)) break;
        reactParents.remove(v);
      }
      return reactParents;
    }

    public static Set<View> findAllClosest(View root, Predicate<View> predicate) {
        HashSet<View> found = new HashSet<>();
        if (predicate.test(root)) found.add(root);
        else {
            for (View v : directChildren(root)) {
               found.addAll(findAllClosest(v, predicate));
           }
       }
       return found;
    }

    public static boolean isReactView(View view) {
      return view instanceof ReactRootView || view instanceof ReactViewGroup;
    }
}
