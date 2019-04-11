package com.tatusafety.matuba.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tatusafety.matuba.R
import com.tatusafety.matuba.interfaces.MainActivityCallBack
import com.tatusafety.matuba.utils.GlobalUtils
import kotlinx.android.synthetic.main.navigation_activity.*
import java.util.*

const val MY_PERMISSIONS_REQUEST_LOCATION = 99

class MainActivity : AppCompatActivity(), MainActivityCallBack {

    private val TAG = javaClass.simpleName
    private var mContext: Context? = null
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                ?: return

        val navController = host.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)

        val topLevelDestinationIds = setOf(R.id.home_dest, R.id.traffic, R.id.speed_dest)
        appBarConfiguration = AppBarConfiguration(topLevelDestinationIds, drawer_layout)

        setupActionBar(navController, appBarConfiguration)

        setupNavigationMenu(navController)

        setupBottomNavMenu(navController)

    }

    // set up bottom navigation
    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = bottom_navigation
        bottomNav?.setupWithNavController(navController)
    }

    private fun setupNavigationMenu(navController: NavController) {
        val sideNavView = nav_view
        sideNavView?.setupWithNavController(navController)
    }


    //    Show a title in the ActionBar based off of the destination's label
//    Display the Up button whenever you're not on a top-level destination
//    Display a drawer icon (hamburger icon) when you're on a top-level destination
    private fun setupActionBar(navController: NavController,
                               appBarConfig: AppBarConfiguration) {

//        // This allows NavigationUI to decide what label to show in the action bar
//        // By using appBarConfig, it will also determine whether to
//        // show the up arrow or drawer menu icon
        setupActionBarWithNavController(navController, appBarConfig)

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    override fun checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "********* request for permissions")

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {

                    AlertDialog.Builder(this@MainActivity)
                            .setTitle(R.string.permission_dialog_title)
                            .setMessage(R.string.permission_message_dialog)
                            .setPositiveButton(R.string.give_permission_button_dialog) { dialogInterface, i ->
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(this@MainActivity,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                        MY_PERMISSIONS_REQUEST_LOCATION)
                            }
                            .create()
                            .show()
                } else {

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION)
                }
            } else {
                Log.e(TAG, "********* permissions granted")

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "********* permissions given")

                    GlobalUtils.locationsGiven = true

                    // permission was granted
                    ContextCompat.checkSelfPermission(Objects.requireNonNull<MainActivity>(this@MainActivity),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }
}