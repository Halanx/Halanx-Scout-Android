package com.technicalrj.halanxscouts.Home.MoveOut;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.technicalrj.halanxscouts.Home.HomeFragment;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.AmenitiesFragment;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.PropertyDetailsFragment;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.RemarksFragment;
import com.technicalrj.halanxscouts.R;


public class MoveOutActivity extends AppCompatActivity implements PropertyDetailsFragment.OnPropertyDetailsInteractionListener,
        AmenitiesFragment.OnAmenitiesInteractionListener {


    private static final String TAG = MoveOutActivity.class.getSimpleName();

    private static final String PROPERTY_DETAILS_FRAGMENT_TAG = "property_details_tag";
    private static final String AMENITIES_FRAGMENT_TAG = "amenities_tag";
    private static final String REMARKS_FRAGMENT_TAG = "remarks_tag";

    private Button done_button;
    private CheckBox checkBox;
    private StateProgressBar stateProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_out);

        ImageView backButtonImageView = findViewById(R.id.back_button_image_view);


        String[] descriptionData = {"Property Details", "Amenities Checkup", "Remark"};
        stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateNumberTextSize(0);
        stateProgressBar.setStateDescriptionSize(14f);
        stateProgressBar.setStateDescriptionTypeface("font/montserrat_regular.otf");

        backButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, PropertyDetailsFragment.newInstance(), PROPERTY_DETAILS_FRAGMENT_TAG)
                .addToBackStack(PROPERTY_DETAILS_FRAGMENT_TAG)
                .commit();




    }


    @Override
    public void onCheckAmenitiesClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, AmenitiesFragment.newInstance(), AMENITIES_FRAGMENT_TAG)
                .addToBackStack(AMENITIES_FRAGMENT_TAG)
                .commit();

        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);


    }

    @Override
    public void onLoadRemarksClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, RemarksFragment.newInstance(), REMARKS_FRAGMENT_TAG)
                .addToBackStack(REMARKS_FRAGMENT_TAG)
                .commit();

        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNgiumber.THREE);

    }

    @Override
    public void onAmenitiesBackPressed() {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        PropertyDetailsFragment propertyDetailsFragment = (PropertyDetailsFragment) fm.findFragmentByTag(PROPERTY_DETAILS_FRAGMENT_TAG);

        if (propertyDetailsFragment != null && propertyDetailsFragment.isVisible()) {
            finish();
        } else {

            // For the progress bar
            int x = stateProgressBar.getCurrentStateNumber();
            if (x == 3) {
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
            } else if (x == 2) {
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
            }

            fm.popBackStack();
        }

    }

    public void enableButton(boolean val) {

        if (val) {
            done_button.setEnabled(true);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape));
        } else {
            done_button.setEnabled(false);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape_dark_grey));

        }

    }
}


