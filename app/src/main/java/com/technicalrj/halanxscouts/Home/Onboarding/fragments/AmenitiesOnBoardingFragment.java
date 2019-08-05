package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.technicalrj.halanxscouts.Adapters.AmenityOnBoardingAdapter;
import com.technicalrj.halanxscouts.Constants;
import com.technicalrj.halanxscouts.Home.MoveOut.AmenitiesResponse;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.AmenitiesFragment;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.PropertyDetailsFragment;
import com.technicalrj.halanxscouts.Pojo.AmenityOnBoarding;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmenitiesOnBoardingFragment extends Fragment implements AmenityOnBoardingAdapter.OnAmenityOnBoardingClickListener {

    private OnAmenitiesOnBoardingInteractionListener listener;
    private PropertyDetailsFragment.OnPropertyDetailsInteractionListener refreshTaskListListener;
    private ArrayList<AmenityOnBoarding>  amenityArrayList;
    private AmenityOnBoardingAdapter amenityAdapter;
    private RecyclerView amenityRecyclerView;
    private String key;

    private int taskId;

    private RetrofitAPIClient.DataInterface halanxDataInterface;
    private RetrofitAPIClient.DataInterface scoutDataInterface;

    private String TAG = AmenitiesFragment.class.getSimpleName();

    public static AmenitiesOnBoardingFragment newInstance(int taskId) {

        Bundle args = new Bundle();
        args.putInt(Constants.TASK_ID, taskId);
        AmenitiesOnBoardingFragment fragment = new AmenitiesOnBoardingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getArguments().getInt(Constants.TASK_ID);
    }

    public AmenitiesOnBoardingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amenities_on_boarding, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        Button doneButton = view.findViewById(R.id.done_button);
        amenityRecyclerView = view.findViewById(R.id.amenity_recycler_view);

        amenityArrayList = new ArrayList<>();
        amenityAdapter = new AmenityOnBoardingAdapter(getActivity(), amenityArrayList, this);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        amenityRecyclerView.setLayoutManager(layoutManager);
        amenityRecyclerView.setAdapter(amenityAdapter);

        halanxDataInterface = RetrofitAPIClient.getHalanxRetrofitClient().create(RetrofitAPIClient.DataInterface.class);
        scoutDataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);

        getListOfAmenities();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, AmenitiesResponse.Amenity> selectedAmenityMap = getSelectedAmenityMap();
                if(selectedAmenityMap.size() == 0){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage("You haven't selected any amenity!")
                            .setPositiveButton("Select Amenity", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    listener.onAmenitiesUploaded();
                                }
                            })
                            .create();
                    alertDialog.show();
                } else {
                    uploadAmenities(selectedAmenityMap);
                }
            }
        });

        return view;
    }

    private void getListOfAmenities(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        halanxDataInterface.getListOfAllAmenities()
                .enqueue(new Callback<ArrayList<AmenityOnBoarding>>() {
                    @Override
                    public void onResponse(Call<ArrayList<AmenityOnBoarding>> call, Response<ArrayList<AmenityOnBoarding>> response) {
                        progressDialog.dismiss();
                        if(response.code() == 200 && response.body() != null){
                            amenityArrayList.addAll(response.body());
                            amenityAdapter.notifyDataSetChanged();
                        } else {
                            showErrorDialog(false);
                            Log.d(TAG, "onResponse: "+response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<AmenityOnBoarding>> call, Throwable t) {
                        progressDialog.dismiss();
                        showErrorDialog(false);
                        t.printStackTrace();
                        Log.d(TAG, "onFailure: ");
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnAmenitiesOnBoardingInteractionListener){
            listener = (OnAmenitiesOnBoardingInteractionListener) context;
        }

        if(context instanceof PropertyDetailsFragment.OnPropertyDetailsInteractionListener){
            refreshTaskListListener = (PropertyDetailsFragment.OnPropertyDetailsInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        refreshTaskListListener = null;
    }

    @Override
    public void onAmenityClicked(View rootView, ImageView selectImageView) {
        Log.d(TAG, "onAmenityClicked: "+selectImageView.isActivated());
        int position = amenityRecyclerView.getChildAdapterPosition(rootView);
        AmenityOnBoarding amenityOnBoarding = amenityArrayList.get(position);
        selectImageView.setActivated(!selectImageView.isActivated());
        amenityOnBoarding.setSelected(selectImageView.isActivated());
        amenityArrayList.set(position, amenityOnBoarding);
    }

    public interface OnAmenitiesOnBoardingInteractionListener{

        void onAmenitiesUploaded();
    }

    private HashMap<String, AmenitiesResponse.Amenity> getSelectedAmenityMap(){
        HashMap<String, AmenitiesResponse.Amenity> hashMap = new HashMap<>();
        for(AmenityOnBoarding amenityOnBoarding : amenityArrayList){
            if(amenityOnBoarding.isSelected()){
                AmenitiesResponse.Amenity amenity = new AmenitiesResponse.Amenity(amenityOnBoarding.getId());
                hashMap.put(String.valueOf(amenityOnBoarding.getId()), amenity);
            }
        }
        return hashMap;
    }

    private void uploadAmenities(HashMap<String, AmenitiesResponse.Amenity> selectedAmenityMap){

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AmenitiesResponse.AmenityJsonData amenityJsonData = new AmenitiesResponse.AmenityJsonData(
                new AmenitiesResponse.AmenityData(selectedAmenityMap)
        );

        scoutDataInterface.updateOnBoardingAmenities("Token "+key, taskId, amenityJsonData)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        progressDialog.dismiss();

                        if(response.code() == 200){
                           listener.onAmenitiesUploaded();
                        } else {
                            Log.d(TAG, "onResponse: error code: "+response.code());
                            showErrorDialog(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        progressDialog.dismiss();

                        showErrorDialog(true);
                        Log.d(TAG, "onFailure: ");
                        t.printStackTrace();

                    }
                });
    }

    private void showErrorDialog(final boolean isUploadingSelectedAmenities){
        String alertMessage;
        if(isUploadingSelectedAmenities){
            alertMessage = "Error submitting data!";
        } else {
            alertMessage = "Error loading data!";
        }
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage(alertMessage)
                .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(isUploadingSelectedAmenities){
                            uploadAmenities(getSelectedAmenityMap());
                        } else {
                            getListOfAmenities();
                        }

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(refreshTaskListListener != null){
                            refreshTaskListListener.onRefreshTaskList();
                        }
                    }
                })
                .create();
        alertDialog.show();
    }


}
