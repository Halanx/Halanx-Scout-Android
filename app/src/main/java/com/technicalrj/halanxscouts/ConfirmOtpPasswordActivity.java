package com.technicalrj.halanxscouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;

public class ConfirmOtpPasswordActivity extends AppCompatActivity {

    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;

    private String phoneNumber;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp_password);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);


        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                otp2.requestFocus();
            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                otp3.requestFocus();
            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                otp4.requestFocus();
            }
        });


        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmOtp(otp4);
            }
        });


    }

    public void confirmOtp(View view) {

        String otp11 = otp1.getText().toString();
        String otp22 = otp2.getText().toString();
        String otp33 = otp3.getText().toString();
        String otp44 = otp4.getText().toString();


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");


        if(otp11.isEmpty() ||otp22.isEmpty()  || otp33.isEmpty()  || otp44.isEmpty()  ){
            Toast.makeText(this,"Otp Not Valid",Toast.LENGTH_LONG).show();
        }else {
            final String otp = otp11 + otp22 + otp33 + otp44;



            Log.i("InfoText","otp :"+otp+ "phone :"+phoneNumber);

            progressDialog.show();

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username","s"+phoneNumber)
                    .addFormDataPart("password",otp)
                    .build();

            final Request request = new Request.Builder()
                    .url(halanxScout+"/scouts/login_otp/")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    final int code = response.networkResponse().code();

                    Log.i("InfoText","code : "+code);




                    if(response.isSuccessful()){
                        String body = response.body().string();
                        try {

                            progressDialog.dismiss();

                            JSONObject jsonObject = new JSONObject(body);
                            String key = jsonObject.get("key").toString();



                            Intent intent = new Intent(ConfirmOtpPasswordActivity.this, EnterPasswordActivity.class);
                            intent.putExtra("key",key);
                            intent.putExtra("otp",otp);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }





                    }else {
                        ConfirmOtpPasswordActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();

                                if(code==400||code==404){
                                    Toast.makeText(ConfirmOtpPasswordActivity.this,"Incorrect Otp", Toast.LENGTH_SHORT).show();
                                }else if(code==409){
                                    Toast.makeText(ConfirmOtpPasswordActivity.this,"Scout with this Phone Number already exists!", Toast.LENGTH_SHORT).show();
                                }



                            }
                        });
                    }

                }
            });



        }


    }


    public void resentOtp(View view) {


        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, "{}");

        Request request = new Request.Builder()
                .url("https://scout-api.halanx.com/scouts/get_otp/" + phoneNumber + "/")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });



    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }


}
