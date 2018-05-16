package com.tatusafety.matuba.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tatusafety.matuba.interfaces.VolleyCallback;
import com.tatusafety.matuba.network.VolleyToolboxExtension;
import com.tatusafety.matuba.variables.PreferenceVariables;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

/**
 * Created by dickson-incentro on 4/18/18.
 */

public class NetworkManager {
    private final String TAG = getClass().getSimpleName();
    private static NetworkManager instance = null;
    private SharedPreferences preferences;
    // private Database database;

    private ConnectivityManager connectivityManager;
    private Resources resources;

    // for Volley API
    public RequestQueue requestQueue;

    /**
     * Constructor that makes requestQueue
     *
     * @param context context
     */
    private NetworkManager(Context context) {
        // Fabric.with(context, new Answers());
        requestQueue = VolleyToolboxExtension.newRequestQueue(context);
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        // database = Database.getInstance(context);
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        resources = context.getResources();
    }

    /**
     * getInstance method
     *
     * @param context context
     * @return NetworkManager instance
     */
    public static synchronized NetworkManager getInstance(Context context) {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }

    /**
     * Initialize SSL
     */
    public void initializeSSLContext(){
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Regular getResponse method to obtain a JSON response from the backend.
     * getCall method to obtain a JSON response from the backend.
     *
     * @param url         backend url to make the call to
     * @param jsonPayload JSON payload for the request. Is an optional parameter, may be null.
     * @param listener    listener that is activated when the call is finished and a response is known
     */
    public void getCall(String url, String jsonPayload, boolean saveToken, final VolleyCallback<String> listener) {
        doRequest(url, jsonPayload, Request.Method.GET, saveToken, listener);
    }

    /**
     * postCall method to obtain a JSON response from the backend.
     *
     * @param url         backend url to make the call to
     * @param jsonPayload JSON payload for the request. Is an optional parameter, may be null.
     * @param listener    listener that is activated when the call is finished and a response is known
     */
    public void postCall(String url, String jsonPayload, boolean saveToken, final VolleyCallback<String> listener) {
        doRequest(url, jsonPayload, Request.Method.POST, saveToken, listener);
    }

    /**
     * putCall method to obtain a JSON response from the backend.
     *
     * @param url         backend url to make the call to
     * @param jsonPayload JSON payload for the request. Is an optional parameter, may be null.
     * @param listener    listener that is activated when the call is finished and a response is known
     */
    public void putCall(String url, String jsonPayload, boolean saveToken, final VolleyCallback<String> listener) {
        doRequest(url, jsonPayload, Request.Method.PUT, saveToken, listener);
    }

    /**
     * deleteCall method to delete a item.
     *
     * @param url         backend url to make the call to
     * @param jsonPayload JSON payload for the request. Is an optional parameter, may be null.
     * @param listener    listener that is activated when the call is finished and a response is known
     */
    public void deleteCall(String url, String jsonPayload, boolean saveToken, final VolleyCallback<String> listener) {
        doRequest(url, jsonPayload, Request.Method.DELETE, saveToken, listener);
    }

    private void doRequest(final String url, String jsonPayload, int method, final boolean saveToken, final VolleyCallback<String> listener) {

        // check if connected to internet
        if(!hasConnectivity()) {
            listener.getResult(null, new VolleyError("NetworkError"), "Network Error");
        }
        else {
            Log.d(TAG, "Call to backend with url: " + url + " and method: " + method + " and payload: " + jsonPayload);

            MetaStringRequest request = new MetaStringRequest(method, url, jsonPayload,
                    new Response.Listener<MetaStringRequest.MetaResponse>() {

                        /**
                         * What to do when a response is known
                         *
                         * @param response string response containing json
                         */
                        @Override
                        public void onResponse(MetaStringRequest.MetaResponse response) {
                            if (response != null) {

                                // send the result
                                listener.getResult(response.getResponse(), null, null);

                                logResponse(response);
                                Log.e(TAG, "Response is NOT null");
                            } else {
                                Log.e(TAG, "Response is null");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        /**
                         * What to do when an error occurred
                         *
                         * @param error error
                         */
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String message = "";
                            String logMessage = "null";
                            int statusCode = -1;
                            String errorData = "null";

                            if (error.networkResponse != null) {
                                statusCode = error.networkResponse.statusCode;

                                if (error.networkResponse.data != null) {
                                    errorData = new String(error.networkResponse.data);
                                }
                            }

                            if (error instanceof ServerError) {
                                logMessage = "ServerError";
                            } else if (error instanceof NetworkError) {
                                logMessage = "NetworkError";
                            } else if (error instanceof AuthFailureError) {
                                logMessage = "AuthFailureError";
                            } else if (error instanceof ParseError) {
                                logMessage = "ParseError";
                            } else if (error instanceof TimeoutError) {
                                logMessage = "TimeoutError";
                            }

                            if(statusCode >= 0) {
                                // parameter cannot be longer than 100 chars
                                String errorUrl = url;
                                if (errorUrl.length() > 100) {
                                    errorUrl = url.substring(0, 99);
                                }

                                // log the network error to fabric
                                Log.e(TAG, "Error "+ statusCode +  errorUrl);
                            }

                            // log to console
                            Log.d(TAG, logMessage + " [" + statusCode + "] from url: " + url);
                            Log.d(TAG, "Data: " + errorData);
                            Log.e(TAG, "onErrorResponse: ", error);

                            // reset auth token if needed
                            if (statusCode == 400 && !TextUtils.isEmpty(errorData) && errorData.contains("AuthenticationTokenInvalid")) {
                                PreferenceVariables.setAuthToken("", preferences);
                            }

                            // return the callback
                            listener.getResult(null, error, message);
                        }
                    }
            )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params;

                    // get super getHeaders in case there is something we should take over
                    if (super.getHeaders().size() > 0) {
                        params = super.getHeaders();
                    } else {
                        params = new HashMap<>();
                    }

                    // todo: repalce the false with a check if user is not logged in
                    if (PreferenceVariables.isLoggedIn(preferences)) {
                        params.put("Authorization", "Bearer " + PreferenceVariables.getAuthToken(preferences));
                        Log.e(TAG, "isLoggedIn: ");
                    } else {;
                        Log.e(TAG, "Not LoggedIn: ");
                    }

                    return params;
                }

                @Override
                public String getBodyContentType() {
                    // todo: repalce the true with a check if it's logged in
                    if (true) {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }
                    return "application/json; charset=UTF-8";
                }
            };

            //obtain instance of NetworkConfiguration
            NetworkConfiguration networkConfiguration = NetworkConfiguration.getInstance();

            //use values, may be altered before a specific call somewhere
            request.setRetryPolicy(new DefaultRetryPolicy(
                    networkConfiguration.getTimeoutInMs(),
                    networkConfiguration.getMaxRetries(),
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //change back to defaults
            networkConfiguration.setTimeoutInMs();
            networkConfiguration.setMaxRetries();

            //add request to queue
            requestQueue.add(request);
        }
    }

    /**
     * log the data when there is a response and when the build is not in release mode
     * @param response
     */
    private void logResponse(MetaStringRequest.MetaResponse response) {

        // only do this when not in release mode
        // and if response not empty
        if(!TextUtils.isEmpty(response.getResponse())) {
            // get the mapper
            ObjectMapper mapper = new ObjectMapper();
            // try to map for a pretty log message
            try {
                Object jsonObject = mapper.readValue(response.getResponse(), Object.class);
                String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
                Log.d(TAG, "Response from call to backend was obtained: \n" + prettyJson);
            }
            // no pretty log message. output the error and the original
            catch (IOException e) {
                Log.d(TAG, "Response cannot be parsed. Exception is:");
                Log.e(TAG, e.toString());
                Log.d(TAG, "Response was:\n" + response.getResponse());
            }
        } 
    }

    /**
     * returns a boolean true if the device is connected to internet
     * @return
     */
    private boolean hasConnectivity() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
