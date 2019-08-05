package com.technicalrj.halanxscouts.Service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.technicalrj.halanxscouts.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = FetchAddressIntentService.class.getSimpleName();
    private ResultReceiver receiver;

    public FetchAddressIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Check if receiver was properly registered.
        if (receiver == null) {
            Log.d(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        LatLng location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        if (location == null) {
//            errorMessage = getString(R.string.no_location_data_provided);
            Log.d(TAG, "no_location_data_provided");
            deliverResultToReceiver(Constants.GEO_FAILURE_RESULT, "no_location_data_provided");
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";


        // ...

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            ioException.printStackTrace();
            Log.e(TAG, "error: service_not_available", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            illegalArgumentException.printStackTrace();
            Log.e(TAG, "error: invalid_lat_long_used" + ". " +
                    "Latitude = " + location.latitude +
                    ", Longitude = " +
                    location.longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Address not found";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.GEO_FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
//            Log.d(TAG, "onHandleIntent: locality: "+address.getLocality());
//            Log.d(TAG, "onHandleIntent:admin area "+address.getAdminArea());
//            Log.d(TAG, "onHandleIntent: feature name: "+address.getFeatureName());
//
////            Could be null
//            Log.d(TAG, "onHandleIntent: sub admin areaa: "+address.getSubAdminArea());
//
////            Could be null
//            Log.d(TAG, "onHandleIntent: sub locality: "+address.getSubLocality());
//
//
//            Log.d(TAG, "onHandleIntent: premises: "+address.getPremises());
//            Log.d(TAG, "onHandleIntent: throughfare: "+address.getThoroughfare());
//            Log.d(TAG, "onHandleIntent: sub throughfare: "+address.getSubThoroughfare());

            String addr;
            if(address.getSubLocality() == null){
                addr = address.getLocality();
                if(address.getAdminArea() != null){
                    addr = addr+","+address.getAdminArea();
                }
            } else {
                addr = address.getSubLocality() + "," + address.getLocality();
            }
            Log.d(TAG, "onHandleIntent: combined address"+addr);
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                Log.d(TAG, "onHandleIntent: i: "+i);
                addressFragments.add(address.getAddressLine(i));
                Log.d(TAG, "onHandleIntent: "+address.getAddressLine(i));
            }
            Log.i(TAG, "Address found");
            deliverResultToReceiver(Constants.GEO_SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.GEO_RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }
}
