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

    public static <T> Set<View> allParentsUntil(View root, Class<T> cls) {
        HashSet<View> parents = new HashSet<>();
        ViewParent target = root.getParent();
        while (target != null && (!cls.isInstance(target))) {
            if (target instanceof View) parents.add((View) target);
            target = target.getParent();
        }
        return parents;
    }

    public static <T> Set<T> findAllClosest(View root, Class<T> cls) {
        HashSet<T> found = new HashSet<>();
        if (cls.isInstance(root)) found.add((T)root);
        else {
            for (View v : directChildren(root)) {
               found.addAll(findAllClosest(v, cls));
           }
       }
       return found;
    }
}
