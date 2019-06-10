package com.technicalrj.halanxscouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;

public class EnterPasswordActivity extends AppCompatActivity {

    private EditText passwordTv;
    private EditText confirmPasswordTv;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        key = getIntent().getStringExtra("key");

        passwordTv = findViewById(R.id.password);
        confirmPasswordTv = findViewById(R.id.confirm_password);

    }

    public void login(View view) {


        String password  = passwordTv.getText().toString().trim();
        String confirmPassword  = confirmPasswordTv.getText().toString().trim();

        if(password.isEmpty()){
            passwordTv.setError("Password can't be empty");
        }else if(confirmPassword.isEmpty()){
            confirmPasswordTv.setError("Confirm Password can't be empty");
        }else {

            // Not validate the informatono entered

            if(!password.equals(confirmPassword)){
                confirmPasswordTv.setError("Password Not matching");
            } else {

                //Everyting is correct sending otp

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                OkHttpClient client = new OkHttpClient();





                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("new_password1",password)
                        .addFormDataPart("new_password2",password)
                        .build();



                final Request request = new Request.Builder()
                        .url(halanxScout+"/rest-auth/password/change/")
                        .post(body)
                        .addHeader("Authorization","Token "+key)
                        .build();

                Log.i("InfoText","password key:"+key);

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        if(response.isSuccessful()){


                            progressDialog.dismiss();

                            EnterPasswordActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EnterPasswordActivity.this,"Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Log.i("InfoText","password succ.");
                            Intent intent = new Intent(EnterPasswordActivity.this,LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();



                        }else {
                            EnterPasswordActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();

                                    Log.i("InfoText","password not succ.");
                                    Toast.makeText(EnterPasswordActivity.this,"Unable to change password", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                });




            }

        }


    }
}
