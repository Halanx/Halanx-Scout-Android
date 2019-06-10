package com.technicalrj.halanxscouts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.technicalrj.halanxscouts.Adapters.ChatAdapter;

public class ChatWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        RecyclerView rv_chat = findViewById(R.id.rv_chat);
        ChatAdapter adapter = new ChatAdapter(getApplicationContext());
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        rv_chat.setAdapter(adapter);
        rv_chat.setLayoutManager(lm);
    }
}