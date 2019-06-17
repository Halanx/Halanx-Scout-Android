package com.technicalrj.halanxscouts.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technicalrj.halanxscouts.Home.Chat.Result;
import com.technicalrj.halanxscouts.R;

import java.util.ArrayList;

/**
 * Created by Nishant on 19/12/17.
 */


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.HomesViewHolder> {

    Context c;
    ArrayList<Result> results;

    public ChatAdapter(Context context, ArrayList<Result> results) {

        this.results = results;
        this.c = context;
    }

    @Override
    public HomesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat, parent, false);
        HomesViewHolder holder = new HomesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HomesViewHolder holder, final int position) {

        if (position%2==0){

            holder.mssg_send_box.setVisibility(View.GONE);
            holder.mssg_rcv_box.setVisibility(View.VISIBLE);
            holder.mssg_rcv_box.setText("Hi... How are you?");
        }
        else{
            holder.mssg_send_box.setVisibility(View.VISIBLE);
            holder.mssg_rcv_box.setVisibility(View.GONE);
            holder.mssg_send_box.setText("Hi... i m fine");

        }

    }

    @Override
    public int getItemCount() {
        return 5;
    }


    public class HomesViewHolder extends RecyclerView.ViewHolder {

        TextView mssg_rcv_box,mssg_send_box;

        public HomesViewHolder(View itemView) {
            super(itemView);
            mssg_rcv_box = itemView.findViewById(R.id.mssg_rcv_box);
            mssg_send_box = itemView.findViewById(R.id.mssg_send_box);
        }
    }

}
