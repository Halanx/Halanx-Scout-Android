package com.technicalrj.halanxscouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

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

public class ForgotPassword extends AppCompatActivity {

    private EditText phoneNumber;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        phoneNumber = findViewById(R.id.phone_number);

    }

    public void sentOtp(View view) {

        final String phoneN = phoneNumber.getText().toString().trim();
        if( phoneN.isEmpty() ){
            phoneNumber.setError("Phone Number can't be empty");
        }else if( !isPhoneValid(phoneN)){
            phoneNumber.setError("Phone Number is not valid");
        }else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, "{}");

            Request request = new Request.Builder()
                    .url(halanxScout+"/scouts/get_otp/" + phoneN + "/")
                    .post(body)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    progressDialog.dismiss();

                    Intent intent = new Intent(ForgotPassword.this, ConfirmOtpPasswordActivity.class);
                    intent.putExtra("phoneNumber",phoneN);
                    startActivity(intent);
                }
            });



        }



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }




    public boolean isPhoneValid(String phoneN){

        if( phoneN.length()<10 || phoneN.length()>15 )
            return false;



        if(!Patterns.PHONE.matcher(phoneN).matches()){
            return false;
        }

        if(phoneN.contains(" ")|| phoneN.contains(".") )
            return false;

        return true;
    }
}
