package com.technicalrj.halanxscouts.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;

public class DocumentsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_AADHAR_1 = 1;
    private static final int PICK_IMAGE_AADHAR_2 = 2;
    private static final int PICK_IMAGE_PAN = 3;
    private static final String TAG = "InfoText";
    private ImageView aadhar1, aadhar2;
    private ImageView pan;
    private ImageView imgView = null;
    private String key ;
    private int aadhar1Id, aadhar2Id,panId;

    private ProgressDialog progressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == PICK_IMAGE_AADHAR_1 || requestCode == PICK_IMAGE_AADHAR_2 || requestCode == PICK_IMAGE_PAN) && resultCode == RESULT_OK) {


            Log.i("InfoText", "Image : " + data.toString());

            Uri selectedImage = data.getData();


            if (requestCode == PICK_IMAGE_AADHAR_1) {


                if(aadhar1.getContentDescription().equals("format"))
                    deleteAadhar1(aadhar1);

                Picasso.get()
                        .load(selectedImage)
                        .into(aadhar1);

                File aadhar1File = new File(getRealPathFromURI(selectedImage));
                uploadData(aadhar1File, "Aadhar");




            }
            if (requestCode == PICK_IMAGE_AADHAR_2) {

                if(aadhar2.getContentDescription().equals("format"))
                    deleteAadhar1(aadhar2);

                Picasso.get()
                        .load(selectedImage)
                        .into(aadhar2);

                File aadhar2File = new File(getRealPathFromURI(selectedImage));
                uploadData(aadhar2File, "Aadhar");
            }
            if (requestCode == PICK_IMAGE_PAN) {

                if(aadhar2.getContentDescription().equals("format"))
                    deleteAadhar2(pan);

                Picasso.get()
                        .load(selectedImage)
                        .into(pan);

                File panFile = new File(getRealPathFromURI(selectedImage));
                uploadData(panFile, "PAN");
            }


        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            if (requestCode == PICK_IMAGE_AADHAR_1)
                chooseImage(PICK_IMAGE_AADHAR_1);

            if (requestCode == PICK_IMAGE_AADHAR_2)
                chooseImage(PICK_IMAGE_AADHAR_2);

            if (requestCode == PICK_IMAGE_PAN)
                chooseImage(PICK_IMAGE_PAN);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);


        aadhar1 = findViewById(R.id.aadhar1);
        aadhar2 = findViewById(R.id.aadhar2);
        pan = findViewById(R.id.pan1);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        final SharedPreferences prefs =getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        Log.i("InfoText","key:"+key);

        OkHttpClient client = new OkHttpClient();




        Request request = new Request.Builder()
                .url(halanxScout + "/scouts/documents/")
                .addHeader("Authorization", "Token " + key)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String body = response.body().string();
                //Log.i(TAG,"documents :"+body);

                try {
                    JSONObject jsonObject = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (response.isSuccessful()) {

                    try {
                        JSONArray jsonArray = new JSONArray(body);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject document = jsonArray.getJSONObject(i);


                            final String url = document.getString("image");
                            String type = document.getString("type");
                            final int id = document.getInt("id");
                            Log.i("InfoText","id:"+id);

                            Log.i("InfoText", "cont des:" + aadhar1.getContentDescription());

                            if (type.equals("Aadhar")) {
                                DocumentsActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (aadhar1.getContentDescription().equals("actual")) {
                                            aadhar2.setContentDescription("actual");
                                            Picasso.get()
                                                    .load(url)
                                                    .into(aadhar2);

                                            aadhar2Id = id;

                                        } else {
                                            aadhar1.setContentDescription("actual");
                                            Picasso.get()
                                                    .load(url)
                                                    .into(aadhar1);

                                            aadhar1Id = id;
                                        }


                                    }
                                });
                            } else if (type.equals("PAN") && pan.getContentDescription().equals("format")) {
                                DocumentsActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pan.setContentDescription("actual");
                                        Picasso.get()
                                                .load(url)
                                                .into(pan);

                                        panId = id;

                                    }
                                });
                            }


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }

            }
        });


    }


    public void uploadAadharFront(View view) {
        if (isStoragePermissionGranted(PICK_IMAGE_AADHAR_1)) {
            chooseImage(PICK_IMAGE_AADHAR_1);
        }
    }

    public void uploadAadharBack(View view) {
        if (isStoragePermissionGranted(PICK_IMAGE_AADHAR_2)) {
            chooseImage(PICK_IMAGE_AADHAR_2);
        }
    }

    public void uploadPan(View view) {
        if (isStoragePermissionGranted(PICK_IMAGE_PAN)) {
            chooseImage(PICK_IMAGE_PAN);
        }
    }

    private void chooseImage(int permisson) {


        Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamera});

        startActivityForResult(chooserIntent, permisson);




    }

/*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go


            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }
*/



    public  boolean isStoragePermissionGranted(int permissionCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(DocumentsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permissionCode);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }





    private void uploadData(File file , String type ) {

        OkHttpClient client = new OkHttpClient();
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/*");


        RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image",file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                .addFormDataPart("type", type)
                .build();




        Request request = new Request.Builder()
                .url(halanxScout+"/scouts/documents/")
                .post(req)
                .addHeader("Authorization","Token "+key)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String body = response.body().string();
                Log.i(TAG,body);

                if(response.isSuccessful()){
                    DocumentsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DocumentsActivity.this,"Document Uploaded",Toast.LENGTH_SHORT).show();
                        }
                    });
                    progressDialog.dismiss();
                }

            }
        });


    }


    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    public void deleteAadhar1(View view) {


        if(aadhar1.getContentDescription().equals("format"))
            return;



        OkHttpClient client = new OkHttpClient();
        progressDialog.setMessage("Loading...");
        progressDialog.show();




        Request request = new Request.Builder()
                .url(halanxScout+"/scouts/documents/"+aadhar1Id+"/")
                .delete()
                .addHeader("Authorization","Token "+key)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String body = response.body().string();
                Log.i(TAG,body+"id="+aadhar1Id);
                if(response.isSuccessful()){
                    DocumentsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Picasso.get()
                                    .load(R.drawable.upload_image)
                                    .into(aadhar1);
                            aadhar1.setContentDescription("format");
                            DocumentsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DocumentsActivity.this,"Document Deleted",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }


                progressDialog.dismiss();

            }
        });


    }

    public void deletePan(View view) {

        if(pan.getContentDescription().equals("format"))
            return;



        OkHttpClient client = new OkHttpClient();
        progressDialog.setMessage("Loading...");
        progressDialog.show();




        Request request = new Request.Builder()
                .url(halanxScout+"/scouts/documents/"+panId+"/")
                .delete()
                .addHeader("Authorization","Token "+key)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String body = response.body().string();
                Log.i(TAG,body);
                if(response.isSuccessful()){
                    DocumentsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(R.drawable.upload_image)
                                    .into(pan);
                            pan.setContentDescription("format");

                            DocumentsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DocumentsActivity.this,"Document Deleted",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                progressDialog.dismiss();


            }
        });

    }

    public void deleteAadhar2(View view) {

        if(aadhar2.getContentDescription().equals("format"))
            return;



        OkHttpClient client = new OkHttpClient();
        progressDialog.setMessage("Loading...");
        progressDialog.show();






        Request request = new Request.Builder()
                .url(halanxScout+"/scouts/documents/"+aadhar2Id+"/")
                .delete()
                .addHeader("Authorization","Token "+key)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String body = response.body().string();
                Log.i(TAG,body);
                if(response.isSuccessful()){
                    DocumentsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(R.drawable.upload_image)
                                    .into(aadhar2);
                            aadhar2.setContentDescription("format");

                            DocumentsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DocumentsActivity.this,"Document Deleted",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }


                progressDialog.dismiss();

            }
        });

    }
}
