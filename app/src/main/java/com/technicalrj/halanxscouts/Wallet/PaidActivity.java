package com.technicalrj.halanxscouts.Wallet;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.technicalrj.halanxscouts.Adapters.PaidAdapter;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaidActivity extends AppCompatActivity {

    String key;
    ArrayList<TaskPayment> taskPayments ;
    PaidAdapter adapter;
    ImageView noPaymentImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_history);

        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        noPaymentImg = findViewById(R.id.no_payment);






        getAllPayments();



    }

    private void getAllPayments() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<List<TaskPayment>> call1 = availabilityInterface.getPayments("paid","Token "+key);

        call1.enqueue(new Callback<List<TaskPayment>>() {
            @Override
            public void onResponse(Call<List<TaskPayment>> call, Response<List<TaskPayment>> response) {

                taskPayments  = (ArrayList<TaskPayment>) response.body();

                if(taskPayments.size()==0){
                    noPaymentImg.setVisibility(View.VISIBLE);
                }else {
                    noPaymentImg.setVisibility(View.GONE);
                    RecyclerView rv_payments = findViewById(R.id.rv_notifications);
                    adapter = new PaidAdapter(PaidActivity.this,taskPayments);
                    rv_payments.setAdapter(adapter);
                    rv_payments.setLayoutManager(new LinearLayoutManager(PaidActivity.this));



                }
                Log.i("InfoText","taskPayments.size():"+taskPayments.size());
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<TaskPayment>> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }




    public void backPress(View view) {
        onBackPressed();
    }
}
