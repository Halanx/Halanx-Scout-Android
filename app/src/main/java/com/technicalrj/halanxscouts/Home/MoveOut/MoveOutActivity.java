package com.technicalrj.halanxscouts.Home.MoveOut;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.technicalrj.halanxscouts.Home.HomeFragment;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.AmenitiesFragment;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.PropertyDetailsFragment;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.RemarksFragment;
import com.technicalrj.halanxscouts.R;


public class MoveOutActivity extends AppCompatActivity implements PropertyDetailsFragment.OnPropertyDetailsInteractionListener,
        AmenitiesFragment.OnAmenitiesInteractionListener{


    private static final String TAG = MoveOutActivity.class.getSimpleName();

    private static final String PROPERTY_DETAILS_FRAGMENT_TAG = "property_details_tag";
    private static final String AMENITIES_FRAGMENT_TAG = "amenities_tag";
    private static final String REMARKS_FRAGMENT_TAG = "remarks_tag";

    private TextView amountTextView;

    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_out);

        taskId = getIntent().getIntExtra("id",0);

        ImageView backButtonImageView = findViewById(R.id.back_button_image_view);
        amountTextView = findViewById(R.id.amount_tv);

        backButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, PropertyDetailsFragment.newInstance(taskId), PROPERTY_DETAILS_FRAGMENT_TAG)
                .addToBackStack(PROPERTY_DETAILS_FRAGMENT_TAG)
                .commit();

    }


    @Override
    public void onCheckAmenitiesClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, AmenitiesFragment.newInstance(), AMENITIES_FRAGMENT_TAG)
                .addToBackStack(AMENITIES_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void setAmount(int amount) {
        amountTextView.setText("₹ "+amount);
    }

    @Override
    public void onRefreshTaskList() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }

    @Override
    public void onLoadRemarksClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, RemarksFragment.newInstance(), REMARKS_FRAGMENT_TAG)
                .addToBackStack(REMARKS_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onAmenitiesBackPressed() {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        PropertyDetailsFragment propertyDetailsFragment = (PropertyDetailsFragment) fm.findFragmentByTag(PROPERTY_DETAILS_FRAGMENT_TAG);

        if(propertyDetailsFragment != null && propertyDetailsFragment.isVisible()){
            finish();
        } else {
            fm.popBackStack();
        }

    }

}


