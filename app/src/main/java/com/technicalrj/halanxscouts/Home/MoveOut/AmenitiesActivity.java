package com.technicalrj.halanxscouts.Home.MoveOut;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.technicalrj.halanxscouts.Adapters.AmenetiesAdapter;
import com.technicalrj.halanxscouts.R;

public class AmenitiesActivity extends AppCompatActivity {


    private RecyclerView amenitiesRecycler;
    private AmenetiesAdapter amenetiesAdapter;
    private Button done_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities);

        done_button   = findViewById(R.id.done_button);
        amenitiesRecycler = findViewById(R.id.amenities_recyclerview);
        amenetiesAdapter = new AmenetiesAdapter(this);
        amenitiesRecycler.setAdapter(amenetiesAdapter);
        amenitiesRecycler.setNestedScrollingEnabled(false);
        amenitiesRecycler.setLayoutManager(new LinearLayoutManager(this));



    }

    public void goToRemark(View view) {
        startActivity(new Intent(this,RemarkActivity.class));
    }



    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void enableButton(boolean val){

        if(val){
            done_button.setEnabled(true);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape));
        }else {
            done_button.setEnabled(false);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape_dark_grey));

        }

    }
}
