package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technicalrj.halanxscouts.Constants;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.PropertyDetailsFragment;
import com.technicalrj.halanxscouts.Home.Onboarding.MapsActivity;
import com.technicalrj.halanxscouts.Pojo.LocationOnBoarding;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends Fragment {

    private static final int LOCATION_REQUEST_CODE = 11;
    private static final String TAG = AddressFragment.class.getSimpleName();
    private TextView locationTextView;
    private EditText streetEditText;
    private EditText cityEditText;
    private EditText stateEditText;

    private String houseLat;
    private String houseLng;

    private int taskId;
    private String key;

    private OnAddressFragmentInteractionListener listener;
    private PropertyDetailsFragment.OnPropertyDetailsInteractionListener refreshListener;

    private RetrofitAPIClient.DataInterface dataInterface;

    public static AddressFragment newInstance(int taskId) {

        Bundle args = new Bundle();
        args.putInt(Constants.TASK_ID, taskId);
        AddressFragment fragment = new AddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getArguments().getInt(Constants.TASK_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        locationTextView = view.findViewById(R.id.location_text_view);
        streetEditText = view.findViewById(R.id.street_address_edit_text);
        cityEditText = view.findViewById(R.id.city_edit_text);
        stateEditText = view.findViewById(R.id.state_edit_text);
        Button doneButton = view.findViewById(R.id.done_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAddress();
            }
        });

        dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);

        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), MapsActivity.class),LOCATION_REQUEST_CODE);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOCATION_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                String exactLocation = data.getStringExtra(Constants.LOCATION_DATA);
                houseLat = data.getStringExtra(Constants.LOCATION_LAT);
                houseLng = data.getStringExtra(Constants.LOCATION_LNG);
                Log.d(TAG, "onActivityResult: lat: "+houseLat);
                Log.d(TAG, "onActivityResult: lng: "+houseLng);
                locationTextView.setText(exactLocation);
            }
        }
    }

    private void submitAddress(){

        if(houseLat == null || houseLng == null){
            Toast.makeText(getActivity(), "Please mark house location on Map!", Toast.LENGTH_SHORT).show();
            return;
        }

        String street = streetEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();

        if(street.equals("")){
            Toast.makeText(getActivity(), "Please enter house number!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(city.equals("")){
            Toast.makeText(getActivity(), "Please enter city!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(state.equals("")){
            Toast.makeText(getActivity(), "Please enter state!", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationOnBoarding locationOnBoarding = new LocationOnBoarding(
                houseLng, houseLat, locationTextView.getText().toString(), street, city, state
        );

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dataInterface.updateAddress("Token " +key, taskId, locationOnBoarding)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.dismiss();

                        //Todo handle 500 response code

                        if(response.code() == 201 || response.code() == 500 || response.code()==409){
                            listener.onAddressUpdated();
                        } else {
                            Log.d(TAG, "onResponse: "+response.code());
                            showErrorDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: ");
                        t.printStackTrace();
                        showErrorDialog();
                    }
                });



    }


    private void showErrorDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                .setCancelable(false)
                .setMessage("Couldn't upload data!")
                .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        submitAddress();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(refreshListener != null){
                            refreshListener.onRefreshTaskList();
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnAddressFragmentInteractionListener){
            listener = (OnAddressFragmentInteractionListener) context;
        }

        if(context instanceof PropertyDetailsFragment.OnPropertyDetailsInteractionListener){
            refreshListener = (PropertyDetailsFragment.OnPropertyDetailsInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        refreshListener = null;
    }

    public interface OnAddressFragmentInteractionListener{

        void onAddressUpdated();
    }
}
