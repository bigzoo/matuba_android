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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tatusafety.matuba.R;
import com.valdesekamdem.library.mdtoast.MDToast;

/**
 * Created by incentro on 4/7/2018.
 */

public class TransportFragment extends Fragment implements View.OnClickListener {
    private EditText whereTo;
    private FloatingActionButton fab;
    TextView lat, longit;
    double latitude,longitude;
    LocationManager locManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.where_to, container, false);


    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        whereTo = view.findViewById(R.id.whereTo_et);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        longit = view.findViewById(R.id.longitude);
        lat = view.findViewById(R.id.latitude);
        checkLocationPermission();
        locManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        provider = locManager.getBestProvider(new Criteria(), false);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("I need it")
                        .setMessage("Give it to me")
                        .setPositiveButton("Aight", new DialogInterface.OnClickListener() {
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
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:

                        locManager.requestLocationUpdates(provider, 400, 1, new LocationListener() {

                            @Override
                            public void onLocationChanged(Location location) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                longit.append(String.format("%.2f", location.getLongitude()));
                                lat.append(String.format("%.2f", location.getLatitude()));
                                longit.setText((int)longitude);
                                lat.setText((int)latitude);

                                Log.d("LOGLong", String.valueOf(longitude));
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                Toast.makeText(getContext(), "GPS Updating", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                                Toast.makeText(getContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                                Toast.makeText(getContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }

                } else {

                   Toast.makeText(getContext(),"noo",Toast.LENGTH_SHORT).show();

                }
                return;
            }

        }

    }

    //when you click the floating action button
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                MDToast mdToast = MDToast.makeText(getContext(), "Searching....", MDToast.LENGTH_SHORT ,MDToast.TYPE_INFO );
                mdToast.show();
                break;
        }

    }
}
