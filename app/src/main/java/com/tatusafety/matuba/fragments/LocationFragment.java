package com.tatusafety.matuba.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pathsense.android.sdk.location.PathsenseDetectedActivities;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;
import com.tatusafety.matuba.ActivityReceiver;
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.activities.LocationActivity;
import com.tatusafety.matuba.activities.MainActivity;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Objects;

/**
 * Created by Kilasi on 4/7/2018.
 */

public class TransportFragment extends Fragment implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private EditText mWhereToEditText;

    private FloatingActionButton mFab;

    private TextView mLatitudeTv, mLongitudeTv, mActivity;

    double latitude, longitude;

    private LocationManager mLocManager;

    private static boolean mLocationPermissions;

    private String mProvider;

    private String TAG;
    private GoogleApiClient mGoogleApiClient;

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver localActivityReceiver;
    private LocationRequest mLocationRequest;
    LocationActivity locationActivity;
    private Location mLastLocation;
    private PathsenseLocationProviderApi pathsenseLocationProviderApi;

    public TransportFragment(){

    }

    public static TransportFragment newInstance(boolean permissionGranted) {

        return new TransportFragment();
    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.where_to, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String TAG = this.getClass().getSimpleName();

        mWhereToEditText = view.findViewById(R.id.whereTo_et);

        mFab = view.findViewById(R.id.fab);

        mFab.setOnClickListener(this);
        locationActivity = new LocationActivity();

        mLongitudeTv = view.findViewById(R.id.longitude);

        mLatitudeTv = view.findViewById(R.id.latitude);
        mActivity = view.findViewById(R.id.activitiText);

        mLocManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);

        mProvider = mLocManager.getBestProvider(new Criteria(), false);

        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        //This just gets the activity intent from the ActivityReceiver class
       accessActivityReceiver();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
//                MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()), "Searching....", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO);
//                mdToast.show();
                // Fab goes to Location Activity
                Intent intent = new Intent(this.getActivity(), LocationActivity.class);
                startActivity(intent);
                break;
        }

    }
    public void accessActivityReceiver() {

        localBroadcastManager = LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext()));

        localActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //The detectedActivities object is passed as a serializable
                PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) intent.getSerializableExtra("ps");

                if (detectedActivities != null) {
                    // get the  most probable activity in string format
                    String detectedActivity = detectedActivities.getMostProbableActivity()
                            .getDetectedActivity()
                            .name();

                    mActivity.setText(detectedActivity);

                    if (!detectedActivities.isStationary()) {
                        Log.e(TAG, "******************* detectedActivity " + detectedActivity);
                    }
                }
            }
        };
    }


    /*Ending the updates for the location service*/
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
       getUserLocation();
    }

    private void getUserLocation() {
        
        // Check if permissions are granted first
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), 
                android.Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_GRANTED) {
       
            /*Getting the location after aquiring location service*/

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {

                mLatitudeTv.setText(String.format("%s%s", getString(R.string.Latitude), String.valueOf(mLastLocation.getLatitude())));
                mLongitudeTv.setText(String.format("%s%s", getString(R.string.Longitude), String.valueOf(mLastLocation.getLongitude())));

            } else {

                /*if there is no last known location. Which means the device has no data for the location currently.
                 * So we will get the current location.
                 * For this we'll implement Location Listener and override onLocationChanged */

                Log.e(TAG, "****************No data for location found");

                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, TransportFragment.this);
            }
        }
        else {
            MainActivity mainActivity = new MainActivity();
            mainActivity.checkLocationPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection Suspended!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "**************** Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLatitudeTv.setText(String.format("%s%s", getString(R.string.Latitude), String.valueOf(mLastLocation.getLatitude())));
        mLongitudeTv.setText(String.format("%s%s", getString(R.string.Longitude), String.valueOf(mLastLocation.getLongitude())));
    }

    @Override
    public void onResume() {
        super.onResume();

        //Register local broadcast receiver
        localBroadcastManager.registerReceiver(localActivityReceiver, new IntentFilter("activity"));

        //This gives an update everytime it receives one, even if it was the same as the last update
        pathsenseLocationProviderApi.requestActivityUpdates(ActivityReceiver.class);

//        This gives updates only when it changes (ON_FOOT -> IN_VEHICLE for example)
//        pathsenseLocationProviderApi.requestActivityChanges(ActivityReceiver.class);
    }

    @Override
    public void onPause() {
        super.onPause();

        pathsenseLocationProviderApi.removeActivityUpdates();

        pathsenseLocationProviderApi.removeActivityChanges();

        //Unregister local receiver
        localBroadcastManager.unregisterReceiver(localActivityReceiver);
    }

}
