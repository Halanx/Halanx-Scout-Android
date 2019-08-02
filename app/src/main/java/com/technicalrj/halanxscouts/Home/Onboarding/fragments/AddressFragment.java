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
public class AddressFragment extends Fragment {

    public static AddressFragment newInstance() {

        Bundle args = new Bundle();
        AddressFragment fragment = new AddressFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address, container, false);
    }



}
