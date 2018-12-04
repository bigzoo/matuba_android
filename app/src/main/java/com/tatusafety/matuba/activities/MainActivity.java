package com.tatusafety.matuba.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import com.tatusafety.matuba.R;
import com.tatusafety.matuba.fragments.LocationFragment;
import com.tatusafety.matuba.fragments.ReportsFragment;
import com.tatusafety.matuba.fragments.SpeedFragment;
import com.tatusafety.matuba.interfaces.NavigationInteraction;
import com.tatusafety.matuba.navigation.FragNavController;
import com.tatusafety.matuba.navigation.FragmentHistory;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends _BaseActivity implements NavigationInteraction, FragNavController.RootFragmentListener {

    private final String TAG = getClass().getSimpleName();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Context mContext;
    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomTabLayout;
    /**
     * Names for the navigation tabs
     */
    private String[] TABS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;
        checkLocationPermission();

        // Setup navigation
        fragmentHistory = new FragmentHistory();

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.main_layout)
                .rootFragmentListener(this)
                .build();

       openFragment(LocationFragment.newInstance());

        bottomTabLayout.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home_base:
                    openFragment(LocationFragment.newInstance());
                    return true;
                case R.id.speed:
                    openFragment(SpeedFragment.newInstance());
                    return true;
            }
            return false;
        }
    };

    private void openFragment(Fragment fragment) {
        FragmentManager fbet = getSupportFragmentManager();
        FragmentTransaction ftbet = fbet.beginTransaction();
        ftbet.replace(R.id.main_activity_frame_layout, fragment);
        ftbet.commit();
    }

    public void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext != null) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.permission_dialog_title)
                            .setMessage(R.string.permission_message_dialog)
                            .setPositiveButton(R.string.give_permission_button_dialog, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(MainActivity.this,
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    ContextCompat.checkSelfPermission(Objects.requireNonNull(MainActivity.this),
                            Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
        }
    }

    /**
     * onOptionsItemSelected method
     *
     * @param item item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method onBackPressed, handling the 'physical' back button
     */
    @Override
    public void onBackPressed() {

        if (fragmentHistory.isEmpty()) {
            super.onBackPressed();
        } else {
            if (fragmentHistory.getStackSize() < 1) {
                fragmentHistory.emptyStack();
            }
        }
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        } else {
            Log.i(TAG, " onSaveInstanceState : " + "mNaveController == null");
        }
    }

    /**
     * Add a fragment to the stack
     *
     * @param fragment fragment
     */
    @Override
    public void pushFragment(Fragment fragment) {
        if (!isFinishing() && mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void popFragment() {

    }

    @Override
    public void replaceFragment(Fragment fragment) {
        if (!isFinishing() && mNavController != null) {
            mNavController.replaceFragment(fragment);
        }
    }

    @Override
    public Fragment getCurrentFragment() {
        return mNavController.getCurrentFrag();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();

        recreate();
    }


}
