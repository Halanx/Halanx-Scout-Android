package com.technicalrj.halanxscouts.Home;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.technicalrj.halanxscouts.Adapters.ChatAdapter;
import com.technicalrj.halanxscouts.Home.Chat.Messages;
import com.technicalrj.halanxscouts.Home.Chat.Result;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatWindow extends AppCompatActivity {

    String id,name;
    ChatAdapter adapter;
    ArrayList<Result> results;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);


        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        final SharedPreferences prefs = getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(name);





        RecyclerView rv_chat = findViewById(R.id.rv_chat);
        adapter = new ChatAdapter(getApplicationContext(),results);
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        lm.setReverseLayout(true);
        rv_chat.setAdapter(adapter);
        rv_chat.setLayoutManager(lm);



        updateChat();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }


    private void updateChat() {

        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Messages> call = availabilityInterface.getMessages(id,"Token "+key,"scout");
        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(Call<Messages> call, Response<Messages> response) {
                Messages messages = response.body();
                results = (ArrayList<Result>) messages.getResults();
                for (int i = 0; i <results.size() ; i++) {

                }
            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {

            }
        });


    }
}
