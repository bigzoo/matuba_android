package com.tatusafety.matuba.fragments;

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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.tatusafety.matuba.R;
import com.tatusafety.matuba.activities.MainActivity;
import com.tatusafety.matuba.activities.PathSenseActivity;
import com.tatusafety.matuba.activities.SpamActivity;
import com.tatusafety.matuba.fragments.dialogFragments.DismissOnlyAlertDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kilasi on 4/7/2018.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private String TAG = getClass().getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    LocationManager mLocationManager;

    PathSenseActivity pathSenseActivity;
    private String mBestProvider;
    @BindView(R.id.whereTo_et)
    EditText whereToEditText;
    @BindView(R.id.speed_activity_btn)
    Button mSpeed;
    @BindView(R.id.spam_activity_btn)
    Button mSpam;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String TAG = this.getClass().getSimpleName();
        ButterKnife.bind(this, view);

        SupportPlaceAutocompleteFragment autocompleteFragment = new SupportPlaceAutocompleteFragment();
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = null;
        if (fm != null) {
            ft = fm.beginTransaction();
        }
        if (ft != null) {
            ft.replace(R.id.place_autocomplete_fragment, autocompleteFragment);
        }
        if (ft != null) {
            ft.commit();
        }
        setUpAutoComplete(autocompleteFragment);

        FloatingActionButton fab = view.findViewById(R.id.fabulous);
        fab.setOnClickListener(this);
        mSpam.setOnClickListener(this);
        mSpeed.setOnClickListener(this);

        // set up the activity recognition
        pathSenseActivity = new PathSenseActivity();

        // set up location manager so that we can Know where the user currently is
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
            case R.id.spam_activity_btn:
                MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()),
                        "Preparing to spam....", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO);
                mdToast.show();
                Intent intent = new Intent(this.getActivity(), SpamActivity.class);
                startActivity(intent);
                break;
            case R.id.speed_activity_btn:
                mdToast = MDToast.makeText(Objects.requireNonNull(getContext()),
                        "Speeding....", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO);
                mdToast.show();
                Intent intent1 = new Intent(this.getActivity(), SpeedFragment.class);
                startActivity(intent1);
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
                    Log.e(TAG, "*************** user is at lat " + mLastLocation.getLatitude() + " long " + mLastLocation.getLongitude());
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

            // If any additional address line present than only,
            // check with max available address lines by getMaxAddressLineIndex()
            String address = addresses.get(0).getAddressLine(0);

            // Use address if known name is not available
            String knownName = addresses.get(0).getFeatureName();
            if (!TextUtils.isEmpty(knownName)) {
                whereToEditText.setText(knownName);
            } else
                whereToEditText.setText(address);

        } catch (IOException e) {
            showError(null, null);
            e.printStackTrace();
        }
    }

    private void setUpAutoComplete(SupportPlaceAutocompleteFragment autocompleteFragment) {

        if (autocompleteFragment != null) {

            // Bind between Kenya and Nairobi
            autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(0.0236, 37.9062),
                    new LatLng(1.2921, 36.8219)));

            // only display seach results from Kenya and address
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .setCountry("KE")
                    .build();

            autocompleteFragment.setFilter(typeFilter);

            // Once a place is selected , show the user where they are currently and where they have selected
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {

                    Log.e(TAG, "Place lat: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
                    whereToEditText.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Status status) {
                    if (isAdded())
                        DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(),
                                "dismiss", getString(R.string.error), String.valueOf(status));
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        showError(getResources().getString(R.string.error), getResources().getString(R.string.connection_lost));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), 9000);
            } catch (IntentSender.SendIntentException e) {
                showError(getResources().getString(R.string.no_connection), getResources().getString(R.string.information_title));
                e.printStackTrace();
            }
        } else {
            // Connection error not resolvable
            showError(getResources().getString(R.string.unknown), getResources().getString(R.string.error));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        reverseGeoCode(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String title = getResources().getString(R.string.error);

        /* This is called when the GPS status alters */
        String mDismiss = "dimiss";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                String message = getResources().getString(R.string.out_of_service);

                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                String message2 = getResources().getString(R.string.connection_lost);

                // Show dialog
                DismissOnlyAlertDialog.showCustomDialog(getContext(), getActivity(), mDismiss, title, message2);
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
        Toast.makeText(getContext(), getResources().getString(R.string.turn_on_gps), Toast.LENGTH_LONG).show();
        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(viewIntent);
    }

    private void showError(String message, String title) {
        if (!TextUtils.isEmpty(title)) {
            title = "Information";
        }
        if (!TextUtils.isEmpty(message)) {
            message = "Oops we encountered and error";
        }
        DismissOnlyAlertDialog.showCustomDialog(getContext(),
                getActivity(), getString(R.string.dialog_dismiss),
                title, message);
    }
}
