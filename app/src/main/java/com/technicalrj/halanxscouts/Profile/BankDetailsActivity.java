package com.technicalrj.halanxscouts.Profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.technicalrj.halanxscouts.R;

public class BankDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Bank Details");



    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    public void saveDetails(View view) {




    }
}
