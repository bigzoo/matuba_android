package com.tatusafety.matuba.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.fragments.dialogFragments.DismissOnlyAlertDialog;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tatusafety.matuba.activities.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by Kilasi 30/09/18
 * We use Location listener to request updates then update the speed we get from there to the speedometer
 */
public class SpeedFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private String mBestProvider;
    private String TAG = getClass().getSimpleName();
    @BindView(R.id.info)
    TextView speedTv;
    @BindView(R.id.speedView)
    TubeSpeedometer speedometer;
    @BindString(R.string.dismiss)
    String mDismiss;


    public SpeedFragment() {
    }

    public static SpeedFragment newInstance() {
        return new SpeedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        //set up the speedometer
        initSpeedometer();

        if (getContext() != null)
            mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        // Device will determine the best provider between the GPS provider and Network provider
        mBestProvider = mLocationManager.getBestProvider(new Criteria(), false);

        // We use getContext() for connectivity to internet
        // When the device has an internet connection , the onConnected method is called
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getContext()))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Check if we have permissions
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // no permissions granted , request for permissions
            checkLocationPermission();
        } else {
            // check if there is a connection
            mGoogleApiClient.connect();
        }

        // Set up the location listener
        this.onLocationChanged(null);
    }

    private void initSpeedometer() {
        speedometer.setSpeedTextPosition(PointerSpeedometer.Position.BOTTOM_CENTER);
        speedometer.setLowSpeedPercent(25);
        speedometer.setMediumSpeedPercent(75);
        speedometer.setSpeedometerColor(getResources().getColor(R.color.colorWhite));
    }

    // When there is a change in speed , getContext() method is called
    // The speed is acquired from the device's location in m/s and converted to km/h by multiplying by 3.6
    public void onLocationChanged(Location location) {
        if (location == null) {
            speedometer.speedTo(0);
        } else {
            float currentSpeedinMs = location.getSpeed();
            double multiplier = 3.6;
            double speedInKmh = currentSpeedinMs * multiplier;

            speedometer.speedTo((float) speedInKmh, 3000);

            // set the text to let the user now to slow down if they are speeding
            if (speedInKmh < 80) {
                speedTv.setText(R.string.observe_speed_limits);
            } else if (speedInKmh >= 80) {
                speedTv.setText(R.string.slow_down);
            } else if (speedInKmh >= 120) {
                speedTv.setText(R.string.slow_down_more);
            }
        }
    }

    // The status of the provider can change at any time
    // The int status contains the change
    // Out of service is not usually resolvable as the device has either no internet or no GPS connection
    // Temporarily unavailable means that the internet connection was lost but may be back shortly
    // Available means that the provider has just become available
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (isAdded()) {
            String title = getResources().getString(R.string.error);
            /* getContext() is called when the GPS status alters */
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    String message = getResources().getString(R.string.out_of_service);
                    // Show dialog
                    DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
                    break;
                case LocationProvider.AVAILABLE:
                    mGoogleApiClient.connect();
                    onLocationChanged(null);
                    break;
            }
        }
    }

    // Available means that the provider has just become available
    @Override
    public void onProviderEnabled(String provider) {
        if (isAdded())
            mGoogleApiClient.connect();
    }

    // The user has disabled the location services on their device
    @Override
    public void onProviderDisabled(String provider) {
        if (isAdded()) {
            String title = getResources().getString(R.string.error);
            String message = getResources().getString(R.string.provider_disabled);

            // Show dialog
            DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String title = getResources().getString(R.string.information_title);
        String message = getResources().getString(R.string.no_connection);

        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!TextUtils.isEmpty(mBestProvider) && getContext() != null) {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            } else {
                mLocationManager.requestLocationUpdates(mBestProvider, 0, 0, this);
            }
        }
    }

    public void checkLocationPermission() {
        if (getContext() != null && getActivity() != null &&
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.permission_dialog_title)

                        .setMessage(R.string.permission_message_dialog)

                        .setPositiveButton(R.string.give_permission_button_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && getActivity() != null) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mGoogleApiClient.connect();
                    }
                }
            }
        }
    }

    /*Ending the updates for the location service*/
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
