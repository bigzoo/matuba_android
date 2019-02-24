package com.tatusafety.matuba.fragments.dialogFragments;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
