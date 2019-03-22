package com.tatusafety.matuba.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tatusafety.matuba.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpamFragment extends Fragment {


    public SpamFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new SpamFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spam, container, false);
    }

}
