package com.tatusafety.matuba.fragments

import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tatusafety.matuba.R


open class _BaseFragment : Fragment() {


    /**
     * Used to show a snackbar with a custom error message
     */
    fun showSnackBar(string: String, isError: Boolean, view: View?) {

        val snackbar = view?.let { Snackbar.make(it, string, Snackbar.LENGTH_LONG) }
        val snackBarView = snackbar?.view

        // set the background color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isError) {
            context?.getColor(R.color.lightRed)?.let { snackBarView?.setBackgroundColor(it) }
        }
        snackbar?.show()
    }
}
