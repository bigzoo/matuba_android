package com.tatusafety.matuba.network;

/**
 * Created by dickson-incentro on 4/19/18.
 */

class NetworkConfiguration {
    private static NetworkConfiguration instance;

    // defaults
    private static int defaultTimeoutInMs = 10000;  // 10 secs
    private static int defaultMaxRetries = 2;

    // initial values
    private static int timeoutInMs = defaultTimeoutInMs;
    private static int maxRetries = defaultMaxRetries;

    /**
     * Get and create instance of NetworkConfiguration
     *
     * @return NetworkConfiguration instance
     */
    public static NetworkConfiguration getInstance() {
        synchronized (NetworkConfiguration.class) {
            if (instance == null) {
                instance = new NetworkConfiguration();
            }
            return instance;
        }
    }

    public int getTimeoutInMs() {
        return timeoutInMs;
    }

    /**
     * Setter to change timeout back to the default
     */
    public void setTimeoutInMs() {
        timeoutInMs = defaultTimeoutInMs;
    }

    public void setTimeoutInMs(int input) {
        timeoutInMs = input;
    }


    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Setter to change max retries back to the default
     */
    public void setMaxRetries() {
        maxRetries = defaultMaxRetries;
    }

    public void setMaxRetries(int input) {
        maxRetries = input;
    }
}
