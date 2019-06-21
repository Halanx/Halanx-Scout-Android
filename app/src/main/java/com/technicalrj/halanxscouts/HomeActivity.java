package com.technicalrj.halanxscouts;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.technicalrj.halanxscouts.Home.HomeFragment;
import com.technicalrj.halanxscouts.Notification.NotificationFragment;
import com.technicalrj.halanxscouts.Profile.ProfileFragment;
import com.technicalrj.halanxscouts.Wallet.WalletFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static com.technicalrj.halanxscouts.Profile.ProfileFragment.BANK_DETAILS_UPDATE;
import static com.technicalrj.halanxscouts.Profile.ProfileFragment.DOCUMENTS_DETAILS_UPDATE;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView navigation;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("InfoText","onAcitivyt: in HomeAcity");



/*        if (requestCode == DOCUMENTS_DETAILS_UPDATE || requestCode== BANK_DETAILS_UPDATE) {
            Log.i("InfoText","resultCode:");
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                Log.i("InfoText","resyke:"+result);
                if(result.equals("update")){
                    Log.i("InfoText","result update");
                    //navigation.setSelectedItemId(R.id.action_user);


                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new ProfileFragment())
                            .commit();
                }
            }
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        //onNavigationItemSelected(navigation.getMenu().getItem(R.menu.action_home));
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
