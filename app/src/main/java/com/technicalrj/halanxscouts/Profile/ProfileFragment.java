package com.technicalrj.halanxscouts.Profile;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.technicalrj.halanxscouts.ConfirmOtpPasswordActivity;
import com.technicalrj.halanxscouts.ForgotPassword;
import com.technicalrj.halanxscouts.HomeActivity;
import com.technicalrj.halanxscouts.LoginActivity;
import com.technicalrj.halanxscouts.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private CardView documents;
    private CardView bank;
    private CardView changePass;
    private CardView help;
    private CardView logout;

    private TextView name;
    private TextView phoneNumberTv;
    private TextView emailTv;

    public ProfileFragment() {
        // Required empty public constructor
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



        OkHttpClient client = new OkHttpClient();

        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        String key = prefs.getString("login_key", null);

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

                        JSONObject jsonObject = new JSONObject(body);

                        String phoneNumber = jsonObject.getString("phone_no");
                        phoneNumberTv.setText(phoneNumber);


                        JSONObject user = jsonObject.getJSONObject("user");

                        String firstName = user.getString("first_name");
                        String lastName = user.getString("last_name");
                        String email = user.getString("email");


                        name.setText(firstName +" "+lastName);
                        emailTv.setText(email);




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


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


        return view;
    }



}
