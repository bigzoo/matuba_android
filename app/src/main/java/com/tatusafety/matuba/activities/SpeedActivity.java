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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.fragments.dialogFragments.DismissOnlyAlertDialog;

import java.util.Objects;

import static com.tatusafety.matuba.activities.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class SpeedActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private PointerSpeedometer speedometer;
    LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private String mBestProvider;
    private TextView speedTv;
    LocationListener locationListener;
    private String TAG = getClass().getSimpleName();
    private String mDismiss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        //set up the speedometer
        initSpeedometer();
        mDismiss = getResources().getString(R.string.dialog_dismiss);


        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mBestProvider = mLocationManager.getBestProvider(new Criteria(), false);


        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(this))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            checkLocationPermission();
        } else {
            mGoogleApiClient.connect();
        }
        this.onLocationChanged(null);
    }

    public void onLocationChanged(Location location) {
        if (location == null) {
            speedTv.setText("-.- km/h");
            speedometer.speedTo(0);
        } else {
            float currentSpeedinMs = location.getSpeed();
            speedTv.setText(currentSpeedinMs + " km/h");
            double multiplier = 3.6;
            double speedInKmh = currentSpeedinMs * multiplier;
            if(speedInKmh > 5)
            speedometer.speedTo((float) speedInKmh, 4000);
            Log.e(TAG, "speed in km: " + speedInKmh + " speed in m/s : " + currentSpeedinMs);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String title = getResources().getString(R.string.error);
        /* This is called when the GPS status alters */
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                String message = getResources().getString(R.string.out_of_service);
                Log.e(TAG, "****************Status Changed: Out of Service");

                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(this, mDismiss, title, message);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.e(TAG, "*************Status Changed: Temporarily Unavailable");
                String message2 = getResources().getString(R.string.connection_lost);

                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(this, mDismiss, title, message2);
                break;
            case LocationProvider.AVAILABLE:
                mGoogleApiClient.connect();
                onLocationChanged(null);
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onProviderDisabled(String provider) {
        String title = getResources().getString(R.string.error);
        String message = getResources().getString(R.string.provider_disabled);
        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(this, mDismiss, title, message);
    }

    private void initSpeedometer() {
        speedTv = findViewById(R.id.speedTv);
        speedometer = findViewById(R.id.speedView);
        speedometer.setSpeedTextPosition(PointerSpeedometer.Position.BOTTOM_CENTER);
        speedometer.setLowSpeedPercent(25);
        speedometer.setMediumSpeedPercent(75);
    }

    @Override
    public void onConnectionSuspended(int i) {
        String title = getResources().getString(R.string.information_title);
        String message = getResources().getString(R.string.connection_lost);
        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(this, mDismiss, title, message);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String title = getResources().getString(R.string.information_title);
        String message = getResources().getString(R.string.no_connection);

        // Show dialog
        DismissOnlyAlertDialog.showCustomDialog(this, mDismiss, title, message);
    }

    /*Ending the updates for the location service*/
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
}
