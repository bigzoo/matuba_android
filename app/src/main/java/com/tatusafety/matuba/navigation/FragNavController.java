package com.tatusafety.matuba.navigation;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.View;

import org.json.JSONArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The class is used to manage navigation through multiple stacks of fragments, as well as coordinate
 * fragments that may appear on screen
 * <p>
 * https://github.com/ncapdevi/FragNav
 * Nic Capdevila
 * Nic.Capdevila@gmail.com
 * <p>
 * Originally Created March 2016
 */
@SuppressWarnings("RestrictedApi")
public class FragNavController {

    // Extras used to store savedInstanceState
    private static final String EXTRA_TAG_COUNT = FragNavController.class.getName() + ":EXTRA_TAG_COUNT";
    private static final String EXTRA_SELECTED_TAB_INDEX = FragNavController.class.getName() + ":EXTRA_SELECTED_TAB_INDEX";
    private static final String EXTRA_CURRENT_FRAGMENT = FragNavController.class.getName() + ":EXTRA_CURRENT_FRAGMENT";
    private static final String EXTRA_FRAGMENT_STACK = FragNavController.class.getName() + ":EXTRA_FRAGMENT_STACK";

    @IdRes
    private final int mContainerId;
    @NonNull
    private final List<Stack<Fragment>> mFragmentStacks;
    @NonNull
    private final FragmentManager mFragmentManager;
    private final FragNavTransactionOptions mDefaultTransactionOptions;
    @Nullable
    private Fragment mCurrentFrag;
    @Nullable
    private DialogFragment mCurrentDialogFrag;
    @Nullable
    private RootFragmentListener mRootFragmentListener;
    @Nullable
    private TransactionListener mTransactionListener;
    private boolean mExecutingTransaction;

    //region Construction and setup

    private FragNavController(Builder builder, @Nullable Bundle savedInstanceState) {
        mFragmentManager = builder.mFragmentManager;
        mContainerId = builder.mContainerId;
        mFragmentStacks = new ArrayList<>(builder.mNumberOfTabs);
        mRootFragmentListener = builder.mRootFragmentListener;
        mTransactionListener = builder.mTransactionListener;
        mDefaultTransactionOptions = builder.mDefaultTransactionOptions;
    }

    public static Builder newBuilder(@Nullable Bundle savedInstanceState, FragmentManager fragmentManager, int containerId) {
        return new Builder(savedInstanceState, fragmentManager, containerId);
    }

    //endregion

    /**
     * Push a fragment onto the current stack
     *
     * @param fragment           The fragment that is to be pushed
     * @param transactionOptions Transaction options to be displayed
     */
    public void pushFragment(@Nullable Fragment fragment, @Nullable FragNavTransactionOptions transactionOptions) {
        if (fragment != null) {
            FragmentTransaction ft = createTransactionWithOptions(transactionOptions);

            detachCurrentFragment(ft);
            ft.add(mContainerId, fragment, generateTag(fragment));
            ft.commit();

            executePendingTransactions();

            mCurrentFrag = fragment;
            if (mTransactionListener != null) {
                mTransactionListener.onFragmentTransaction(mCurrentFrag, TransactionType.PUSH);
            }

        }
    }

    /**
     * Push a fragment onto the current stack
     *
     * @param fragment The fragment that is to be pushed
     */
    public void pushFragment(@Nullable Fragment fragment) {
        pushFragment(fragment, null);
    }

    /**
     * Replace the current fragment
     *
     * @param fragment           the fragment to be shown instead
     * @param transactionOptions Transaction options to be displayed
     */
    public void replaceFragment(@NonNull Fragment fragment, @Nullable FragNavTransactionOptions transactionOptions) {
        Fragment poppingFrag = getCurrentFrag();

        if (poppingFrag != null) {
            FragmentTransaction ft = createTransactionWithOptions(transactionOptions);

            String tag = generateTag(fragment);
            ft.replace(mContainerId, fragment, tag);

            //Commit our transactions
            ft.commit();

            executePendingTransactions();

            mCurrentFrag = fragment;

            if (mTransactionListener != null) {
                mTransactionListener.onFragmentTransaction(mCurrentFrag, TransactionType.REPLACE);

            }
        }
    }

    /**
     * Replace the current fragment
     *
     * @param fragment the fragment to be shown instead
     */
    public void replaceFragment(@NonNull Fragment fragment) {
        replaceFragment(fragment, null);
    }

    /**
     * @return Current DialogFragment being displayed. Null if none
     */
    @Nullable
    @CheckResult
    public DialogFragment getCurrentDialogFrag() {
        if (mCurrentDialogFrag != null) {
            return mCurrentDialogFrag;
        }
        //Else try to find one in the FragmentManager
        else {
            FragmentManager fragmentManager;
            if (mCurrentFrag != null) {
                fragmentManager = mCurrentFrag.getChildFragmentManager();
            } else {
                fragmentManager = mFragmentManager;
            }
            if (fragmentManager.getFragments() != null) {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment instanceof DialogFragment) {
                        mCurrentDialogFrag = (DialogFragment) fragment;
                        break;
                    }
                }
            }
        }
        return mCurrentDialogFrag;
    }

    /**
     * Clear any DialogFragments that may be shown
     */
    public void clearDialogFragment() {
        if (mCurrentDialogFrag != null) {
            mCurrentDialogFrag.dismiss();
            mCurrentDialogFrag = null;
        }
        // If we don't have the current dialog, try to find and dismiss it
        else {
            FragmentManager fragmentManager;
            if (mCurrentFrag != null) {
                fragmentManager = mCurrentFrag.getChildFragmentManager();
            } else {
                fragmentManager = mFragmentManager;
            }

            if (fragmentManager.getFragments() != null) {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment instanceof DialogFragment) {
                        ((DialogFragment) fragment).dismiss();
                    }
                }
            }
        }
    }

    /**
     * Display a DialogFragment on the screen
     *
     * @param dialogFragment The Fragment to be Displayed
     */
    public void showDialogFragment(@Nullable DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            FragmentManager fragmentManager;
            if (mCurrentFrag != null) {
                fragmentManager = mCurrentFrag.getChildFragmentManager();
            } else {
                fragmentManager = mFragmentManager;
            }

            //Clear any current dialog fragments
            if (fragmentManager.getFragments() != null) {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment instanceof DialogFragment) {
                        ((DialogFragment) fragment).dismiss();
                        mCurrentDialogFrag = null;
                    }
                }
            }

            mCurrentDialogFrag = dialogFragment;
            try {
                dialogFragment.show(fragmentManager, dialogFragment.getClass().getName());
            } catch (IllegalStateException e) {
                // Activity was likely destroyed before we had a chance to show, nothing can be done here.
            }
        }
    }

    //endregion

    /**
     * Attempts to detach any current fragment if it exists, and if none is found, returns.
     *
     * @param ft the current transaction being performed
     */
    private void detachCurrentFragment(@NonNull FragmentTransaction ft) {
        Fragment oldFrag = getCurrentFrag();
        if (oldFrag != null) {
            ft.detach(oldFrag);
        }
    }

    /**
     * Helper function to attempt to get current fragment
     *
     * @return Fragment the current frag to be returned
     */
    @Nullable
    @CheckResult
    public Fragment getCurrentFrag() {
        //Attempt to used stored current fragment
        if (mCurrentFrag != null) {
            return mCurrentFrag;
        }

        return mCurrentFrag;
    }

    /**
     * Create a unique fragment tag so that we can grab the fragment later from the FragmentManger
     *
     * @param fragment The fragment that we're creating a unique tag for
     * @return a unique tag using the fragment's class name
     */
    @NonNull
    @CheckResult
    private String generateTag(@NonNull Fragment fragment) {
        return fragment.getClass().getName();
    }

    /**
     * This check is here to prevent recursive entries into executePendingTransactions
     */
    private void executePendingTransactions() {
        if (!mExecutingTransaction) {
            mExecutingTransaction = true;
            mFragmentManager.executePendingTransactions();
            mExecutingTransaction = false;
        }
    }

    /**
     * Private helper function to clear out the fragment manager on initialization. All fragment management should be done via FragNav.
     */
    private void clearFragmentManager() {
        if (mFragmentManager.getFragments() != null) {
            FragmentTransaction ft = createTransactionWithOptions(null);
            for (Fragment fragment : mFragmentManager.getFragments()) {
                if (fragment != null) {
                    ft.remove(fragment);
                }
            }
            ft.commit();
            executePendingTransactions();
        }
    }

    /**
     * Setup a fragment transaction with the given option
     *
     * @param transactionOptions The options that will be set for this transaction
     */
    @CheckResult
    private FragmentTransaction createTransactionWithOptions(@Nullable FragNavTransactionOptions transactionOptions) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (transactionOptions == null) {
            transactionOptions = mDefaultTransactionOptions;
        }
        if (transactionOptions != null) {

            ft.setCustomAnimations(transactionOptions.enterAnimation, transactionOptions.exitAnimation, transactionOptions.popEnterAnimation, transactionOptions.popExitAnimation);
            ft.setTransitionStyle(transactionOptions.transitionStyle);

            ft.setTransition(transactionOptions.transition);


            if (transactionOptions.sharedElements != null) {
                for (Pair<View, String> sharedElement : transactionOptions.sharedElements) {
                    ft.addSharedElement(sharedElement.first, sharedElement.second);
                }
            }


            if (transactionOptions.breadCrumbTitle != null) {
                ft.setBreadCrumbTitle(transactionOptions.breadCrumbTitle);
            }

            if (transactionOptions.breadCrumbShortTitle != null) {
                ft.setBreadCrumbShortTitle(transactionOptions.breadCrumbShortTitle);

            }
        }
        return ft;
    }

    //endregion

    //region Public helper functions

    /**
     * Get the number of fragment stacks
     *
     * @return the number of fragment stacks
     */
    @CheckResult
    public int getSize() {
        return mFragmentStacks.size();
    }


    /**
     * Get a copy of the stack at a given index
     *
     * @return requested stack
     */
    @SuppressWarnings("unchecked")
    @CheckResult
    @Nullable
    public Stack<Fragment> getStack(@TabIndex int index) {
        if (index >= mFragmentStacks.size()) {
            throw new IndexOutOfBoundsException("Can't get an index that's larger than we've setup");
        }
        return (Stack<Fragment>) mFragmentStacks.get(index).clone();
    }

    /**
     * @return If true, you are at the bottom of the stack
     *         (Consider using replaceFragment if you need to change the root fragment for some reason)
     *         else you can popFragment as needed as your are not at the root

    //endregion

    //region SavedInstanceState

    /**
     * Call this in your Activity's onSaveInstanceState(Bundle outState) method to save the instance's state.
     *
     * @param outState The Bundle to save state information to
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {

        // Write current fragment
        if (mCurrentFrag != null) {
            outState.putString(EXTRA_CURRENT_FRAGMENT, mCurrentFrag.getTag());
        }

        // Write stacks
        try {
            final JSONArray stackArrays = new JSONArray();

            for (Stack<Fragment> stack : mFragmentStacks) {
                final JSONArray stackArray = new JSONArray();

                for (Fragment fragment : stack) {
                    stackArray.put(fragment.getTag());
                }

                stackArrays.put(stackArray);
            }

            outState.putString(EXTRA_FRAGMENT_STACK, stackArrays.toString());
        } catch (Throwable t) {
            // Nothing we can do
        }
    }

    public enum TransactionType {
        PUSH,
        POP,
        REPLACE
    }

    //Declare the TabIndex annotation
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabIndex {
    }

    // Declare Transit Styles
    @IntDef({FragmentTransaction.TRANSIT_NONE, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, FragmentTransaction.TRANSIT_FRAGMENT_CLOSE, FragmentTransaction.TRANSIT_FRAGMENT_FADE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Transit {
    }

    public interface RootFragmentListener {
        /**
         * Dynamically create the Fragment that will go on the bottom of the stack
         *
         * @param index the index that the root of the stack Fragment needs to go
         * @return the new Fragment
         */
    }

    public interface TransactionListener {

        void onTabTransaction(Fragment fragment, int index);

        void onFragmentTransaction(Fragment fragment, TransactionType transactionType);
    }

    public static final class Builder {
        private final int mContainerId;
        private FragmentManager mFragmentManager;
        private RootFragmentListener mRootFragmentListener;
        @TabIndex
        private TransactionListener mTransactionListener;
        private FragNavTransactionOptions mDefaultTransactionOptions;
        private int mNumberOfTabs = 0;
        private List<Fragment> mRootFragments;
        private Bundle mSavedInstanceState;

        public Builder(@Nullable Bundle savedInstanceState, FragmentManager mFragmentManager, int mContainerId) {
            this.mSavedInstanceState = savedInstanceState;
            this.mFragmentManager = mFragmentManager;
            this.mContainerId = mContainerId;
        }

        /**
         * @param transactionOptions The default transaction options to be used unless otherwise defined.
         */
        public Builder defaultTransactionOptions(@NonNull FragNavTransactionOptions transactionOptions) {
            mDefaultTransactionOptions = transactionOptions;
            return this;
        }


        /**
         * @param rootFragmentListener a listener that allows for dynamically creating root fragments
         */
        public Builder rootFragmentListener(RootFragmentListener rootFragmentListener) {
            mRootFragmentListener = rootFragmentListener;
            return this;
        }

        /**
         * @param val A listener to be implemented (typically within the main activity) to fragment transactions (including tab switches)
         */
        public Builder transactionListener(TransactionListener val) {
            mTransactionListener = val;
            return this;
        }


        public FragNavController build() {
            if (mRootFragmentListener == null && mRootFragments == null) {
                throw new IndexOutOfBoundsException("Either a root fragment(s) needs to be set, or a fragment listener");
            }
            return new FragNavController(this, mSavedInstanceState);
        }
    }
}

