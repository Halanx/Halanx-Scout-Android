package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.Constants;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.PropertyDetailsFragment;
import com.technicalrj.halanxscouts.Pojo.PropertyOnBoarding;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccommodationTypeFragment extends Fragment {

    private static final String TAG = AccommodationTypeFragment.class.getSimpleName();
    private int taskId;

    private TextView fullyFurnishedTextView;
    private TextView semiFurnishedTextView;
    private TextView unfurnishedTextView;
    private TextView furnishSelectedTv;

    private TextView bhk1Tv;
    private TextView bhk2Tv;
    private TextView bhk3Tv;
    private TextView bhk4Tv;
    private TextView bhk5Tv;
    private TextView bhkSelectedTv;
    private EditText bhkEditText;

    private TextView sharedRoomTextView;
    private TextView privateRoomTextView;
    private TextView entireHouseTextView;

    private RetrofitAPIClient.DataInterface dataInterface;

    private PropertyDetailsFragment.OnPropertyDetailsInteractionListener refreshTaskListListener;

    private Button submitButton;

    private ArrayList<String> accommodationTypeList;
    private String key;

    private ProgressDialog progressDialog;

    public static AccommodationTypeFragment newInstance(int taskId) {
        
        Bundle args = new Bundle();
        args.putInt(Constants.TASK_ID, taskId);
        AccommodationTypeFragment fragment = new AccommodationTypeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AccommodationTypeFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_accommodation_type, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        fullyFurnishedTextView = view.findViewById(R.id.fully_furnished_tv);
        semiFurnishedTextView = view.findViewById(R.id.semi_furnished_tv);
        unfurnishedTextView = view.findViewById(R.id.unfurnished_tv);

        bhk1Tv = view.findViewById(R.id.bhk_1_tv);
        bhk2Tv = view.findViewById(R.id.bhk_2_tv);
        bhk3Tv = view.findViewById(R.id.bhk_3_tv);
        bhkEditText = view.findViewById(R.id.bhk_other_et);

        sharedRoomTextView = view.findViewById(R.id.share_room_tv);
        privateRoomTextView = view.findViewById(R.id.private_room_tv);
        entireHouseTextView = view.findViewById(R.id.entire_house_tv);

        submitButton = view.findViewById(R.id.submit_button);

        dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);

        accommodationTypeList = new ArrayList<>();

        fullyFurnishedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fullyFurnishedTextView.isActivated()){
                    furnishSelectedTv = fullyFurnishedTextView;
                    return;
                }
                fullyFurnishedTextView.setActivated(true);
                if(furnishSelectedTv != null){
                    furnishSelectedTv.setActivated(false);
                }
                furnishSelectedTv = fullyFurnishedTextView;
            }
        });

        semiFurnishedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(semiFurnishedTextView.isActivated()){
                    furnishSelectedTv = semiFurnishedTextView;
                    return;
                }
                semiFurnishedTextView.setActivated(true);
                if(furnishSelectedTv != null){
                    furnishSelectedTv.setActivated(false);
                }
                furnishSelectedTv = semiFurnishedTextView;
            }
        });

        unfurnishedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unfurnishedTextView.isActivated()){
                    furnishSelectedTv = unfurnishedTextView;
                    return;
                }
                unfurnishedTextView.setActivated(true);
                if(furnishSelectedTv != null){
                    furnishSelectedTv.setActivated(false);
                }
                furnishSelectedTv = unfurnishedTextView;
            }

        });

        bhk1Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bhk1Tv.isActivated()){
                    bhkSelectedTv = bhk1Tv;
                    return;
                }
                bhk1Tv.setActivated(true);
                if(bhkSelectedTv != null){
                    bhkSelectedTv.setActivated(false);
                }
                bhkSelectedTv = bhk1Tv;
                bhkEditText.setText("");
            }
        });

        bhk2Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bhk2Tv.isActivated()){
                    bhkSelectedTv = bhk2Tv;
                    return;
                }
                bhk2Tv.setActivated(true);
                if(bhkSelectedTv != null){
                    bhkSelectedTv.setActivated(false);
                }
                bhkSelectedTv = bhk2Tv;
                bhkEditText.setText("");
            }
        });

        bhk3Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bhk3Tv.isActivated()){
                    bhkSelectedTv = bhk3Tv;
                    return;
                }
                bhk3Tv.setActivated(true);
                if(bhkSelectedTv != null){
                    bhkSelectedTv.setActivated(false);
                }
                bhkSelectedTv = bhk3Tv;
                bhkEditText.setText("");
            }
        });

        sharedRoomTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedRoomTextView.isActivated()){
                    sharedRoomTextView.setActivated(false);
                    accommodationTypeList.remove(Constants.SHARED_ROOM);
                } else {
                    sharedRoomTextView.setActivated(true);
                    accommodationTypeList.add(Constants.SHARED_ROOM);
                }
            }
        });

        privateRoomTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privateRoomTextView.isActivated()){
                    privateRoomTextView.setActivated(false);
                    accommodationTypeList.remove(Constants.PRIVATE_ROOM);
                } else {
                    privateRoomTextView.setActivated(true);
                    accommodationTypeList.add(Constants.PRIVATE_ROOM);
                }
            }
        });

        entireHouseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entireHouseTextView.isActivated()){
                    entireHouseTextView.setActivated(false);
                    accommodationTypeList.remove(Constants.FLAT);
                } else {
                    entireHouseTextView.setActivated(true);
                    accommodationTypeList.add(Constants.FLAT);
                }
            }
        });

        bhkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if(length >= 1){
                    if(bhkSelectedTv != null){
                        bhkSelectedTv.setActivated(false);
                        bhkSelectedTv = null;
                    }
                }

                if(length == 2){
                    hideKeyboard();
                }
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDetails();
            }
        });

        return view;
    }

    private void submitDetails(){
        if(furnishSelectedTv == null){
            Toast.makeText(getActivity(), "Please select furnish type!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(bhkEditText.getText().toString().isEmpty() && bhkSelectedTv == null){
            Toast.makeText(getActivity(), "Please select bed count!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(accommodationTypeList.size() == 0){
            Toast.makeText(getActivity(), "Please select accommodation type!", Toast.LENGTH_SHORT).show();
            return;
        }


        int bhkCount = 0;
        if(bhkSelectedTv == null){
            bhkCount = Integer.valueOf(bhkEditText.getText().toString());
        } else if(bhkSelectedTv == bhk1Tv){
            bhkCount = 1;
        } else if(bhkSelectedTv == bhk2Tv){
            bhkCount = 2;
        } else if(bhkSelectedTv == bhk3Tv){
            bhkCount = 3;
        } else if(bhkSelectedTv == bhk4Tv){
            bhkCount = 4;
        } else if(bhkSelectedTv == bhk5Tv){
            bhkCount = 5;
        }
        Log.d(TAG, "onClick: bhk count: "+bhkCount);

        String furnishType = "";
        if(furnishSelectedTv == fullyFurnishedTextView){
            furnishType = Constants.FURNISHED;
        } else if(furnishSelectedTv == semiFurnishedTextView){
            furnishType = Constants.SEMI_FURNISHED;;
        } else {
            furnishType = Constants.UNFURNISHED;;
        }

        Log.d(TAG, "onClick: furnish type: "+furnishType);

        for(String accommodation : accommodationTypeList){
            Log.d(TAG, "submitDetails: "+accommodation);
        }

        progressDialog.show();

        final PropertyOnBoarding propertyOnBoarding = new PropertyOnBoarding(furnishType, accommodationTypeList, bhkCount);
//        propertyOnBoarding.setRent(2000);
        Log.d(TAG, "submitDetails: task id: "+taskId);
        dataInterface.uploadOnBoardingPropertyDetails("Token "+key, taskId, propertyOnBoarding)
                .enqueue(new Callback<PropertyOnBoarding>() {
                    @Override
                    public void onResponse(Call<PropertyOnBoarding> call, Response<PropertyOnBoarding> response) {
                        if(response.code() == 201){
                            markTaskAsComplete();
                        } else if(response.code() == 409){
                            progressDialog.dismiss();
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                    .setCancelable(false)
                                    .setMessage("House details already submitted!")
                                    .setPositiveButton("Mark task as done", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            markTaskAsComplete();
                                        }
                                    })
                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            refreshTaskListListener.onRefreshTaskList();
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                        } else {
                            Log.d(TAG, "onResponse: code: "+response.code());
                            Log.d(TAG, "onResponse: code: "+response.message());
                            try {
                                Log.d(TAG, "onResponse: code: "+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            showErrorDialog(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<PropertyOnBoarding> call, Throwable t) {
                        Log.d(TAG, "onFailure: ");
                        progressDialog.dismiss();
                        t.printStackTrace();
                        showErrorDialog(false);
                    }
                });
    }

    private void markTaskAsComplete(){

        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        if(!progressDialog.isShowing()){
           progressDialog.show();
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("complete",true);
        dataInterface.setTaskComplete(taskId, jsonObject, "Token "+key)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.dismiss();
                        if(response.code() == 200){
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                                    .setCancelable(false)
                                    .setMessage("Task completed successfully!")
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            refreshTaskListListener.onRefreshTaskList();
                                        }
                                    }).create();
                            alertDialog.show();
                        } else {
                            showErrorDialog(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: ");
                        t.printStackTrace();
                        showErrorDialog(true);
                    }
                });
    }

    private void showErrorDialog(final boolean isTaskComplete){

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                .setCancelable(false)
                .setMessage("Error in submitting data!")
                .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(isTaskComplete){
                            markTaskAsComplete();
                        } else {
                            submitDetails();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        refreshTaskListListener.onRefreshTaskList();
                    }
                })
                .create();
        alertDialog.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof PropertyDetailsFragment.OnPropertyDetailsInteractionListener){
            refreshTaskListListener = (PropertyDetailsFragment.OnPropertyDetailsInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        refreshTaskListListener = null;
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
