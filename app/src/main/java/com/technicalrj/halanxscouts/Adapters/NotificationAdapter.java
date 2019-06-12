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


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.HomesViewHolder> {

    Context c;


    public NotificationAdapter(Context context) {

        c = context;
    }

    @Override
    public HomesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notification, parent, false);
        HomesViewHolder holder = new HomesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HomesViewHolder holder, final int position) {


    }

    @Override
    public int getItemCount() {
        return 5;
    }


    public class HomesViewHolder extends RecyclerView.ViewHolder {


        public HomesViewHolder(View itemView) {
            super(itemView);
        }


    }

}
