package com.technicalrj.halanxscouts.Home.MoveOut.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.technicalrj.halanxscouts.Adapters.AmenetiesAdapter;
import com.technicalrj.halanxscouts.R;

public class AmenitiesFragment extends Fragment {


    private RecyclerView amenitiesRecycler;
    private AmenetiesAdapter amenetiesAdapter;
    private Button done_button;
    private TextView backTextView;

    private OnAmenitiesInteractionListener listener;

    public static AmenitiesFragment newInstance() {

        Bundle args = new Bundle();

        AmenitiesFragment fragment = new AmenitiesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amenities, container, false);

        done_button   = view.findViewById(R.id.done_button);
        backTextView = view.findViewById(R.id.cancel_action);
        amenitiesRecycler = view.findViewById(R.id.amenities_recyclerview);

        amenetiesAdapter = new AmenetiesAdapter(getActivity());
        amenitiesRecycler.setAdapter(amenetiesAdapter);
        amenitiesRecycler.setNestedScrollingEnabled(false);
        amenitiesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onLoadRemarksClicked();
                }
            }
        });

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onAmenitiesBackPressed();
                }
            }
        });

        enableButton(true);
        return view;
    }

    public void goToRemark(View view) {

    }



    private void enableButton(boolean val){

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
        if(context instanceof OnAmenitiesInteractionListener){
            listener = (OnAmenitiesInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnAmenitiesInteractionListener{

        void onLoadRemarksClicked();

        void onAmenitiesBackPressed();
    }
}
