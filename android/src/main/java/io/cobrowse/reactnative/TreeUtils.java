package io.cobrowse.reactnative;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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

    public static View closest(View root, Predicate<View> predicate) {
      if (predicate.test(root)) return root;
      ViewParent parent = root.getParent();
      if (parent instanceof ViewGroup) return closest((ViewGroup)parent, predicate);
      return null;
    }
}
