package com.technicalrj.halanxscouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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

public class LoginActivity extends AppCompatActivity {

    public static String halanxScout = "https://scout-api.halanx.com";
    private EditText phoneNumber;
    private EditText password;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        phoneNumber = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);


        SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        String restoredText = prefs.getString("login_key", null);
        if (restoredText != null) {

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }



    }

    public void registerHere(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    public void login(View view) {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");


        String phoneN = phoneNumber.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if( phoneN.isEmpty() ){
            phoneNumber.setError("Phone Number can't be empty");
        }else if(pass.isEmpty()){
            password.setError("Password can't be empty");
        }else {

            Log.i("InfoText",Patterns.PHONE.matcher(phoneN).matches()+"");

            if(!isPhoneValid(phoneN) ){
                phoneNumber.setError("Phone Number is not valid");
            }else{

                progressDialog.show();

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username","s"+phoneN)
                        .addFormDataPart("password",pass)
                        .build();

                final Request request = new Request.Builder()
                        .url(halanxScout+"/rest-auth/login/")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        if(response.isSuccessful()){
                            String body = response.body().string();
                            try {

                                progressDialog.dismiss();

                                JSONObject jsonObject = new JSONObject(body);
                                String key = jsonObject.get("key").toString();


                                SharedPreferences.Editor editor = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE).edit();
                                editor.putString("login_key", key);
                                editor.apply();



                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }





                        }else {
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this,"Unable to log in with provided credentials.", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                });



            }


        }

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
