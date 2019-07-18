package com.technicalrj.halanxscouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;

public class ConfirmOtpRegisterActivity extends AppCompatActivity {

    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp_register);



        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);

        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        password = getIntent().getStringExtra("password");





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
            String otpString = otp11 + otp22 + otp33 + otp44;
            int otp = Integer.valueOf(otpString);




            progressDialog.show();


            OkHttpClient client = new OkHttpClient();



            String json = "{ \"first_name\" : \""+ firstName+ "\","
                            +"\"last_name\": \""+ lastName+ "\","
                            +"\"phone_no\": \""+ phoneNumber+ "\", "
                            +"\"otp\": "+otp+", "
                            +"\"password\": \""+ password+ "\","
                            +"\"email\": \""+email+ "\" "
                            +" }" ;



            RequestBody body = RequestBody.create(JSON, json);

            /*RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("first_name",firstName)
                    .addFormDataPart("last_name",lastName)
                    .addFormDataPart("phone_no",phoneNumber)
                    .addFormDataPart("otp",otp)
                    .addFormDataPart("password",password)
                    .addFormDataPart("email",email)
                    .build();*/


            Log.i("InfoText",firstName + lastName + phoneNumber +otp+password + email);

            final Request request = new Request.Builder()
                    .url(halanxScout+"/scouts/register/")
                    .addHeader("Content-Type","application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    Log.i("InfoText","Error in register");
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    final int code = response.networkResponse().code();



                    //Log.i("InfoText","responsebody :"+response1.toString());
                    Log.i("InfoText","responseMessge :"+response.networkResponse().message());
                    Log.i("InfoText","responseBody :"+response.networkResponse().body());

                    Log.i("InfoText","code regiser: "+code);

                    String messageString = null;
                    try {
                        JSONObject jsonObject= new JSONObject (response.body().string());
                        messageString =jsonObject.toString();
                        Log.i("InfoText","Detail :"+messageString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }






                    if(response.isSuccessful()){
                        try {

                            progressDialog.dismiss();

                            JSONObject jsonObject = new JSONObject(messageString);
                            String key = jsonObject.get("key").toString();


                            SharedPreferences.Editor editor = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE).edit();
                            editor.putString("login_key", key);
                            editor.apply();



                            Intent intent = new Intent(ConfirmOtpRegisterActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }





                    }else {
                        ConfirmOtpRegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();



                                if(code==400){
                                    Toast.makeText(ConfirmOtpRegisterActivity.this,"Incorrect Otp Or Scout with this Phone Number already exists!", Toast.LENGTH_SHORT).show();
                                }else if(code==409){
                                    Toast.makeText(ConfirmOtpRegisterActivity.this,"Scout with this Phone Number already exists!", Toast.LENGTH_SHORT).show();
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


    public void backPress(View view) {
        onBackPressed();
    }
}
