package com.technicalrj.halanxscouts.Home.Onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.technicalrj.halanxscouts.Constants;
import com.technicalrj.halanxscouts.Home.MoveOut.MoveOutActivity;
import com.technicalrj.halanxscouts.Home.MoveOut.fragment.PropertyDetailsFragment;
import com.technicalrj.halanxscouts.Home.Onboarding.fragments.AccommodationTypeFragment;
import com.technicalrj.halanxscouts.Home.Onboarding.fragments.AddressFragment;
import com.technicalrj.halanxscouts.Home.Onboarding.fragments.AmenitiesOnBoardingFragment;
import com.technicalrj.halanxscouts.Home.Onboarding.fragments.UploadPhotosFragment;
import com.technicalrj.halanxscouts.R;

public class OnboardingActivity extends AppCompatActivity implements PropertyDetailsFragment.OnPropertyDetailsInteractionListener,
 AddressFragment.OnAddressFragmentInteractionListener, UploadPhotosFragment.OnUploadPhotoInteractionListener,
AmenitiesOnBoardingFragment.OnAmenitiesOnBoardingInteractionListener{

    private static final String TAG = OnboardingActivity.class.getSimpleName();
    public static final String ADDRESS_FRAGMENT_TAG = "address";
    public static final String PHOTOS_FRAGMENT_TAG = "photos";
    public static final String AMENITIES_FRAGMENT_TAG = "amenities";
    public static final String ACCOMMODATION_FRAGMENT_TAG = "accommodation";
    private StateProgressBar stateProgressBar;

    int taskId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        taskId = getIntent().getIntExtra(Constants.TASK_ID, -1);

        ImageView backButtonImageView = findViewById(R.id.back_button_image_view);

        String[] descriptionData = {"Address", "Photos", "Amenities","Property Details"};
        stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateNumberTextSize(0);
        stateProgressBar.setStateDescriptionSize(14f);
        stateProgressBar.setStateDescriptionTypeface("font/montserrat_regular.otf");


//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.frame_layout, AddressFragment.newInstance(taskId), ADDRESS_FRAGMENT_TAG)
//                .addToBackStack(ADDRESS_FRAGMENT_TAG)
//                .commit();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, AmenitiesOnBoardingFragment.newInstance(taskId), AMENITIES_FRAGMENT_TAG)
                .addToBackStack(AMENITIES_FRAGMENT_TAG)
                .commit();

        backButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });



    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        AddressFragment addressFragment = (AddressFragment) fm.findFragmentByTag(ADDRESS_FRAGMENT_TAG);

        if (addressFragment != null && addressFragment.isVisible()) {
            finish();
        } else {

            // For the progress bar
            int x = stateProgressBar.getCurrentStateNumber();
            if (x == 4) {
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
            }
            else if (x == 3) {
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
            } else if (x == 2) {
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
            }

            fm.popBackStack();
        }

    }

    @Override
    public void onCheckAmenitiesClicked() {

    }

    @Override
    public void setAmount(int amount) {

    }

    @Override
    public void onRefreshTaskList() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onAddressUpdated() {
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, UploadPhotosFragment.newInstance(), PHOTOS_FRAGMENT_TAG)
                .addToBackStack(PHOTOS_FRAGMENT_TAG)
                .commit();

    }

    @Override
    public void onPhotoUploaded() {
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, AmenitiesOnBoardingFragment.newInstance(taskId), AMENITIES_FRAGMENT_TAG)
                .addToBackStack(AMENITIES_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onAmenitiesUploaded() {
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, AccommodationTypeFragment.newInstance(), ACCOMMODATION_FRAGMENT_TAG)
                .addToBackStack(ACCOMMODATION_FRAGMENT_TAG)
                .commit();
    }
}
