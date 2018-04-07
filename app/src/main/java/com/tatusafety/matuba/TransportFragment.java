package com.tatusafety.matuba;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by incentro on 4/7/2018.
 */

public class TransportFragment extends Fragment implements View.OnClickListener {
    private EditText whereTo;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.where_to, container, false);


    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        whereTo = view.findViewById(R.id.whereTo_et);
        fab = view.findViewById(R.id.fab);
    }

    //when you click the floating action button
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                break;
        }

    }
}
