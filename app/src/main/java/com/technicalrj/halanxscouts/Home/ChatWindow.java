package com.technicalrj.halanxscouts.Home;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.technicalrj.halanxscouts.Adapters.ChatAdapter;
import com.technicalrj.halanxscouts.Home.Chat.Messages;
import com.technicalrj.halanxscouts.Home.Chat.Result;
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

    String id,firstName,lastName;
    ChatAdapter adapter;
    ArrayList<Result> results;
    String key;
    EditText chat_text;
    LinearLayoutManager lm;
    int page=1;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);


        id = getIntent().getStringExtra("id");
        firstName = getIntent().getStringExtra("first_name");
        lastName = getIntent().getStringExtra("last_name");
        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle( firstName.substring(0,1).toUpperCase() + firstName.substring(1)  +" "+ lastName.substring(0,1).toUpperCase() + lastName.substring(1) );






        chat_text = findViewById(R.id.chat_text);
        if(chat_text.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        final RecyclerView rv_chat = findViewById(R.id.rv_chat);
        results = new ArrayList<>();
        adapter = new ChatAdapter(getApplicationContext(),results);
        lm = new LinearLayoutManager(getApplicationContext());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(false);
        rv_chat.setAdapter(adapter);
        rv_chat.setLayoutManager(lm);


        updateChat(page);


        try {
            IO.Options opts = new IO.Options();
            mSocket = IO.socket("https://consumerchat.herokuapp.com");
            mSocket.on(Socket.EVENT_CONNECT, onConnect);

            mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Transport transport = (Transport)args[0];
                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                            // modify request headers
                            headers.put("Authorization", Arrays.asList( "Token "+key));
                            headers.put("PARTICIPANT-TYPE", Arrays.asList("scout"));
                            // modify request headers
                        }
                    }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.i("InfoText", "error header call: "+args[0]);
                        }
                    });
                }
            });
            mSocket.connect();
            mSocket.on("chat_event", chat_event);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
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


                    adapter.notifyDataSetChanged();
                    chat_text.setText("");
                }else {
                    try {
                        Log.i("InfoText","sending messege error:"+response.errorBody().string());
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




}
