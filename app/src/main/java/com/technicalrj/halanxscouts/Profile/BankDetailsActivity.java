package com.technicalrj.halanxscouts.Profile;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.technicalrj.halanxscouts.Notification.NoficationPojo.Notification;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.BankDetail;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompatActivity {

    TextView account_number,confirm_account_number,account_holder_name,ifsc_code , bank_name ,bank_branch;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);


        account_number = findViewById(R.id.account_number);
        confirm_account_number = findViewById(R.id.confirm_account_number);
        account_holder_name = findViewById(R.id.account_holder_name);
        ifsc_code = findViewById(R.id.ifsc_code);
        bank_name = findViewById(R.id.bank_name);
        bank_branch = findViewById(R.id.bank_branch);

        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Bank Details");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RetrofitAPIClient.DataInterface retrofitAPIClient = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Profile> call = retrofitAPIClient.getProfile("Token "+key);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                progressDialog.dismiss();

                BankDetail bankDetail = response.body().getBankDetail();
                if(bankDetail.getAccountHolderName()!=null)
                    account_holder_name.setText(bankDetail.getAccountHolderName());

                if(bankDetail.getAccountNumber()!=null)
                    account_number.setText(bankDetail.getAccountNumber()+"");

                if(bankDetail.getBankBranch()!=null)
                    bank_branch.setText(bankDetail.getBankBranch());

                if(bankDetail.getBankName()!=null)
                    bank_name.setText(bankDetail.getBankName());

                if(bankDetail.getIfscCode()!=null)
                    ifsc_code.setText(bankDetail.getIfscCode());



            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                progressDialog.dismiss();
            }
        });






    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    public void saveDetails(View view) {

        if(account_number.getText().toString().isEmpty()){
            account_number.setError("This field cant be empty");
            return;
        }

        if(confirm_account_number.getText().toString().isEmpty())
            {
                confirm_account_number.setError("This field cant be empty");
                return;
            }

        if(account_holder_name.getText().toString().isEmpty())
        {
            account_holder_name.setError("This field cant be empty");
            return;
        }

        if(ifsc_code.getText().toString().isEmpty())
        {
            ifsc_code.setError("This field cant be empty");
            return;
        }

        if(bank_name.getText().toString().isEmpty())
        {
            bank_name.setError("This field cant be empty");
            return;
        }

        if(bank_branch.getText().toString().isEmpty())
        {
            bank_branch.setError("This field cant be empty");
            return;
        }


        if(!account_number.getText().toString().equals( confirm_account_number.getText().toString()))
        {
            confirm_account_number.setError("This is not same as Account Number");
            return;
        }


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();




        final Profile profile = new Profile();
        BankDetail bankDetail = new BankDetail();
        bankDetail.setAccountHolderName(account_holder_name.getText().toString());
        bankDetail.setAccountNumber(account_number.getText().toString());
        bankDetail.setBankBranch(bank_branch.getText().toString());
        bankDetail.setBankName(bank_name.getText().toString());
        bankDetail.setIfscCode(ifsc_code.getText().toString());





        profile.setBankDetail(bankDetail);

        Log.i("InfoText","profile"+profile.getBankDetail());

        RetrofitAPIClient.DataInterface retrofitAPIClient = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Profile> call = retrofitAPIClient.updateBankDetails(profile,"Token "+key);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.i("InfoText",response.message().toString());
                Log.i("InfoText",response.body().getBankDetail().toString());
                progressDialog.dismiss();

                BankDetailsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BankDetailsActivity.this,"Bank Details Updated",Toast.LENGTH_SHORT).show();
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
