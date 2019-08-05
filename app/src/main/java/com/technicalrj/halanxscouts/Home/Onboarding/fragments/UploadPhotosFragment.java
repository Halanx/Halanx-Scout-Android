package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.Adapters.HousePhotosAdapter;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadPhotosFragment extends Fragment {

    private OnUploadPhotoInteractionListener listener;
    private RecyclerView photosRecyclerView;
    private HousePhotosAdapter housePhotosAdapter;
    private FloatingActionButton addPhoto;
    private ArrayList<String> imageUrls;
    private static final int PICK_IMAGE = 12;
    private RetrofitAPIClient.DataInterface dataInterface;
    private String key;
    private int taskId;
    private String TAG=getClass().getName();
    private ProgressDialog progressDialog;


    public UploadPhotosFragment() {
        // Required empty public constructor
    }

    public static UploadPhotosFragment newInstance(int taskId) {

        Bundle args = new Bundle();
        args.putInt("taskId",taskId);
        UploadPhotosFragment fragment = new UploadPhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri selectedImage = result.getUri();
                uploadImage(selectedImage);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }





    }

    private void uploadImage(Uri selectedImage) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        File file = new File(selectedImage.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        dataInterface.addHouseImage("Token "+key,taskId,body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()){
                    String url = response.body().get("image").getAsString();
                    imageUrls.add(url);
                    housePhotosAdapter.notifyItemChanged(imageUrls.size()-1);
                    progressDialog.dismiss();
                }else {
                    Log.e(TAG, "onResponse: "+response.errorBody().toString() );
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                t.printStackTrace();
                Log.e(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(getActivity(),"Error Uploading Image",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });







    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_photos, container, false);
        taskId = getArguments().getInt("taskId");


        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);
        dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);

        Button doneButton = view.findViewById(R.id.done_button);
        addPhoto = view.findViewById(R.id.add_photo);
        photosRecyclerView = view.findViewById(R.id.recyclerView);
        imageUrls = new ArrayList<>();
        housePhotosAdapter = new HousePhotosAdapter(getActivity(),listener,imageUrls);
        photosRecyclerView.setAdapter(housePhotosAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        photosRecyclerView.setLayoutManager(linearLayoutManager);
        photosRecyclerView.setNestedScrollingEnabled(false);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted(PICK_IMAGE)){
                    chooseImage(PICK_IMAGE);
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUrls.size()>0){
                    listener.onPhotoUploaded();
                }else {
                    Toast.makeText(getActivity(),"Please Upload Atleast One Image",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public  boolean isStoragePermissionGranted(int permissionCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) {
                return true;
            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        permissionCode);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            if(requestCode==PICK_IMAGE)
                chooseImage(PICK_IMAGE);

        }
    }


    private void chooseImage(int permisson) {

        CropImage.activity()
                .start(getContext(), this);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnUploadPhotoInteractionListener){
            listener = (OnUploadPhotoInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnUploadPhotoInteractionListener{
        void onPhotoUploaded();
    }

}
