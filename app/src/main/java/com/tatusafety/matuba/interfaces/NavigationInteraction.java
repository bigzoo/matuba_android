package com.tatusafety.matuba.interfaces;

import android.support.v4.app.Fragment;

/**
 * Interaction between Activities and it's fragments/adapters which have navigation structure
 *
 * Created by Erwin on 19/02/2018.
 */
public interface NavigationInteraction {

    /**
     * Add a fragment to the stack
     * @param fragment fragment to add
     */
    void pushFragment(Fragment fragment);

    /**
     * Remove a fragment from the stack
     */
    void popFragment();

    /**
     * Replace fragment from stack
     * @param fragment fragment
     */
    void replaceFragment(Fragment fragment);

    /**
     * Obtain current fragment from stack
     * @return Fragment
     */
    Fragment getCurrentFragment();

    /**
     * Finish activity
     */
    void finishActivity();

}
