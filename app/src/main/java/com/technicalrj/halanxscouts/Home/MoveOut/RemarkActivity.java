package com.technicalrj.halanxscouts.Home.MoveOut;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.technicalrj.halanxscouts.HomeActivity;
import com.technicalrj.halanxscouts.R;

public class RemarkActivity extends AppCompatActivity {

    private EditText remarks;
    private Button done_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);

        remarks = findViewById(R.id.remarks);
        done_button   = findViewById(R.id.done_button);


        remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().equals("")){
                    enableButton(false);
                }else {
                    enableButton(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    public void submitRemark(View view) {

        String rem = remarks.getText().toString();

        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();

    }

    public void cancelTask(View view) {
    }
}
