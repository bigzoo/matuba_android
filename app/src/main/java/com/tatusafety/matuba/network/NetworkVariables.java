package com.tatusafety.matuba.network;

/**
 * Created by dickson-incentro on 4/18/18.
 */

public class NetworkVariables {
    private static final String BASE_URL_JOURNEY = "https://platform.whereismytransport.com";
    private static final String BASE_URL_TOKEN = "https://identity.whereismytransport.com";

    public static String getAuthTokenUrl() {
        return BASE_URL_TOKEN + "/connect/token";
    }
}
