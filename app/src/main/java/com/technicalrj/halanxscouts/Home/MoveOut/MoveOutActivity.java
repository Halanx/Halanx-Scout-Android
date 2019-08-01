package com.technicalrj.halanxscouts.Home.MoveOut;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.technicalrj.halanxscouts.R;

import java.util.ArrayList;

public class MoveOutActivity extends AppCompatActivity {

    private Button done_button;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_out);


        done_button   = findViewById(R.id.done_button);
        checkBox = findViewById(R.id.checkBox);

        String[] descriptionData = {"Details", "Status", "Photo", "Confirm"};
        StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);


        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    enableButton(true);
                }else {
                    enableButton(false);
                }
            }
        });

    }

    public void checkAmenities(View view) {
        startActivity(new Intent(this,AmenitiesActivity.class));
    }

    public void cancelTask(View view) {
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
