package com.tatusafety.matuba.fragments;

/**
 * Created by ADMIN on 4/8/2018.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tatusafety.matuba.R;

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
        tvLatitude = (TextView) view.findViewById(R.id.tvLatitudeAuto);
        tvLongitude = (TextView) view.findViewById(R.id.tvLongitudeAuto);
    }


//    @SuppressWarnings("depreciation")
//    public void setDate(View view) {
////        showDialog(999);
//    }
//    @Override
//    protected Dialog onCreateDialog(int id){
//        if (id == 999){
//            return new DatePickerDialog(this,myDateListener, year,month,day);
//        }
//        return null;
//    }
//    private DatePickerDialog.OnDateSetListener myDateListener = new
//            DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int month, int day) {
//                    showDate(year,month,day);
//                }
//            };
//    private void showDate(int year, int month, int day) {
//        tvDateView.setText(new StringBuilder().append(day).append("/")
//                .append(month).append("/").append(year));
//    }
//    public void setTime(View v){
//        DialogFragment newFragment = new TimePickerFragment();
//        newFragment.show(getFragmentManager(),"TimePicker");
//    }
    //id,latitude,longitude,date,time,road,sacco,speed,plates,county,extras,status
    public void submitReport() {
        final String Latitude = Double.toString(latitude);
        final String Longitude = Double.toString(longitude);
        final String Date = tvDateView.getText().toString();
        final String Time = tvTimeView.getText().toString();
        final String Road = road.getText().toString().trim();
        final String Sacco = sacco.getText().toString().trim();
        final String Speed = Float.toString(speed);
        final String Plates = plates.getText().toString();
        final String County = county.getText().toString();
        final String Extras = extras.getText().toString();
        final String Status = "unsynced";
        final String Uuid = java.util.UUID.randomUUID().toString();
//        if (speed<0) {
//            Log.d("DATE PICKED",Date);
//            Toast.makeText(this, "You cannot submit report with a speed value less than 60!", Toast.LENGTH_SHORT).show();
//        }
//        else {
//        Report report = new Report(null,Latitude,Longitude,Date,Time,Road,Sacco,Speed,Plates,County,Extras,Status,Uuid);
//
//        db.insertReport(report);
        //Toast.makeText(this, "Total Submitted is "+db.countRecords(), Toast.LENGTH_SHORT).show();
        tvDateView.setText("");
        tvTimeView.setText("");
        road.setText("");
        sacco.setText("");
        plates.setText("");
        county.setText("");
        extras.setText("");
//        Intent i=new Intent(this,SyncService.class);
//        this.startService(i);
//        db.fetchUnsyncedRecords();
//        Intent intent = new Intent(NewReportActivity.this,HistoryActivity.class);
//        Toast.makeText(this, "Record sent succesfully", Toast.LENGTH_SHORT).show();
//        startActivity(intent);
//        Log.d("JSON_DATA", db.fetchUnsyncedRecords());
//        }
    }

}
