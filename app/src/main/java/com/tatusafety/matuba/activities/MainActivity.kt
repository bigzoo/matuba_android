package com.tatusafety.matuba.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.tatusafety.matuba.R
import java.util.*

const val MY_PERMISSIONS_REQUEST_LOCATION = 99

class MainActivity : _BaseActivity() {
    private val TAG = javaClass.simpleName
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkLocationPermission()

    }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        //fragmentTransaction.replace(R.id.main_activity_frame_layout, fragment)
        fragmentTransaction.commit()
    }

    fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    ContextCompat.checkSelfPermission(Objects.requireNonNull<MainActivity>(this@MainActivity),
                            Manifest.permission.ACCESS_FINE_LOCATION)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()

    }
}