package com.technicalrj.halanxscouts.Home.Onboarding;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.Service.FetchAddressIntentService;
import com.technicalrj.halanxscouts.utils.Constants;


import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 51;
    private static final int REQUEST_CHECK_SETTINGS = 52;
    private GoogleMap mMap;

    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates;
    private FusedLocationProviderClient fusedLocationClient;

    private FloatingActionButton myLocationFab;
    private Marker userMarker;
    private AddressResultReceiver resultReceiver;

    private LatLng selectedLatLng;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        myLocationFab = findViewById(R.id.my_location_fab);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMyLocation();
            }
        });

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
//        autocompleteFragment.setCountry("IN");

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng().latitude);
                moveToLocation(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestingLocationUpdates = false;

        locationRequest = new LocationRequest()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback(){
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLastLocation();
                if(mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.d(TAG, "onLocationAvailability: "+locationAvailability.isLocationAvailable());
            }
        };


        checkPermissionAndGetLocation();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setOnMapClickListener(this);
    }

    public void checkPermissionAndGetLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            switch (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){

                case PackageManager.PERMISSION_DENIED:
                    Log.i(TAG, "onCreate: requesting permission");

                    if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setMessage("Need permission to access your current location!")
                                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                LOCATION_PERMISSIONS_REQUEST_CODE);
                                    }
                                })
                                .setCancelable(false);
                        builder.create().show();
                    } else {
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSIONS_REQUEST_CODE);
                    }
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    Log.i(TAG, "onCreate: permission granted");
                    getCurrentLocation();
                    break;

            }
        } else {
            getCurrentLocation();
        }

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if(!requestingLocationUpdates) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
//                        mapFragment.
                            if (location != null && mMap != null) {
                                // Logic to handle location object
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(location.getLatitude(), location.getLongitude()), 15));
                            }
                        }
                    });
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "onSuccess:");
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });
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
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!requestingLocationUpdates){
            checkPermissionAndGetLocation();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        stopLocationUpdates();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSIONS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: permission denied");
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    public void moveToMyLocation() {
        if(mMap != null && currentLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
        } else {
            Toast.makeText(this, "Fetching location!", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveToLocation(LatLng latLng){

        selectedLatLng = latLng;

        if (userMarker != null)
            userMarker.remove();
        userMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        if(resultReceiver == null) {
            resultReceiver = new AddressResultReceiver(new Handler());
        }

        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Fetching address...");
        }
        progressDialog.show();
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, latLng);
        startService(intent);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        moveToLocation(latLng);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(progressDialog != null) {
                progressDialog.dismiss();
            }

            if (resultData == null) {
                if(userMarker != null){
                    userMarker.remove();
                }
                Toast.makeText(MapsActivity.this, "Couldn't get current location, Try Again!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultCode == Constants.GEO_SUCCESS_RESULT) {
                final String addressOutput = resultData.getString(Constants.GEO_RESULT_DATA_KEY);
                Log.d(TAG, "onReceiveResult: "+addressOutput);

                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                        .setMessage(addressOutput)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: lat: "+selectedLatLng.latitude);
                                Log.d(TAG, "onClick: lng: "+selectedLatLng.longitude);
                                Log.d(TAG, "onClick: address: "+addressOutput);

                                Intent intent = new Intent();
                                intent.putExtra(com.technicalrj.halanxscouts.Constants.LOCATION_DATA, addressOutput);
                                intent.putExtra(com.technicalrj.halanxscouts.Constants.LOCATION_LAT, selectedLatLng.latitude);
                                intent.putExtra(com.technicalrj.halanxscouts.Constants.LOCATION_LNG, selectedLatLng.longitude);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                userMarker.remove();
                            }
                        }).create();
                alertDialog.show();

            } else {
                if(userMarker != null){
                    userMarker.remove();
                }
                Toast.makeText(MapsActivity.this, "Couldn't get current location, Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
