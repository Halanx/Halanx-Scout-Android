package com.technicalrj.halanxscouts.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Notification.NoficationPojo.Notification;
import com.technicalrj.halanxscouts.R;

import java.util.ArrayList;

/**
 * Created by Nishant on 19/12/17.
 */


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.HomesViewHolder> {

    Context c;
    ArrayList<Notification> notificationArrayList;


    public NotificationAdapter(Context context, ArrayList<Notification> notificationArrayList) {

        this.notificationArrayList = notificationArrayList;
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


        holder.notification_content.setText(notificationArrayList.get(position).getContent());
        String[] parts =   notificationArrayList.get(position).getTimestamp().split(" ");

        holder.time.setText(parts[0]+" "+parts[1] + " " + parts[2]);
        holder.date.setText(parts[3] + " "+ parts[4]);


        Picasso.get()
                .load( notificationArrayList.get(position).getCategory().getImage())
                .into(holder.notification_icon);


    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }


    public class HomesViewHolder extends RecyclerView.ViewHolder {

        TextView notification_content, time,date;
        ImageView notification_icon;

        public HomesViewHolder(View itemView) {
            super(itemView);

            notification_content = itemView.findViewById(R.id.notification_content);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            notification_icon = itemView.findViewById(R.id.notification_icon);
        }


    }

}
