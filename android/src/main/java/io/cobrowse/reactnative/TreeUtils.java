package io.cobrowse.reactnative;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.HashSet;
import java.util.Set;

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

    public static Set<View> allParents(View root) {
        HashSet<View> parents = new HashSet<>();
        if (root instanceof ViewParent) {
          ViewParent target = (ViewParent) root;
          while ((target = target.getParent()) != null) {
            if (target instanceof View)
              parents.add((View) target);
          }
        }
        return parents;
    }

}
