package com.technicalrj.halanxscouts.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
    String aadhar1Url,aadhar2Url,panUrl;

    private ProgressDialog progressDialog;




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == PICK_IMAGE_AADHAR_1 || requestCode == PICK_IMAGE_AADHAR_2 || requestCode == PICK_IMAGE_PAN) && resultCode == RESULT_OK) {




            Uri selectedImage = data.getData();

            Log.i("InfoText", "Sledted image uri : " + selectedImage);


            if (requestCode == PICK_IMAGE_AADHAR_1) {




                File aadhar1File=null;
                if(selectedImage==null){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    aadhar1.setImageBitmap(photo);
                    aadhar1File = persistImage( photo ,"sdf");
                }else {
                    aadhar1.setImageBitmap(getRotatedImage(selectedImage));
                    aadhar1File = persistImage( getRotatedImage(selectedImage) ,"dsf");
                }

                aadhar1.setContentDescription("actual");
                uploadData(aadhar1File, "Aadhar","aadhar1");




            }
            if (requestCode == PICK_IMAGE_AADHAR_2) {


                File aadhar2File=null;
                if(selectedImage==null){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    aadhar2.setImageBitmap(photo);
                    aadhar2File = persistImage( photo ,"sdf");
                }else {
                    aadhar2.setImageBitmap(getRotatedImage(selectedImage));
                    aadhar2File = persistImage( getRotatedImage(selectedImage) ,"dsf");
                }
                aadhar2.setContentDescription("actual");
                uploadData(aadhar2File, "Aadhar","aadhar2");


            }
            if (requestCode == PICK_IMAGE_PAN) {

                File panFile=null;
                if(selectedImage==null){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    pan.setImageBitmap(photo);
                    panFile = persistImage( photo ,"sdf");
                }else {
                    pan.setImageBitmap(getRotatedImage(selectedImage));
                    panFile = persistImage( getRotatedImage(selectedImage) ,"dsf");
                }
                pan.setContentDescription("actual");
                uploadData(panFile, "PAN","pan");


            }


        }


    }






    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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

                                            aadhar2Url = url;
                                            aadhar2Id = id;

                                        } else {
                                            aadhar1.setContentDescription("actual");
                                            Picasso.get()
                                                    .load(url)
                                                    .into(aadhar1);

                                            aadhar1Url = url;
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

                                        panUrl = url;
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
        if(aadhar1.getContentDescription().equals("actual")){
            Intent intent  = new Intent(this,DocumentImage.class);
            intent.putExtra("image_url",aadhar1Url);
            startActivity(intent);
            return;
        }

        if (isStoragePermissionGranted(PICK_IMAGE_AADHAR_1)) {
            chooseImage(PICK_IMAGE_AADHAR_1);
        }
    }

    public void uploadAadharBack(View view) {

        if(aadhar2.getContentDescription().equals("actual")){
            Intent intent  = new Intent(this,DocumentImage.class);
            intent.putExtra("image_url",aadhar2Url);
            startActivity(intent);
            return;
        }

        if (isStoragePermissionGranted(PICK_IMAGE_AADHAR_2)) {
            chooseImage(PICK_IMAGE_AADHAR_2);
        }
    }

    public void uploadPan(View view) {
        if(pan.getContentDescription().equals("actual")){
            Intent intent  = new Intent(this,DocumentImage.class);
            intent.putExtra("image_url",panUrl);
            startActivity(intent);
            return;
        }


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




    public  boolean isStoragePermissionGranted(int permissionCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) {
                return true;
            } else {

                ActivityCompat.requestPermissions(DocumentsActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                                permissionCode);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }





    private void uploadData(File file , String type , final String document) {

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

                try {
                    JSONObject jsonObject = new JSONObject(body);
                    String url = jsonObject.getString("image");

                    if(document.equals("aadhar1")){
                        aadhar1Url = url;
                    }else if(document.equals("aadhar2")){
                        aadhar2Url = url;
                    }else if(document.equals("pan")){
                        panUrl = url;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(response.isSuccessful()){
                    DocumentsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {



                            Toast.makeText(DocumentsActivity.this,"Document Uploaded",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {

                }
                progressDialog.dismiss();
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

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","update");
        setResult(RESULT_OK,returnIntent);
        finish();
        super.onBackPressed();
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


    public Bitmap getRotatedImage(Uri selectedImage){


        Bitmap rotatedBitmap=null;
        try {
            ExifInterface ei = null;
            ei = new ExifInterface(getRealPathFromURI(selectedImage));
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap bitmap = null;
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

            rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {
            Log.i("InfoText","exception :"+e.getMessage());
            e.printStackTrace();
        }




        return rotatedBitmap;
    }

    private  File persistImage(Bitmap bitmap, String name) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error writing bitmap", e);
        }

        return imageFile;
    }

    public File getFileFromBitmap(Bitmap bitmap){
        File f = new File(this.getCacheDir(),"sdf.jpg" );
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
