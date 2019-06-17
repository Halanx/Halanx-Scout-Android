package com.technicalrj.halanxscouts.Home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.technicalrj.halanxscouts.R;

public class ScoutAcceptanceActivity extends AppCompatActivity {

    TextView tv_task,tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_acceptance);

        tv_task = findViewById(R.id.tv_task);
        tv_time = findViewById(R.id.time);

        tv_task.setText("Move In");
        tv_time.setText("12 Nov, 12:00PM");





    }
}
