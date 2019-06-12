package com.technicalrj.halanxscouts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.technicalrj.halanxscouts.Adapters.NotificationAdapter;
import com.technicalrj.halanxscouts.Adapters.PaidAdapter;

public class PayHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_history);

        RecyclerView rv_notiification = findViewById(R.id.rv_notifications);
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        PaidAdapter adapter = new PaidAdapter(getApplicationContext());
        rv_notiification.setAdapter(adapter);
        rv_notiification.setLayoutManager(lm);
    }
}
