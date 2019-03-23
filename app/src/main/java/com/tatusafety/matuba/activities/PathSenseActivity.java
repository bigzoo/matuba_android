package com.tatusafety.matuba.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pathsense.android.sdk.location.PathsenseDetectedActivities;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.receivers.ActivityReceiver;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class PathSenseActivity extends _BaseActivity {

    private String TAG = getClass().getSimpleName();
    private PathsenseLocationProviderApi pathsenseLocationProviderApi;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver localActivityReceiver;
    FrameLayout mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_sense_activity);
        mProgressBar = findViewById(R.id.progressBar);
        setupToolBar(true, "Activity Recognition");

        pathsenseLocationProviderApi = PathsenseLocationProviderApi.getInstance(this);

        //This just gets the activity intent from the ActivityReceiver class
        accessActivityReceiver(PathSenseActivity.this);
        mProgressBar.setVisibility(View.VISIBLE);

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
                    textView.setText("You are " + detectedActivity.toLowerCase());
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
//        pathsenseLocationProviderApi.requestActivityUpdates(ActivityReceiver.class);

//        This gives updates only when it changes (ON_FOOT -> IN_VEHICLE for example)
        pathsenseLocationProviderApi.requestActivityChanges(ActivityReceiver.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeReceiver();
    }

    private void removeReceiver() {
        pathsenseLocationProviderApi.removeActivityUpdates();
        pathsenseLocationProviderApi.removeActivityChanges();
        //Unregister local receiver
        localBroadcastManager.unregisterReceiver(localActivityReceiver);
    }
}