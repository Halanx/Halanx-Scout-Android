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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.ChangePassword;
import com.technicalrj.halanxscouts.LoginActivity;
import com.technicalrj.halanxscouts.R;

import org.jetbrains.annotations.NotNull;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;
import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;
import static com.technicalrj.halanxscouts.RegisterActivity.JSON;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private static final int PICK_IMAGE = 1;
    private CardView documents;
    private CardView bank;
    private CardView changePass;
    private CardView help;
    private CardView logout;

    private TextView name;
    private TextView phoneNumberTv;
    private TextView emailTv;

    private ImageView imageView;
    private ImageView editImage;
    private ImageView bankStatus;
    private ImageView docStatus;

    private String key;
    String profile_pic_url;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){


            Uri selectedImage = data.getData();
            Picasso.get()
                    .load(selectedImage)
                    .transform(new CircleTransform())
                    .into(imageView);

            File profileFile = new File(getRealPathFromURI(selectedImage));
            uploadData(profileFile);

        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            if(requestCode==PICK_IMAGE)
                chooseImage(PICK_IMAGE);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");


        documents = view.findViewById(R.id.documents);
        bank = view.findViewById(R.id.bankDetails);
        changePass = view.findViewById(R.id.changePass);
        help = view.findViewById(R.id.help);
        logout = view.findViewById(R.id.logout);

        name = view.findViewById(R.id.name);
        phoneNumberTv = view.findViewById(R.id.phone_number);
        emailTv = view.findViewById(R.id.email);

        imageView = view.findViewById(R.id.imageView);
        editImage = view.findViewById(R.id.editImage);
        bankStatus = view.findViewById(R.id.bank_status);
        docStatus = view.findViewById(R.id.doc_status);


        OkHttpClient client = new OkHttpClient();

        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        Request request = new Request.Builder()
                .url(halanxScout+"/scouts/")
                .addHeader("Authorization","Token "+key)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {



                if(response.isSuccessful()){
                    String body = response.body().string();
                    try {

                        final JSONObject jsonObject = new JSONObject(body);

                        String phoneNumber = jsonObject.getString("phone_no");
                        phoneNumberTv.setText(phoneNumber);


                        JSONObject user = jsonObject.getJSONObject("user");

                        final String firstName = user.getString("first_name");
                        final String lastName = user.getString("last_name");
                        final String email = user.getString("email");

                        final boolean document_submission = jsonObject.getBoolean("document_submission_complete");
                        final boolean bank_submission = jsonObject.getBoolean("bank_details_complete");

                        profile_pic_url = jsonObject.getString("profile_pic_url");
                        final String profile_pic_thumbnail_url = jsonObject.getString("profile_pic_thumbnail_url");
                        final String account_holder_name = jsonObject.getJSONObject("bank_detail").getString("account_holder_name");


                        Log.i("InfoText","KEY:"+key);


                        //String account_holder_name  = user.getJSONObject("bank_detail").getString("account_holder_name");

                        if(getActivity()==null)
                            return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get()
                                        .load(profile_pic_thumbnail_url)
                                        .placeholder(R.drawable.male_avatar)
                                        .transform(new CircleTransform())
                                        .into(imageView);


                                name.setText(firstName +" "+lastName);
                                emailTv.setText(email);


                                if(!document_submission){
                                    docStatus.setImageDrawable(getResources().getDrawable(R.drawable.not_submitted));
                                    //docStatus.getLayoutParams().width = (int)(100 / ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                                }else {
                                    docStatus.setImageDrawable(getResources().getDrawable(R.drawable.submitted));
                                    docStatus.getLayoutParams().width = (int)(120 / ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                                }


                                if(!bank_submission){
                                    bankStatus.setImageDrawable(getResources().getDrawable(R.drawable.not_submitted));
                                    //bankStatus.getLayoutParams().width = (int)(90 / ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                                }else {
                                    bankStatus.setImageDrawable(getResources().getDrawable(R.drawable.submitted));
                                    bankStatus.getLayoutParams().height = (int)(120 / ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                                }
                            }
                        });





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), ProfileImageActivity.class);
                intent.putExtra("profile_pic_url",profile_pic_url);
                startActivity(intent);


            }
        });


        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isStoragePermissionGranted(PICK_IMAGE)){
                    chooseImage(PICK_IMAGE);
                }



            }
        });





        documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DocumentsActivity.class);
                startActivity(intent);
            }
        });

        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BankDetailsActivity.class);
                startActivity(intent);
            }
        });


        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), ChangePassword.class);
                intent.putExtra("comingFrom","profile");
                startActivity(intent);



            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("key",prefs.getString("login_key",""))
                        .build();

                final Request request = new Request.Builder()
                        .url("https://scout-api.halanx.com/rest-auth/logout/")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        if(response.isSuccessful()){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"Logged out successfully",Toast.LENGTH_SHORT).show();



                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.remove("login_key");
                                    editor.apply();

                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"Unable to logout",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                });
            }
        });


        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){


                    final String name_entered = name.getText().toString().trim();
                    final String email_entered = emailTv.getText().toString().trim();

                    //Check email
                    if(!email_entered.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email_entered).matches() ){
                        emailTv.setError("Please enter a valid email address");
                        return false;
                    }

                    //Check name
                    if(name_entered.isEmpty()) {
                        name.setError("Name cant be empty");
                    }else if( !name_entered.contains(" ")){
                        name.setError("Please Enter Last name");
                        return false;
                    }

                    //All good

                    try {

                        JSONObject user = new JSONObject();
                        final int idx  = name_entered.indexOf(" ");

                        user.put("first_name",name_entered.substring(0,idx));
                        user.put("last_name",name_entered.substring(idx+1));
                        user.put("email",email_entered);




                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        OkHttpClient client = new OkHttpClient();

                        RequestBody body = RequestBody.create(JSON, "{ \"user\" : "+ user.toString() +" }");

                        Request request = new Request.Builder()
                                .url(halanxScout+"/scouts/")
                                .patch(body)
                                .addHeader("Authorization","Token "+key)
                                .build();

                        Log.i("InfoText","json :"+"{ \"user\" : "+ user.toString() +" }");
                        Log.i("InfoText","key :"+key);



                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                progressDialog.dismiss();


                                JSONObject jsonObject= null;
                                try {
                                    jsonObject = new JSONObject(response.body().string());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String messageString =jsonObject.toString();
                                Log.i("InfoText","Detail :"+messageString);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),"Data Updated",Toast.LENGTH_SHORT).show();
                                    }
                                });

                                name.setText(name_entered.substring(0,idx)+" "+name_entered.substring(idx+1));
                                emailTv.setText(email_entered);

                            }
                        });




                        Log.i("InfoText","name:"+name_entered.substring(0,idx)+"-"+name_entered.substring(idx+1)+"-"+email_entered);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        emailTv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){


                    String name_entered = name.getText().toString().trim();
                    String email_entered = emailTv.getText().toString().trim();

                    //Check email
                    if(!email_entered.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email_entered).matches() ){
                        emailTv.setError("Please enter a valid email address");
                        return false;
                    }

                    //Check name
                    if(name_entered.isEmpty()) {
                        name.setError("Name cant be empty");
                    }else if( !name_entered.contains(" ")){
                        name.setError("Please Enter Last name");
                        return false;
                    }

                    //All good

                    try {

                        JSONObject user = new JSONObject();
                        int idx  = name_entered.indexOf(" ");

                        user.put("first_name",name_entered.substring(0,idx));
                        user.put("last_name",name_entered.substring(idx+1));
                        user.put("email",email_entered);


                        Log.i("InfoText","name:"+name_entered.substring(0,idx)+"-"+name_entered.substring(idx+1)+"-"+email_entered);




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    return true;

                }
                return false;
            }
        });




        return view;
    }


    private void uploadData(File file) {

        OkHttpClient client = new OkHttpClient();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());;
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final SharedPreferences prefs =getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        String key = prefs.getString("login_key", null);

        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/*");


        RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image",file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                .build();




        Request request = new Request.Builder()
                .url(halanxScout+"/scouts/pictures/")
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

                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        Log.i("InfoText",body);
                        profile_pic_url = jsonObject.getString("image");

                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

    }

    private void chooseImage(int permisson) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, permisson);
    }




    public  boolean isStoragePermissionGranted(int permissionCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permissionCode);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

}
