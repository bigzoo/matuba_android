package com.tatusafety.matuba.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tatusafety.matuba.R;

/**
 * Created by incentro on 4/7/2018.
 */

public class ReportsFragment extends Fragment {

    private TextView eta_tv, duration_tv, distance_tv, fare_tv, description_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_directions_xml, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eta_tv = view.findViewById(R.id.eta_tv);
        duration_tv = view.findViewById(R.id.duration_tv);
        distance_tv = view.findViewById(R.id.total_distance_tv);
        fare_tv = view.findViewById(R.id.fare_tv);
        description_tv = view.findViewById(R.id.description_tv);

    }

}
