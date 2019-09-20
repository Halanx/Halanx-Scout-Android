package com.technicalrj.halanxscouts.Home;

import android.app.ProgressDialog;
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
import com.technicalrj.halanxscouts.Constants;
import com.technicalrj.halanxscouts.Home.Chat.Messages;
import com.technicalrj.halanxscouts.Home.Chat.Result;
import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.Profile.ProfileImageActivity;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;
import com.technicalrj.halanxscouts.utlis.EndlessRecyclerOnScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatWindow extends AppCompatActivity {

    String taskId,firstName,lastName , profilePicUrl,phoneNumber,conversationId;
    ChatAdapter adapter;
    ArrayList<Result> results;
    String key;
    EditText chat_text;
    LinearLayoutManager lm;
    private Socket mSocket;
    private TextView nameTv;
    ImageView customerImg;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    Button sendButton;
    public final static String nodeBaseUrl = "https://share.halanx.com/";
    RecyclerView rv_chat;

    private RetrofitAPIClient.DataInterface dataInterface;
    private String TAG = ChatWindow.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);


        taskId = getIntent().getIntExtra("conversation",0)+"";


        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);



        dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);



        nameTv = findViewById(R.id.name_tv);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressBar = findViewById(R.id.progress_bar);
        sendButton = findViewById(R.id.send_button);
        chat_text = findViewById(R.id.chat_text);
        if(chat_text.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        rv_chat = findViewById(R.id.rv_chat);
        customerImg = findViewById(R.id.customer_img);
        results = new ArrayList<>();
        adapter = new ChatAdapter(getApplicationContext(),results);
        lm = new LinearLayoutManager(getApplicationContext());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(false);
        rv_chat.setAdapter(adapter);
        rv_chat.setLayoutManager(lm);

        rv_chat.addOnScrollListener(new EndlessRecyclerOnScrollListener(lm) {
            @Override
            public void onLoadMore(int current_page) {
                updateChat(current_page);
            }
        });






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

        Log.i("ChatWindow", "onCreate: taskId:"+taskId);


        progressDialog.show();

        Call<ScheduledTask> call = dataInterface.getTasksById(Integer.valueOf(taskId),"Token "+key);
        call.enqueue(new Callback<ScheduledTask>() {
            @Override
            public void onResponse(Call<ScheduledTask> call, Response<ScheduledTask> response) {
                //Get task details

                if(response.isSuccessful() && response.body() != null) {
                    firstName = response.body().getCustomer().getUser().getFirstName();
                    lastName = response.body().getCustomer().getUser().getLastName();
                    profilePicUrl = response.body().getCustomer().getProfilePicUrl();
                    phoneNumber = response.body().getCustomer().getPhoneNo();
                    conversationId = response.body().getConversation() + "";

                    nameTv.setText(firstName.substring(0, 1).toUpperCase() + firstName.substring(1) + " " + lastName.substring(0, 1).toUpperCase() + lastName.substring(1));
                    Picasso.get()
                            .load(profilePicUrl)
                            .into(customerImg);

                    updateChat(1);


                    //For Socket pusposes send id of scout
                    JSONObject json = new JSONObject();
                    try {
                        json.put("id", response.body().getScout());
                        json.put("chat_type", "chat_between_scout_and_customer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mSocket.emit("setCache", json);
                } else {
                    Toast.makeText(ChatWindow.this, "Error, Try Again!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ScheduledTask> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ChatWindow.this, "Error, Try Again!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }



    @Override
    public void onBackPressed() {
        mSocket.disconnect();
        super.onBackPressed();
    }


    private void updateChat(final int currentPage) {

        Log.d(TAG, "updateChat: page: "+currentPage);

        Call<Messages> call = dataInterface.getMessages(conversationId,currentPage,"Token "+key,"scout");
        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(Call<Messages> call, Response<Messages> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    Messages messages = response.body();

                    results.addAll(messages.getResults());
                    adapter.notifyDataSetChanged();

                } else {
                    if(currentPage == 1){
                        Toast.makeText(ChatWindow.this, "Error, Try Again!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }



            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                if(currentPage == 1){
                    Toast.makeText(ChatWindow.this, "Error, Try Again!", Toast.LENGTH_SHORT).show();
                    finish();
                }
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

        Call<Result> call = dataInterface.createMessage(map,conversationId,"Token "+key,"scout","application/json");
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(response.isSuccessful()){

                    Log.i("InfoText","id:"+response.body().getId());

                    results.add(0,response.body());
                    sendButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    adapter.notifyItemInserted(0);
                    rv_chat.scrollToPosition(0);

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
                        Log.i("onChat:InfoText","done");
                        Log.i("onChat:InfoText","done: "+json.toString());
                        Result result = new Result();

                        result.setId(json.getInt("id"));
                        result.setCreatedAt(json.getString("created_at"));
                        result.setIsRead(json.getBoolean("is_read"));
                        result.setContent(json.getString("content"));

                        results.add(0,result);
                        Log.i("InfoText", String.valueOf(result));



                        adapter.notifyItemInserted(0);
                        rv_chat.scrollToPosition(0);

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
                        Log.i("onChat:InfoText","done");
                        Log.i("onChat:InfoText","done: "+json.toString());

                        /*result.setId(json.getInt("id"));
                        result.setCreatedAt(json.getString("created_at"));
                        result.setIsRead(json.getBoolean("is_read"));
                        result.setContent(json.getString("content"));
                        result.setRole(json.get());
*/

                        results.add(0,result);
                        Log.i("InfoText", String.valueOf(result));



                        adapter.notifyItemInserted(0);
                        rv_chat.scrollToPosition(0);




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



                        adapter.notifyItemInserted(0);
                        rv_chat.scrollToPosition(0);



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
//-8 5 3 1 6
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
