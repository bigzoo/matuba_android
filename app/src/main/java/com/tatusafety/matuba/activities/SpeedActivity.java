package com.tatusafety.matuba.activities;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.fragments.dialogFragments.DismissOnlyAlertDialog;

import java.util.Objects;

import static com.tatusafety.matuba.activities.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by Kilasi 30/09/18
 * We use Location listener to request updates then update the speed we get from there to the speedometer
 */
public class SpeedActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private TubeSpeedometer speedometer;
    LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private String mBestProvider;
    private TextView speedTv;
    private String TAG = getClass().getSimpleName();
    private String mDismiss = "Dismiss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        //set up the speedometer
        initSpeedometer();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Device will determine the best provider between the GPS provider and Network provider
        mBestProvider = mLocationManager.getBestProvider(new Criteria(), false);

        // We use this for connectivity to internet
        // When the device has an internet connection , the onConnected method is called
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(this))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Check if we have permissions
        if (ActivityCompat.checkSelfPermission(this,
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
        speedTv = findViewById(R.id.info);
        speedometer = findViewById(R.id.speedView);
        speedometer.setSpeedTextPosition(PointerSpeedometer.Position.BOTTOM_CENTER);
        speedometer.setLowSpeedPercent(25);
        speedometer.setMediumSpeedPercent(75);
        speedometer.setSpeedometerColor(getResources().getColor(R.color.colorWhite));
    }

    // When there is a change in speed , this method is called
    // The speed is acquired from the device's location in m/s and converted to km/h by multiplying by 3.6
    public void onLocationChanged(Location location) {
        if (location == null) {
            speedometer.speedTo(0);
        } else {
            float currentSpeedinMs = location.getSpeed();
            double multiplier = 3.6;
            double speedInKmh = currentSpeedinMs * multiplier;

            speedometer.speedTo((float) speedInKmh, 4000);

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
        String title = getResources().getString(R.string.error);
        /* This is called when the GPS status alters */
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                String message = getResources().getString(R.string.out_of_service);
                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(this, this, mDismiss, title, message);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                String message2 = getResources().getString(R.string.connection_lost);

                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(this, this, mDismiss, title, message2);
                break;

            case LocationProvider.AVAILABLE:
                mGoogleApiClient.connect();
                onLocationChanged(null);
                break;
        }
    }

    // Available means that the provider has just become available
    @Override
    public void onProviderEnabled(String provider) {
        mGoogleApiClient.connect();
    }

    // The user has disabled the location services on their device
    @Override
    public void onProviderDisabled(String provider) {
        String title = getResources().getString(R.string.error);
        String message = getResources().getString(R.string.provider_disabled);

        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(this, this, mDismiss, title, message);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String title = getResources().getString(R.string.information_title);
        String message = getResources().getString(R.string.no_connection);

        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(this, this, mDismiss, title, message);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!TextUtils.isEmpty(mBestProvider)) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            } else {
                mLocationManager.requestLocationUpdates(mBestProvider, 0, 0, this);
            }
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(SpeedActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(SpeedActivity.this)
                        .setTitle(R.string.permission_dialog_title)

                        .setMessage(R.string.permission_message_dialog)

                        .setPositiveButton(R.string.give_permission_button_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SpeedActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(SpeedActivity.this),
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
