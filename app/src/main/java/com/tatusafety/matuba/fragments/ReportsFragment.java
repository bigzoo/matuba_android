package com.tatusafety.matuba.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tatusafety.matuba.R;

/**
 * Created by incentro on 4/7/2018.
 */

public class ReportsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reporting_fragment, container, false);
    }
}