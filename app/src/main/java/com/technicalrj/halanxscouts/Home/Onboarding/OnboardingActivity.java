package com.technicalrj.halanxscouts.Home.Onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.technicalrj.halanxscouts.Home.MoveOut.MoveOutActivity;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.PropertyDetailsFragment;
import com.technicalrj.halanxscouts.Home.Onboarding.fragments.AddressFragment;
import com.technicalrj.halanxscouts.R;

public class OnboardingActivity extends AppCompatActivity {

    private static final String TAG = OnboardingActivity.class.getSimpleName();
    public static final String ADDRESS_FRAGMENT_TAG = "address";
    private StateProgressBar stateProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ImageView backButtonImageView = findViewById(R.id.back_button_image_view);

        String[] descriptionData = {"Address", "Photos", "Amenities","Property Details"};
        stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateNumberTextSize(0);
        stateProgressBar.setStateDescriptionSize(14f);
        stateProgressBar.setStateDescriptionTypeface("font/montserrat_regular.otf");


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, AddressFragment.newInstance(), ADDRESS_FRAGMENT_TAG)
                .addToBackStack(ADDRESS_FRAGMENT_TAG)
                .commit();

        backButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
