package com.tatusafety.matuba;

/**
 * Created by ADMIN on 4/8/2018.
 */

import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by incentro on 4/7/2018.
 */

public class SummaryFragment extends Fragment {

    TextView tvLatitude, tvLongitude, tvSpeed, tvDateView, tvTimeView;
    LocationManager locManager;
    double latitude, longitude;
    float speed;
    Calendar calendar;
    int year, month, day;
    EditText road, sacco, plates, county, extras;
    Button btnSubmit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        road = (EditText) view.findViewById(R.id.edtRoadName);
//        sacco = (EditText) view.findViewById(R.id.edtSacco);
//        plates = (EditText) view.findViewById(R.id.edtPlates);
//        county = (EditText) view.findViewById(R.id.edtCounty);
//        extras = (EditText) view.findViewById(R.id.edtExtras);
//        tvTimeView = (TextView) view.findViewById(R.id.tvTimeView);
//        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
//
//        //datepicker action
//        tvDateView = (TextView) view.findViewById(R.id.tvDateView);
//        //tvDateView.setVisibility(tvDateView.INVISIBLE);
//        calendar = Calendar.getInstance();
//        year = calendar.get(Calendar.YEAR);
//        month = calendar.get(Calendar.MONTH);
//        day = calendar.get(Calendar.DAY_OF_MONTH);
//        showDate(year, month, day);
    }

}
