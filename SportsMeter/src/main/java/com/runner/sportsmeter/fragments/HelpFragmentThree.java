package com.runner.sportsmeter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.runner.sportsmeter.R;

/**
 * Created by angelr on 07-Dec-15.
 */
public class HelpFragmentThree extends Fragment {

    public HelpFragmentThree() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.help_fragment_three, container, false);
    }
}
