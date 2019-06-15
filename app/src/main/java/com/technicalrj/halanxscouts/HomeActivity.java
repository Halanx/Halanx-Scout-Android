package com.technicalrj.halanxscouts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.technicalrj.halanxscouts.Home.HomeFragment;
import com.technicalrj.halanxscouts.Notification.NotificationFragment;
import com.technicalrj.halanxscouts.Profile.ProfileFragment;
import com.technicalrj.halanxscouts.Wallet.WalletFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        /*Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
*/
        loadFragment(new HomeFragment());

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);


        navigation.setSelectedItemId(R.id.action_home);




    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        Fragment fragment = null;

        if(id== R.id.action_wallet){
            fragment = new WalletFragment();
        }else if(id== R.id.action_stars){
            fragment = new RatingsFragment();
        }else if(id== R.id.action_home){
            fragment = new HomeFragment();
        }else if(id== R.id.action_notifi){
            fragment = new NotificationFragment();
        }else if(id== R.id.action_user){
            fragment = new ProfileFragment();
        }

        return loadFragment(fragment);

    }
}
