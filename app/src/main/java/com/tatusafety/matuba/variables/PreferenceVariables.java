package com.tatusafety.matuba.variables;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by dickson-incentro on 4/19/18.
 */

public class PreferenceVariables {
    private static final String TAG = PreferenceVariables.class.getSimpleName();

    private static final String AUTHORIZATION_TOKEN = "authorization_token";

    /**
     * Removes all shared preferences from the device
     *
     * @param context context
     */
    public static void removePreferencesFromDevice(Context context) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
            sp.edit().clear().apply();
        }
    }

    /**
     * Save auth token
     *
     * @param authToken   authToken
     * @param preferences preferences
     */
    public static void setAuthToken(String authToken, SharedPreferences preferences) {
        // save auth token to shared preferences
        preferences.edit().putString(PreferenceVariables.AUTHORIZATION_TOKEN, authToken).apply();
    }

    public static String getAuthToken(SharedPreferences preferences) {
        // save auth token to shared preferences
        return preferences.getString(PreferenceVariables.AUTHORIZATION_TOKEN, "");
    }

    public static boolean isLoggedIn(SharedPreferences preferences) {
        return !TextUtils.isEmpty(getAuthToken(preferences));
    }
}
