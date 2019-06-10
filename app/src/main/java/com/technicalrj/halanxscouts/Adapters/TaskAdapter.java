package com.technicalrj.halanxscouts.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.technicalrj.halanxscouts.ChatWindow;
import com.technicalrj.halanxscouts.MoveInActivity;
import com.technicalrj.halanxscouts.R;

/**
 * Created by Nishant on 19/12/17.
 */


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.HomesViewHolder> {

    Context c;


    public TaskAdapter(Context context) {

        c = context;
    }

    @Override
    public HomesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tasks_rv, parent, false);
        HomesViewHolder holder = new HomesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HomesViewHolder holder, final int position) {

        holder.ll_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.startActivity(new Intent(c, MoveInActivity.class));

            }
        });

        holder.chat_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.startActivity(new Intent(c, ChatWindow.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return 5;
    }


    public class HomesViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_bed_detail;
        TextView tv_bed;
        ImageView chat_icon;

        LinearLayout ll_layout;
        public HomesViewHolder(View itemView) {
            super(itemView);
            chat_icon = itemView.findViewById(R.id.chat_icon);
            ll_layout = itemView.findViewById(R.id.ll_layout);
        }


    }

}
