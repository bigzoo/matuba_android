package com.tatusafety.matuba.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.tatusafety.matuba.PagerAdapter;
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.interfaces.VolleyCallback;
import com.tatusafety.matuba.network.NetworkManager;
import com.tatusafety.matuba.network.NetworkVariables;
import com.tatusafety.matuba.objects.request.LoginObject;
import com.tatusafety.matuba.utils.GlobalUtils;
import com.tatusafety.matuba.variables.PreferenceVariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = this.getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        getAuthToken();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Where is my transport"));
        tabLayout.addTab(tabLayout.newTab().setText("Reports"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    /**
     * Handle get Authentication Token call
     *
     */
    private void getAuthToken() {

        // initialize SSL context
        // NetworkManager.getInstance(this).initializeSSLContext();

        // Construct Payload
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("client_id", "f889f74b-b183-4dbc-b338-9dfa1c9bbddf");
        hashMap.put("client_secret", "bm8jRtDo67TAD0y2fMqoLTZt0BBbNJS61tmTk9EYz3U=");
        hashMap.put("grant_type", "client_credentials");

        String payload = GlobalUtils.createQueryStringForParameters(hashMap);

        NetworkManager.getInstance(this).postCall(
                NetworkVariables.getAuthTokenUrl(), payload, true, new VolleyCallback<String>() {
                    @Override
                    public void getResult(String response, VolleyError error, String errorMessage) {
                        if (!TextUtils.isEmpty(response)) {
                            parseLoginResponse(response);
                        } else if (error != null) {
                            Log.e(TAG, "Error: " + error.toString());
                        }
                    }
                });
    }

    /**
     * Method to process login Response from the Network request
     *
     * @param response response
     */
    private void parseLoginResponse(String response) {
        // login message to display
        String loginMessage = "";

        try {
            // parse response with jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode object = objectMapper.readTree(response);
            LoginObject loginResponse = objectMapper.treeToValue(object, LoginObject.class);
            String authenticationToken = loginResponse.getAccessToken();

            // save auth-token
            PreferenceVariables.setAuthToken(authenticationToken, preferences);

            makeJourneyCall();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Method to make a call for the journeys
     */
    private void makeJourneyCall() {

        // construct the QueryRequest object
        List<Double> coordinates_one = new ArrayList<>();
        coordinates_one.add(Double.valueOf(36.7929554));
        coordinates_one.add(Double.valueOf(-1.2982362));

        List<Double> coordinates_two = new ArrayList<>();
        coordinates_two.add(Double.valueOf(36.7929554));
        coordinates_two.add(Double.valueOf(-1.2982362));

        List<List<Double>> coordinates = new ArrayList<>();
        coordinates.add(coordinates_two);
        coordinates.add(coordinates_one);
    }
}
