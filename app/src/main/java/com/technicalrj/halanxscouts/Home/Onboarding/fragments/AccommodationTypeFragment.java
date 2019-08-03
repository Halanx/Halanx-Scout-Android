package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technicalrj.halanxscouts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccommodationTypeFragment extends Fragment {

    public static AccommodationTypeFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AccommodationTypeFragment fragment = new AccommodationTypeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AccommodationTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accommodation_type, container, false);
    }

}
