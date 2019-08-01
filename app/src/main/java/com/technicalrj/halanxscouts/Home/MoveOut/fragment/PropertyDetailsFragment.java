package com.technicalrj.halanxscouts.Home.MoveOut.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.technicalrj.halanxscouts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PropertyDetailsFragment extends Fragment {

    private Button done_button;
    private CheckBox checkBox;
    private OnPropertyDetailsInteractionListener listener;

    public PropertyDetailsFragment() {
        // Required empty public constructor
    }

    public static PropertyDetailsFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PropertyDetailsFragment fragment = new PropertyDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);

        done_button   = view.findViewById(R.id.done_button);
        checkBox = view.findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    enableButton(true);
                }else {
                    enableButton(false);
                }
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onCheckAmenitiesClicked();
                }
            }
        });

        return view;
    }

    public void checkAmenities(View view) {
//        startActivity(new Intent(get, AmenitiesFragment.class));
        if(listener != null) {
            listener.onCheckAmenitiesClicked();
        }
    }

    public void cancelTask(View view) {
    }

    public void enableButton(boolean val){

        if(val){
            done_button.setEnabled(true);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape));
        }else {
            done_button.setEnabled(false);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape_dark_grey));

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnPropertyDetailsInteractionListener){
            listener = (OnPropertyDetailsInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnPropertyDetailsInteractionListener{

        void onCheckAmenitiesClicked();
    }

}
