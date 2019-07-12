package com.technicalrj.halanxscouts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.technicalrj.halanxscouts.Home.HomeFragment;
import com.technicalrj.halanxscouts.Notification.NotificationFragment;
import com.technicalrj.halanxscouts.Pojo.MyLocation;
import com.technicalrj.halanxscouts.Profile.ProfileFragment;
import com.technicalrj.halanxscouts.Service.LocationService;
import com.technicalrj.halanxscouts.Wallet.WalletFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static com.technicalrj.halanxscouts.Profile.ProfileFragment.BANK_DETAILS_UPDATE;
import static com.technicalrj.halanxscouts.Profile.ProfileFragment.DOCUMENTS_DETAILS_UPDATE;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView navigation;
    private static final int REQUEST_CHECK_SETTINGS = 51;
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 52;
    public static final String TAG  ="HomeActivity";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates;

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

    public void checkPermissionAndGetLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            switch (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){

                case PackageManager.PERMISSION_DENIED:
                    Log.i(TAG, "onCreate: requesting permission");

                    if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this,R.style.AlertDialogCustom)
                                .setMessage("Need permission to access your current location!")
                                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                LOCATION_PERMISSIONS_REQUEST_CODE);
                                    }
                                })
                                .setCancelable(false);
                        builder.create().show();
                    } else {
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                LOCATION_PERMISSIONS_REQUEST_CODE);
                    }
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    Log.i(TAG, "onCreate: permission granted");
                    getMyLocation();
                    startService(new Intent(this, LocationService.class));
                    break;

            }
        } else {
            getMyLocation();
            startService(new Intent(this, LocationService.class));
        }

    }



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        Log.i(TAG, "onRequestPermissionsResult: " + requestCode + "--" + permissions.toString() + "--" + grantResults.toString());

        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLocation();
                startService(new Intent(this, LocationService.class));
            } else {

                finishAffinity();
                Log.i(TAG, "onRequestPermissionsResult: permission denied");
            }
        }
    }




    @SuppressLint("MissingPermission")
    private void getMyLocation(){
        initializeLocationRequest();
        Log.i("InfoText", "getMyLocation: 1");

        if(!requestingLocationUpdates) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d(TAG, "getMyLocation: onSuccess: "+location.getLatitude());
                                saveLocationInSharedPref(location);

                            }
                        }
                    });
        }

        Log.i("InfoText", "getMyLocation: 2");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "getMyLocation: onSuccess:");
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getMyLocation: onFailure: "+e.getMessage());
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });
    }



    private void initializeLocationRequest(){

        if(fusedLocationClient == null){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }

        if(locationRequest == null) {
            locationRequest = new LocationRequest()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

        if(locationCallback == null) {
            locationCallback = new LocationCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.d(TAG, "initializeLocationRequest: onLocationResult: location callback: "+locationResult.getLastLocation().getLatitude());
                    saveLocationInSharedPref(locationResult.getLastLocation());
                    stopLocationUpdates();

                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                    Log.d(TAG, "onLocationAvailability: " + locationAvailability.isLocationAvailable());
                }
            };
        }
    }






    private void saveLocationInSharedPref(Location location){

        //Here we get only lat and longitude


        Log.i("InfoText", "saveLocationInSharedPref: ");

        //Save live location in key
        MyLocation myLocation = new MyLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        String locationString = new Gson().toJson(myLocation);
        getSharedPreferences(Constants.LOCATION_SHARED_PREF, MODE_PRIVATE).edit().
                putString(Constants.LOCATION_KEY, locationString).apply();




    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates:");
        requestingLocationUpdates = true;
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates:");
        requestingLocationUpdates = false;
        fusedLocationClient.removeLocationUpdates(locationCallback);
        locationRequest = null;
        locationCallback = null;
    }


}
