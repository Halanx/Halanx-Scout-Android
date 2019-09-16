package com.technicalrj.halanxscouts.Home.Onboarding.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.Adapters.HousePhotosAdapter;
import com.technicalrj.halanxscouts.Home.TaskFolder.House;
import com.technicalrj.halanxscouts.Pojo.HouseImage;
import com.technicalrj.halanxscouts.Profile.ProfileImageActivity;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;
import com.technicalrj.halanxscouts.utlis.FileSaver;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
public class UploadPhotosFragment extends Fragment implements HousePhotosAdapter.OnPhotoClick {

    private static final int REQUEST_TAKE_PHOTO = 123;
    private OnUploadPhotoInteractionListener listener;
    private RecyclerView photosRecyclerView;
    private HousePhotosAdapter housePhotosAdapter;
    private FloatingActionButton addPhoto;
    private ArrayList<String> imageUrls;
    private ArrayList<HouseImage> houseImageArrayList;
    private static final int PICK_IMAGE = 12;
    private RetrofitAPIClient.DataInterface dataInterface;
    private String key;
    private int taskId;
    private String TAG = UploadPhotosFragment.class.getSimpleName();
    private ProgressDialog progressDialog;

    private Queue<Integer> imageUploadQueue;

    private FileSaver fileSaver;
    private File photoFile;

    private Handler imageUploadHandler;
    private Runnable imageUploadRunnable;
    private boolean isUploading = false;
    private boolean isUploadingHandlerAttached = false;
    private Button doneButton;

    private float rotateAngle = 0;

    private static final int CAMERA_PERMISSION_REQ_CODE = 11;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_photos, container, false);
        taskId = getArguments().getInt("taskId");


        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);
        dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);

        doneButton = view.findViewById(R.id.done_button);
        addPhoto = view.findViewById(R.id.add_photo);
        photosRecyclerView = view.findViewById(R.id.recyclerView);
        imageUrls = new ArrayList<>();
        houseImageArrayList = new ArrayList<>();
        housePhotosAdapter = new HousePhotosAdapter(getActivity(),this,houseImageArrayList);
        photosRecyclerView.setAdapter(housePhotosAdapter);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.setHasFixedSize(true);
        photosRecyclerView.setNestedScrollingEnabled(false);

        imageUploadQueue = new LinkedList<>();
        imageUploadHandler = new Handler(Looper.myLooper());
        imageUploadRunnable = new Runnable() {
            @Override
            public void run() {
                uploadImage();
            }
        };


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCameraPermissionAndGetPhoto();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoUploaded();
            }
        });

        return view;
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_TAKE_PHOTO){
            if (resultCode == RESULT_OK) {

                final int position = houseImageArrayList.size();

                try {
                    ExifInterface ei = new ExifInterface(photoFile.getAbsolutePath());

                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            Log.d(TAG, "onActivityResult: 90");
                            rotateAngle = 90;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            Log.d(TAG, "onActivityResult: 180");
                            rotateAngle = 180;
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            Log.d(TAG, "onActivityResult: 270");
                            rotateAngle = 270;
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotateAngle = 0;

                    }

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);

                    if(rotateAngle != 0) {

                        new AsyncTask<File, Void, File>() {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                progressDialog.show();
                            }

                            @Override
                            protected File doInBackground(File... files) {
                                return rotateImage(files[0], rotateAngle);
                            }

                            @Override
                            protected void onPostExecute(File file) {
                                super.onPostExecute(file);

                                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                                        BitmapFactory.decodeFile(file.getAbsolutePath()),
                                        128,
                                        128);

                                progressDialog.dismiss();


                                houseImageArrayList.add(position, new HouseImage(String.valueOf(Uri.fromFile(file)),
                                        HouseImage.UPLOADING, thumbImage));
                                housePhotosAdapter.notifyItemInserted(position);

                                imageUploadQueue.add(position);

                                if (!isUploadingHandlerAttached) {
                                    attachUploadHandler();
                                }
                            }
                        }.execute(photoFile);
                    } else {
                        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                                BitmapFactory.decodeFile(photoFile.getAbsolutePath()),
                                128,
                                128);

                        houseImageArrayList.add(position, new HouseImage(String.valueOf(Uri.fromFile(photoFile)),
                                HouseImage.UPLOADING, thumbImage));
                        housePhotosAdapter.notifyItemInserted(position);

                        imageUploadQueue.add(position);

                        if (!isUploadingHandlerAttached) {
                            attachUploadHandler();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error taking photo! ", Toast.LENGTH_SHORT).show();
                }


            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private File rotateImage(File imageFile, float angle){
        Log.d(TAG, "rotateImage: "+angle);
        // Rotate the Bitmap thanks to a rotated matrix. This seems to work.
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.fromFile(imageFile));
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //learn content provider for more info
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Log.d(TAG, "rotateImage: file is rotated");
        } catch (IOException e) {
            Log.d(TAG, "rotateImage: file is null");
            e.printStackTrace();
        }
        return imageFile;
    }

    private void uploadImage(){
        Log.d(TAG, "uploadImage: ");
        if(!imageUploadQueue.isEmpty() && !isUploading){
            Log.d(TAG, "uploadImage: really uploading");
            isUploading = true;
            final int position = imageUploadQueue.remove();
            final HouseImage houseImage = houseImageArrayList.get(position);

            File file = new File(Uri.parse(houseImage.getUrl()).getPath());
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            dataInterface.addHouseImage("Token "+key,taskId,body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    isUploading = false;

                    if(response.isSuccessful()){
                        String url = response.body().get("image").getAsString();
                        houseImage.setStatus(HouseImage.UPLOADED);
                        imageUploadSuccessful(position);

                        doneButton.setEnabled(true);

                        attachUploadHandler();
                    }else {
                        Log.e(TAG, "onResponse: message: "+response.errorBody().toString() );
                        Log.e(TAG, "onResponse: code: "+response.code());
                        imageUploadFailed(position);
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    isUploading = false;

                    t.printStackTrace();
                    Log.e(TAG, "onFailure: "+t.getMessage());
                    imageUploadFailed(position);

                }
            });

        } else {
            removeUploadHandler();
        }
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



    @Override
    public void onPhotoClick(View view) {
        int pos = photosRecyclerView.getChildAdapterPosition(view);

        HouseImage houseImage = houseImageArrayList.get(pos);

        if(houseImage.getStatus() == HouseImage.ERROR){
            imageUploadQueue.add(pos);
            houseImage.setStatus(HouseImage.UPLOADING);
            houseImageArrayList.set(pos, houseImage);
            housePhotosAdapter.notifyDataSetChanged();
            attachUploadHandler();
        } else if(houseImage.getStatus() == HouseImage.UPLOADED){
            startActivity(new Intent(getActivity(), ProfileImageActivity.class)
                    .putExtra("profile_pic_url",houseImage.getUrl()));
        } else if(houseImage.getStatus() == HouseImage.UPLOADING){
            Toast.makeText(getActivity(), "Image is being uploaded!", Toast.LENGTH_SHORT).show();
        }

    }

    private void removeUploadHandler(){
        isUploadingHandlerAttached = false;
        imageUploadHandler.removeCallbacks(imageUploadRunnable);
    }

    private void attachUploadHandler(){
        isUploadingHandlerAttached = true;
        imageUploadHandler.post(imageUploadRunnable);
    }


    @Override
    public void onResume() {
        super.onResume();
        attachUploadHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeUploadHandler();
    }

    private void imageUploadSuccessful(int position){
        HouseImage houseImage = houseImageArrayList.get(position);
        houseImage.setStatus(HouseImage.UPLOADED);
        houseImageArrayList.set(position, houseImage);
        housePhotosAdapter.notifyDataSetChanged();
    }

    private void imageUploadFailed(int position){
        HouseImage houseImage = houseImageArrayList.get(position);
        houseImage.setStatus(HouseImage.ERROR);
        houseImageArrayList.set(position, houseImage);
        housePhotosAdapter.notifyDataSetChanged();
    }


    public interface OnUploadPhotoInteractionListener{
        void onPhotoUploaded();
    }


    private void checkCameraPermissionAndGetPhoto(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (getActivity().checkSelfPermission(Manifest.permission.CAMERA)) {
                case PackageManager.PERMISSION_DENIED :
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setMessage("Need camera permission to click photos!")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQ_CODE);
                                    }
                                })
                                .create();
                        alertDialog.show();
                    }
                    break;
                case PackageManager.PERMISSION_GRANTED :
                    startCameraIntent();
            }
        } else {
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        if(fileSaver == null){
            fileSaver = new FileSaver(getActivity(), false, true);
        }

        if(fileSaver.getGalleryFolder() != null && fileSaver.getGalleryFolder().exists()){
            try {
                photoFile = fileSaver.createImageFile(fileSaver.getGalleryFolder());
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            getResources().getString(R.string.file_provider_authority),
                            photoFile);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } else {
                    Log.d(TAG, "onClick: photoFile is null");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "onClick: documnet is null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_REQ_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startCameraIntent();
            } else {
                Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
