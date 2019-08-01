package com.technicalrj.halanxscouts.Home.MoveOut.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    private String key;

    public static RemarksFragment newInstance() {

        Bundle args = new Bundle();

        RemarksFragment fragment = new RemarksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remarks, container, false);
        remarks = view.findViewById(R.id.remarks);
        cancel = view.findViewById(R.id.cancel_action);
        done_button   = view.findViewById(R.id.done_button);


        taskId = getArguments().getInt("id");


        final SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTask(v);
            }
        });



        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRemark(v);
            }
        });




        remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().equals("")){
                    enableButton(false);
                }else {
                    enableButton(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
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

    public void submitRemark(View view) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("complete",true);
        jsonObject.addProperty("remark",remarks.getText().toString().trim());

        Call<Void> call = availabilityInterface.setTaskComplete(taskId,jsonObject,"Token "+key);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();

                /*if(response.isSuccessful()){

                }else {
                    try {
                        Log.i(TAG, "onResponse: "+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

                Log.i(TAG,"saveData succ:"+response.body());

                Intent returnIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Log.i(TAG,"saveData error:"+t.getMessage());
            }
        });

    }

    public void cancelTask(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
                        Call<String> call = availabilityInterface.cancelTask(taskId,"Token "+key);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                progressDialog.dismiss();
                                Log.i("InfoText","cancelTask:"+response.body());

                                Intent returnIntent = new Intent();
                                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                                getActivity().finish();

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                progressDialog.dismiss();

                            }
                        });



                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogCustom);
        builder.setMessage("Are you sure to Cancel this task?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();

    }
}
