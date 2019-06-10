package com.technicalrj.halanxscouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText emailTv;
    private EditText phoneNumber;
    private CheckBox termsAndConditions ;
    private EditText passwordTv;
    private EditText confirmPasswordTv;
    String first;
    String last;
    String phoneN;
    String email;
    String password;
    String confirmPassword;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        emailTv = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);
        termsAndConditions = findViewById(R.id.checkBox);
        passwordTv = findViewById(R.id.password);
        confirmPasswordTv = findViewById(R.id.confirm_password);



    }


    public void sendOtp(View view) {


        first = firstName.getText().toString().trim();
        last = lastName.getText().toString().trim();
        email = emailTv.getText().toString().trim();
        phoneN = phoneNumber.getText().toString().trim();
        password = passwordTv.getText().toString().trim();
        confirmPassword = confirmPasswordTv.getText().toString().trim();


        if(first.isEmpty()){
            firstName.setError("First name can't be empty");
        }else if(last.isEmpty()){
            lastName.setError("Last name can't be empty");
        }else if(phoneN.isEmpty()){
            phoneNumber.setError("Phone Number can't be empty");
        }else if(password.isEmpty()){
            passwordTv.setError("Password can't be empty");
        }else if(confirmPassword.isEmpty()){
            phoneNumber.setError("Confirm Password can't be empty");
        }else {

            // Not validate the informatono entered

            if(!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailTv.setError("Please enter a valid email address");
            }else if(!isPhoneValid(phoneN) ){
                phoneNumber.setError("Please enter a valid Phone Number");
            }else if(!termsAndConditions.isChecked()){
                Toast.makeText(RegisterActivity.this,"You must agree with the terms and conditions", Toast.LENGTH_SHORT).show();
            }else if(!password.equals(confirmPassword)){
                confirmPasswordTv.setError("Password Not matching");
            } else {

                //Everyting is correct sending otp

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

                        Intent intent = new Intent(RegisterActivity.this,ConfirmOtpRegisterActivity.class);
                        intent.putExtra("firstName",first);
                        intent.putExtra("lastName",last);
                        intent.putExtra("email",email);
                        intent.putExtra("phoneNumber",phoneN);
                        intent.putExtra("password",password);
                        startActivity(intent);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    public void termsAndCondi(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://halanx.com/terms"));
        startActivity(intent);

    }
}
