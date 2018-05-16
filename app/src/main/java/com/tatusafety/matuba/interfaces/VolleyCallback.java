package com.tatusafety.matuba.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by dickson-incentro on 4/18/18.
 *
 * Volley callback
 */

public interface VolleyCallback<T> {

    /**
     * Callback for when result is in from volley
     *
     * @param response response
     * @param error error
     * @param errorMessage errorMessage
     */
    void getResult(T response, VolleyError error, String errorMessage);
}
