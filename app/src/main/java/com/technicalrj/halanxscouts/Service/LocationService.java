package com.technicalrj.halanxscouts.Service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.Constants;
import com.technicalrj.halanxscouts.Home.ScheduleAvailability;
import com.technicalrj.halanxscouts.Pojo.MyLocation;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 100f;

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider) {
//            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            Log.d("InfoText", "LocationListener: last location: " + mLastLocation.getLatitude());
        }

        @Override
        public void onLocationChanged(final Location location) {
//            Log.e(TAG, "onLocationChanged: " + location);
//            Toast.makeText(getApplicationContext(), String.valueOf(location), Toast.LENGTH_SHORT).show();
            mLastLocation.set(location);

            String locationString = new Gson().toJson(new MyLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
            Log.d(TAG, "onLocationChanged: " + locationString);
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOCATION_SHARED_PREF, MODE_PRIVATE);
            sharedPreferences.edit().putString(Constants.LOCATION_KEY, locationString).apply();


            JSONObject json = new JSONObject();
            try {
                json.put("clatitude", location.getLatitude());
                json.put("clongitude", location.getLongitude());
                json.put("dlatitude", location.getLatitude());
                json.put("dlongitude", location.getLongitude());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
            String key = prefs.getString("login_key", null);

            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("latitude",location.getLatitude());
            jsonObject1.addProperty("longitude",location.getLongitude());

            RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
            Call<Profile> call = availabilityInterface.updateLocation(jsonObject,key);
            call.enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    Log.i(TAG, "onResponse: "+response.toString());
                }

                @Override
                public void onFailure(Call<Profile> call, Throwable t) {

                    t.printStackTrace();
                }
            });




        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.getLastLocation().
//                    addOnSuccessListener(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//                                String locationString = new Gson().toJson(new MyLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLatitude())));
//                                Log.d(TAG, "onLocationChanged: " + locationString);
//                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOCATION_SHARED_PREF, MODE_PRIVATE);
//                                sharedPreferences.edit().putString(Constants.LOCATION_KEY, locationString).apply();
//                            } else {
//                                Log.d(TAG, "onLocationChanged: loction " + );
//                            }
//                        }
//                    });
//        }

        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}