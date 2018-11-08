package com.tatusafety.matuba.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
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
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.activities.PathSenseActivity;
import com.tatusafety.matuba.activities.MainActivity;
import com.tatusafety.matuba.activities.SpeedActivity;
import com.tatusafety.matuba.fragments.dialogFragments.DismissOnlyAlertDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Kilasi on 4/7/2018.
 */

public class LocationFragment extends Fragment implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private String TAG = getClass().getSimpleName();

    private TextView mLatitudeTv, mLongitudeTv;
    private GoogleApiClient mGoogleApiClient;
    LocationManager mLocationManager;

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver localActivityReceiver;
    private LocationRequest mLocationRequest;
    PathSenseActivity pathSenseActivity;
    private String mBestProvider;
    private String mDismiss = "dimiss";
    private EditText whereToEditText;

    public LocationFragment() {
    }

    public static LocationFragment newInstance(boolean permissionGranted) {

        return new LocationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.where_to, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String TAG = this.getClass().getSimpleName();

        whereToEditText = view.findViewById(R.id.whereTo_et);

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(this);
        pathSenseActivity = new PathSenseActivity();

        mLocationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
        assert mLocationManager != null;
        mBestProvider = mLocationManager.getBestProvider(new Criteria(), false);

        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()), "Speeding....", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO);
                mdToast.show();
                Intent intent = new Intent(this.getActivity(), SpeedActivity.class);
                startActivity(intent);
                break;
        }
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

        if (isAdded())
            // Check if permissions are granted first
            if (ActivityCompat.checkSelfPermission((Objects.requireNonNull(getActivity())),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                /*Getting the location after aquiring location service*/
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {
                    reverseGeoCode(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                } else {

                    /*if there is no last known location. Which means the device has no data for the location currently.
                     * So we will get the current location.
                     * For this we'll implement Location Listener and override onLocationChanged */

                    mLocationManager.requestLocationUpdates(mBestProvider, 0, 0, this);

                    if (!mGoogleApiClient.isConnected())
                        mGoogleApiClient.connect();
                }
            } else {
                MainActivity mainActivity = new MainActivity();
                mainActivity.checkLocationPermission();
            }
    }

    private void reverseGeoCode(Double latitude, Double longitude) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String address = addresses.get(0).getAddressLine(0);

            // Only if available else return NULL
            String knownName = addresses.get(0).getFeatureName();
            if (!TextUtils.isEmpty(knownName)) {
                whereToEditText.setText(knownName);
            } else
                whereToEditText.setText(address);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        String title = getResources().getString(R.string.error);
        String message = getResources().getString(R.string.connection_lost);
        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), 9000);
            } catch (IntentSender.SendIntentException e) {
                String title = getResources().getString(R.string.information_title);
                String message = getResources().getString(R.string.no_connection);

                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
                e.printStackTrace();
            }
        } else {
            // Connection error not resolvable
            String title = getResources().getString(R.string.error);
            String message = getResources().getString(R.string.unknown);

            // Show dialog
            DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitudeTv.setText(String.format("%s%s", getString(R.string.Latitude), String.valueOf(location.getLatitude())));
        mLongitudeTv.setText(String.format("%s%s", getString(R.string.Longitude), String.valueOf(location.getLongitude())));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String title = getResources().getString(R.string.error);
        /* This is called when the GPS status alters */
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                String message = getResources().getString(R.string.out_of_service);
                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                String message2 = getResources().getString(R.string.connection_lost);
                Log.e(TAG, "***************** location provider temporary unv");

                // Show dialog
//                DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message2);
                break;

            case LocationProvider.AVAILABLE:
                mGoogleApiClient.connect();
                onLocationChanged(null);
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        getUserLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        String title = getResources().getString(R.string.information_title);
        String message = getResources().getString(R.string.turn_on_gps);
        String dismiss = getResources().getString(R.string.dialog_dismiss);

        // TODO: 30-Sep-18 add an intent to go to setting with a different dialog

        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), dismiss, title, message);
    }
}
