package com.tatusafety.matuba.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class GlobalUtils {
   public static boolean locationsGiven = false;

    public static Drawable getAPICompatVectorDrawable(Context callingContext, int resource_id) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            return ContextCompat.getDrawable(callingContext.getApplicationContext(), resource_id);
        } else {
            return VectorDrawableCompat.create(
                    callingContext.getResources(),
                    resource_id,
                    callingContext.getTheme());
        }
    }

    public static void hideKeyboard(Activity activity, View viewToRemoveFrom) {
        try {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager manager = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));

            if ((activity.getCurrentFocus() != null) && (activity.getCurrentFocus().getWindowToken() != null)) {
                manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
            if (viewToRemoveFrom != null && viewToRemoveFrom.getWindowToken() != null) {
                manager.hideSoftInputFromWindow(viewToRemoveFrom.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
            if (viewToRemoveFrom == null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;

                // if the keyboard is showing
                if (imm.isAcceptingText()) {
                    // hide the keyboard
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        } catch (Exception e) {
//            Crashlytics.log(TAG + "Error while hiding Keyboard " + e.getLocalizedMessage());
//            Crashlytics.logException(e);
        }
    }


}
