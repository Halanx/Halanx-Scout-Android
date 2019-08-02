package com.technicalrj.halanxscouts.Home.MoveOut.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.Home.MoveOut.AmenitiesResponse;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class RemarksFragment extends Fragment {

    private EditText remarks;
    private Button done_button;
    private TextView cancel;
    public static final String TAG = RemarksFragment.class.getName();

    private int taskId;
    private AmenitiesResponse.AmenityJsonData amenityJsonData;
    private String key;

    private RetrofitAPIClient.DataInterface dataInterface;

    private PropertyDetailsFragment.OnPropertyDetailsInteractionListener refreshTaskListListener;

    public static RemarksFragment newInstance(int taskId, AmenitiesResponse.AmenityJsonData amenityJsonData) {

        Bundle args = new Bundle();
        args.putInt("id", taskId);
        args.putParcelable(AmenitiesResponse.AMENITY_KEY, amenityJsonData);
        RemarksFragment fragment = new RemarksFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        taskId = bundle.getInt("id");
        amenityJsonData = bundle.getParcelable(AmenitiesResponse.AMENITY_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remarks, container, false);
        remarks = view.findViewById(R.id.remarks);
        cancel = view.findViewById(R.id.cancel_action);
        done_button   = view.findViewById(R.id.done_button);

        final SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAmenitiesAndRemarks();
            }
        });


        remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().equals("")){
                    enableButton(false, done_button);
                }else {
                    enableButton(true, done_button);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    private void enableButton(boolean val, Button button){

        if(val){
            button.setEnabled(true);
        } else {
            button.setEnabled(false);

        }

    }

    private void submitAmenitiesAndRemarks(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if(amenityJsonData.getAmenityData().getAmenityHashMap().size() > 0) {

            dataInterface.updateAmenities("Token " + key, taskId, amenityJsonData)
                    .enqueue(new Callback<AmenitiesResponse>() {
                        @Override
                        public void onResponse(Call<AmenitiesResponse> call, Response<AmenitiesResponse> response) {
                            progressDialog.dismiss();
                            if (response.code() == 200) {

                                String remark = remarks.getText().toString().trim();

                                if(remark.equals("")){
                                    markTaskAsComplete();
                                } else {
                                    submitRemarks();
                                }

                            } else {
                                showErrorDialog(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<AmenitiesResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.d(TAG, "onFailure: ");
                            t.printStackTrace();
                            showErrorDialog(false);

                        }
                    });
        } else {
            String remark = remarks.getText().toString().trim();
            if(remark.equals("")){
                markTaskAsComplete();
            } else {
                submitRemarks();
            }
        }
    }

    private void submitRemarks(){

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", remarks.getText().toString().trim());

        dataInterface.updateMoveOutRemarks("Token " + key, taskId, jsonObject)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.dismiss();

                        if (response.code() == 200) {

                            Log.i(TAG, "saveData succ:" + response.body());
//                                                enableButton(true, done_button);
                            markTaskAsComplete();

                        } else {
                            showErrorDialog(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                        Log.i(TAG, "saveData error:" + t.getMessage());
                        showErrorDialog(false);
                    }
                });
    }

    private void markTaskAsComplete(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

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
                .setMessage("Couldn't load data!")
                .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(isTaskComplete){
                            markTaskAsComplete();
                        } else {
                            submitAmenitiesAndRemarks();
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
}
