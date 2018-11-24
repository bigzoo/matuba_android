package com.tatusafety.matuba.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.tatusafety.matuba.R;

/**
 * Base activity containing standard onCreate
 * <p>
 * Created by Kilasi on 24/11/2018.
 */
public abstract class _BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // log the build type
//        Log.d("Build", "Started with build '" + GlobalVariables.CURRENT_BUILD.buildType + "'");
    }

    public Toolbar setupToolBar(boolean homeAsUpEnabled, String title) {
        // setup toolbar and obtain it
        Toolbar toolbar = setupToolBar(homeAsUpEnabled);

        // obtain the views of title
        TextView titleTv = toolbar.findViewById(R.id.toolbar_title);

        if (titleTv != null) {
            // set title
            titleTv.setText(title);
        }

        return toolbar;
    }

    public Toolbar setupToolBar(boolean homeAsUpEnabled) {

        // setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        AppCompatActivity activity = this;

        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        ActionBar actionBar = activity != null ? activity.getSupportActionBar() : null;
        if (actionBar != null) {

            // settings for actionbar
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(homeAsUpEnabled);
        }

        return toolbar;
    }
}
