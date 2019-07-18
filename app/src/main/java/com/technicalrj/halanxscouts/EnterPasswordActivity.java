package com.technicalrj.halanxscouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;
import static com.technicalrj.halanxscouts.RegisterActivity.JSON;

public class EnterPasswordActivity extends AppCompatActivity {

    private EditText passwordTv;
    private EditText confirmPasswordTv;

    private String key;
    private String otp;
    TextView showHide, confirmShowHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);




        key = getIntent().getStringExtra("key");
        otp = getIntent().getStringExtra("otp");


        passwordTv = findViewById(R.id.password);
        confirmPasswordTv = findViewById(R.id.confirm_password);
        showHide = findViewById(R.id.show_hide);
        confirmShowHide = findViewById(R.id.confirm_show_hide);

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




                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("otp",Integer.valueOf(otp));
                    jsonObject.put("old_password",JSONObject.NULL);
                    jsonObject.put("new_password",password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                final Request request = new Request.Builder()
                        .url(halanxScout+"/rest-auth/password/change/")
                        .patch(body)
                        .addHeader("Authorization","Token "+key)
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        Log.i("InfoText","Details :"+response.body().string());
                        if(response.isSuccessful()){


                            progressDialog.dismiss();

                            EnterPasswordActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EnterPasswordActivity.this,"Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });



                            Log.i("InfoText","password succ.");
                            Intent intent = new Intent(EnterPasswordActivity.this, LoginActivity.class);
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



    public void showHideOnClick(View view) {

        if(showHide.getText().equals("HIDE"))
        {
            showHide.setText("SHOW");
            passwordTv.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        else if(showHide.getText().equals("SHOW"))
        {
            showHide.setText("HIDE");
            passwordTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }

    }

    public void confirmShowHideOnClick(View view) {

        if(confirmShowHide.getText().equals("HIDE"))
        {
            confirmShowHide.setText("SHOW");
            confirmPasswordTv.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        else if(confirmShowHide.getText().equals("SHOW"))
        {
            confirmShowHide.setText("HIDE");
            confirmPasswordTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }

    }
}
