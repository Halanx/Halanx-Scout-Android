package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.technicalrj.halanxscouts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadPhotosFragment extends Fragment {

    private OnUploadPhotoInteractionListener listener;

    public UploadPhotosFragment() {
        // Required empty public constructor
    }

    public static UploadPhotosFragment newInstance() {

        Bundle args = new Bundle();

        UploadPhotosFragment fragment = new UploadPhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_photos, container, false);

        Button doneButton = view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoUploaded();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnUploadPhotoInteractionListener){
            listener = (OnUploadPhotoInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnUploadPhotoInteractionListener{
        void onPhotoUploaded();
    }

}
