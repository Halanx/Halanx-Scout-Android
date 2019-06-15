package com.technicalrj.halanxscouts.Wallet;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.technicalrj.halanxscouts.Adapters.PaidAdapter;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;
import com.technicalrj.halanxscouts.Wallet.TaskPayment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DueActivity extends AppCompatActivity {


    String key;
    ArrayList<TaskPayment> taskPayments ;
    PaidAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due);


        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        RecyclerView rv_notiification = findViewById(R.id.rv_notifications);
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());

        taskPayments  = new ArrayList<>();
        getAllDuePayments();

        adapter = new PaidAdapter(getApplicationContext(),taskPayments);
        rv_notiification.setAdapter(adapter);
        rv_notiification.setLayoutManager(lm);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Due Payments");


    }


    private void getAllDuePayments() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<List<TaskPayment>> call1 = availabilityInterface.getPayments("pending","Token "+key);

        call1.enqueue(new Callback<List<TaskPayment>>() {
            @Override
            public void onResponse(Call<List<TaskPayment>> call, Response<List<TaskPayment>> response) {
                taskPayments.clear();
                taskPayments  = (ArrayList<TaskPayment>) response.body();
                adapter.notifyDataSetChanged();



                Log.i("InfoText","taskPayments.size():"+taskPayments.size());

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<TaskPayment>> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

}
