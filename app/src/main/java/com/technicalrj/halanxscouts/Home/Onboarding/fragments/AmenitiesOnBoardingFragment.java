package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.technicalrj.halanxscouts.Adapters.AmenityOnBoardingAdapter;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.AmenitiesFragment;
import com.technicalrj.halanxscouts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmenitiesOnBoardingFragment extends Fragment {

    private OnAmenitiesOnBoardingInteractionListener listener;

    public static AmenitiesOnBoardingFragment newInstance() {

        Bundle args = new Bundle();

        AmenitiesOnBoardingFragment fragment = new AmenitiesOnBoardingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AmenitiesOnBoardingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amenities_on_boarding, container, false);

        Button doneButton = view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAmenitiesUploaded();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnAmenitiesOnBoardingInteractionListener){
            listener = (OnAmenitiesOnBoardingInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnAmenitiesOnBoardingInteractionListener{

        void onAmenitiesUploaded();
    }

}
