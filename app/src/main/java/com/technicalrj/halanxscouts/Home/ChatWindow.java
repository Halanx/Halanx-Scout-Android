package com.technicalrj.halanxscouts.Home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Adapters.ChatAdapter;
import com.technicalrj.halanxscouts.Home.Chat.Messages;
import com.technicalrj.halanxscouts.Home.Chat.Result;
import com.technicalrj.halanxscouts.Profile.ProfileImageActivity;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatWindow extends AppCompatActivity {

    String id,firstName,lastName , profilePicUrl,phoneNumber;
    ChatAdapter adapter;
    ArrayList<Result> results;
    String key;
    EditText chat_text;
    LinearLayoutManager lm;
    int page=1;
    private Socket mSocket;
    private TextView nameTv;
    ImageView customerImg;
    ProgressBar progressBar;
    Button sendButton;
    public final static String nodeBaseUrl = "https://share.halanx.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);


        id = getIntent().getStringExtra("id");
        firstName = getIntent().getStringExtra("first_name");
        lastName = getIntent().getStringExtra("last_name");
        profilePicUrl = getIntent().getStringExtra("profile_pic_url");
        phoneNumber = getIntent().getStringExtra("phone_number");
        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);







        nameTv = findViewById(R.id.name_tv);
        progressBar = findViewById(R.id.progress_bar);
        sendButton = findViewById(R.id.send_button);
        chat_text = findViewById(R.id.chat_text);
        if(chat_text.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        final RecyclerView rv_chat = findViewById(R.id.rv_chat);
        customerImg = findViewById(R.id.customer_img);
        results = new ArrayList<>();
        adapter = new ChatAdapter(getApplicationContext(),results);
        lm = new LinearLayoutManager(getApplicationContext());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(false);
        rv_chat.setAdapter(adapter);
        rv_chat.setLayoutManager(lm);

        nameTv.setText( firstName.substring(0,1).toUpperCase() + firstName.substring(1)  +" "+ lastName.substring(0,1).toUpperCase() + lastName.substring(1) );


        Picasso.get()
                .load( profilePicUrl)
                .into(customerImg);



        updateChat(page);


        try {
            mSocket = IO.socket(nodeBaseUrl);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);


            mSocket.connect();
            mSocket.on("onMessage", onMessage);
            mSocket.on("onChat", onChat);
            //mSocket.on("chat_event", chat_event);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Profile> call = availabilityInterface.getProfile("Token "+key);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                JSONObject json = new JSONObject();
                try {
                    json.put("id", response.body().getId());
                    json.put("chat_type","chat_between_scout_and_customer");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("setCache", json);

            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

            }
        });





    }



    @Override
    public void onBackPressed() {
        mSocket.disconnect();
        super.onBackPressed();
    }

    private void updateChat(final int currentPage) {



        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Messages> call = availabilityInterface.getMessages(id,currentPage,"Token "+key,"scout");
        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(Call<Messages> call, Response<Messages> response) {

                Messages messages= response.body();

                if(messages==null || messages.getCount()==0)
                    return;

                ArrayList<Result> newResults=(ArrayList<Result>) messages.getResults();
                results.addAll(newResults);
                adapter.notifyDataSetChanged();

                if(messages.getNext()==null){
                    return;
                }


                updateChat(currentPage+1);
                page++;


            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

    public void sendText(View view) {


        final String text = chat_text.getText().toString().trim();
        Log.i("ChatWindow", "sendText: "+text);
        if(text.equals(""))
            return;

        sendButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        HashMap<String,String> map  = new HashMap<>();
        map.put("content",text);
        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Result> call = availabilityInterface.createMessage(map,id,"Token "+key,"scout","application/json");
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(response.isSuccessful()){

                    Log.i("InfoText","id:"+response.body().getId());

                    results.add(0,response.body());
                    sendButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();
                    chat_text.setText("");
                }else {
                    try {
                        Log.i("InfoText","sending messege error:"+response.errorBody().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChatWindow.this,"Some Error Occured",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    finish();
                    startActivity(getIntent());
                    JSONObject json = null;
                    try {
                        json = new JSONObject(String.valueOf(args[0]));
                        Log.i("InfoText","done");
                        Result result = new Result();

                        result.setId(json.getInt("id"));
                        result.setCreatedAt(json.getString("created_at"));
                        result.setIsRead(json.getBoolean("is_read"));
                        result.setContent(json.getString("content"));

                        results.add(0,result);
                        Log.i("InfoText", String.valueOf(result));



                        adapter.notifyDataSetChanged();

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    {
                    }
                }
            });
        }
    };
    private Emitter.Listener onChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject json = null;
                    try {
                        json = new JSONObject(String.valueOf(args[0]));

                        Gson gson = new Gson();
                        Result result = gson.fromJson(json.getJSONObject("message_data").toString(),Result.class);
                        Log.i("InfoText","done");

                        /*result.setId(json.getInt("id"));
                        result.setCreatedAt(json.getString("created_at"));
                        result.setIsRead(json.getBoolean("is_read"));
                        result.setContent(json.getString("content"));
                        result.setRole(json.get());
*/

                        results.add(0,result);
                        Log.i("InfoText", String.valueOf(result));



                        adapter.notifyDataSetChanged();




                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    {
                        Log.e("error", String.valueOf(String.valueOf(args[0])));
                    }
                }
            });
        }
    };

    private Emitter.Listener chat_event = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject json = null;
                    try {

                        json = new JSONObject(String.valueOf(args[0]));
                        Log.i("InfoText","done");
                        Result result = new Result();

                        result.setId(json.getInt("id"));
                        result.setCreatedAt(json.getString("created_at"));
                        result.setIsRead(json.getBoolean("is_read"));
                        result.setContent(json.getString("content"));

                        results.add(0,result);
                        Log.i("InfoText", String.valueOf(result));



                        adapter.notifyDataSetChanged();



                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    {
                        Log.e("error", String.valueOf(args[0]));
                    }
                }
            });
        }
    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("InfoText", "connected1");


                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("InfoText", "diconnected");
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("InfoText", "Error connecting");

                }
            });
            Log.i("InfoText", "call: "+args[0].toString());
        }
    };


    public void backPress(View view) {
        onBackPressed();
    }

    public void profileImage(View view) {

        Intent intent = new Intent(this, ProfileImageActivity.class);
        intent.putExtra("profile_pic_url",profilePicUrl);
        startActivity(intent);


    }

    public void callCustomr(View view) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_VIEW );
            callIntent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Toast.makeText(this,"Calling a Phone Number Call failed",Toast.LENGTH_SHORT).show();
        }
    }
}
