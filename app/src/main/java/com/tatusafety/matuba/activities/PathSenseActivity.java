package com.tatusafety.matuba.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.pathsense.android.sdk.location.PathsenseDetectedActivities;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;
import com.tatusafety.matuba.ActivityReceiver;
import com.tatusafety.matuba.R;

public class PathSenseActivity extends AppCompatActivity  {

    private String TAG;

    private PathsenseLocationProviderApi pathsenseLocationProviderApi;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver localActivityReceiver;
    private FrameLayout mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport_fragment);

        pathsenseLocationProviderApi = PathsenseLocationProviderApi.getInstance(this);

        final String TAG = this.getClass().getSimpleName();
        mProgressBar = findViewById(R.id.progressBar);

        //This just gets the activity intent from the ActivityReceiver class
        accessActivityReceiver(PathSenseActivity.this);

    }

    public void accessActivityReceiver(Activity activity) {

        localBroadcastManager = LocalBroadcastManager.getInstance(activity);

        localActivityReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                //The detectedActivities object is passed as a serializable
                PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) intent.getSerializableExtra("ps");

                TextView textView = findViewById(R.id.activitiText);

                if (detectedActivities != null) {
                    mProgressBar.setVisibility(View.GONE);
                    String detectedActivity = detectedActivities.getMostProbableActivity().getDetectedActivity().name();
                    textView.setText("You are "  + detectedActivity);

                    if (!detectedActivities.isStationary()) {
                        Log.e(TAG, "******************* detectedActivity " + detectedActivity);
                    }
                }
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Register local broadcast receiver
        localBroadcastManager.registerReceiver(localActivityReceiver, new IntentFilter("activity"));

        //This gives an update everytime it receives one, even if it was the same as the last update
        pathsenseLocationProviderApi.requestActivityUpdates(ActivityReceiver.class);

//        This gives updates only when it changes (ON_FOOT -> IN_VEHICLE for example)
//        pathsenseLocationProviderApi.requestActivityChanges(ActivityReceiver.class);
    }

    @Override
    protected void onPause() {
        super.onPause();

        pathsenseLocationProviderApi.removeActivityUpdates();

//        pathsenseLocationProviderApi.removeActivityChanges();

        //Unregister local receiver
        localBroadcastManager.unregisterReceiver(localActivityReceiver);
    }
}