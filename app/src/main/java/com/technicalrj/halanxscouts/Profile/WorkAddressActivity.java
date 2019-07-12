package com.technicalrj.halanxscouts.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.technicalrj.halanxscouts.Profile.ProfilePojo.BankDetail;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.WorkAddress;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkAddressActivity extends AppCompatActivity {

    EditText streetAddress, city,state,pincode , country ;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_address);


        streetAddress = findViewById(R.id.street_address);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        pincode = findViewById(R.id.pincode);
        country = findViewById(R.id.country);


        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Work Address");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        RetrofitAPIClient.DataInterface retrofitAPIClient = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Profile> call = retrofitAPIClient.getProfile("Token "+key);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                progressDialog.dismiss();

                WorkAddress workAddress = response.body().getWorkAddress();
                if(workAddress!=null){
                    if(workAddress.getStreetAddress()!=null)
                        streetAddress.setText(workAddress.getStreetAddress());

                    if(workAddress.getCity()!=null)
                        city.setText(workAddress.getCity());

                    if(workAddress.getState()!=null)
                        state.setText(workAddress.getState());

                    if(workAddress.getPincode()!=null)
                        pincode.setText(workAddress.getPincode()+"");

                    if(workAddress.getCountry()!=null)
                        country.setText(workAddress.getCountry());


                }



            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
            }
        });

    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    public void saveDetails(View view) {

        if(streetAddress.getText().toString().isEmpty()){
            streetAddress.setError("This field cant be empty");
            return;
        }

        if(city.getText().toString().isEmpty())
        {
            city.setError("This field cant be empty");
            return;
        }

        if(state.getText().toString().isEmpty())
        {
            state.setError("This field cant be empty");
            return;
        }

        if(pincode.getText().toString().isEmpty())
        {
            pincode.setError("This field cant be empty");
            return;
        }

        if(country.getText().toString().isEmpty())
        {
            country.setError("This field cant be empty");
            return;
        }





        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();




        final Profile profile = new Profile();
        WorkAddress workAddress = new WorkAddress();
        workAddress.setStreetAddress(streetAddress.getText().toString());
        workAddress.setCity(city.getText().toString());
        workAddress.setState(state.getText().toString());
        workAddress.setPincode(Integer.valueOf(pincode.getText().toString().trim()));
        workAddress.setCountry(country.getText().toString());





        profile.setWorkAddress(workAddress);
        Log.i("InfoText","work"+profile.getWorkAddress());

        RetrofitAPIClient.DataInterface retrofitAPIClient = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Profile> call = retrofitAPIClient.updateBankDetails(profile,"Token "+key);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.i("InfoText",response.message().toString());
                Log.i("InfoText",response.body().getWorkAddress().toString());
                progressDialog.dismiss();

                WorkAddressActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WorkAddressActivity.this,"Work Details Updated",Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result","update");
                        setResult(RESULT_OK,returnIntent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
                Log.i("InfoText",t.getMessage());
                progressDialog.dismiss();
            }
        });

    }
}
