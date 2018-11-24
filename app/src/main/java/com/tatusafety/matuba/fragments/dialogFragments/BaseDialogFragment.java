package com.tatusafety.matuba.fragments.dialogFragments;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class BaseDialogFragment extends DialogFragment {
    private String TAG = getClass().getSimpleName();

    // override show method to avoid illegalStateException while showing the dialog
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d(TAG, "Exception" + e);
        }

    }
}
