package com.technicalrj.halanxscouts.Adapters;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Constants;
import com.technicalrj.halanxscouts.Home.ChatWindow;
import com.technicalrj.halanxscouts.Home.MoveOut.MoveOutActivity;
import com.technicalrj.halanxscouts.Home.Onboarding.OnboardingActivity;
import com.technicalrj.halanxscouts.Home.TaskActivity;
import com.technicalrj.halanxscouts.Home.TaskFolder.Category;
import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.Pojo.CustomData;
import com.technicalrj.halanxscouts.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nishant on 19/12/17.
 */


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.HomesViewHolder> {

    private static final int TASK_CLICKED = 12;
    private Context c;
    private ArrayList<ScheduledTask> scheduledTaskList;
    double latitute = 0,longitute =0;
    private String tel = "";

    public TaskAdapter(Context c, ArrayList<ScheduledTask> scheduledTaskList) {
        this.c = c;
        this.scheduledTaskList = scheduledTaskList;

    }

    @Override
    public HomesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tasks_rv, parent, false);
        HomesViewHolder holder = new HomesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HomesViewHolder holder, final int position) {


        final ScheduledTask scheduledTask = scheduledTaskList.get(position);

        final String category = scheduledTask.getCategory().getName();
        holder.task_name.setText(category);
        holder.earning.setText(scheduledTask.getEarning() + "");
        holder.date_time.setText(scheduledTask.getScheduledAt());


//        customer is null in onboarding and custom data field is available only with onboarding
//        custom data contains name, phone, lat, lng, address if task is assigned by halanx
//                                OR
//        custom data only contains address
        if(category.equals(Category.PROPERTY_ONBOARDING)) {

            CustomData customData = scheduledTask.getCustomData();
            holder.address.setText(customData.getLocation());

            if(customData.getLatitude() != null && customData.getLongitude() != null){
                latitute = customData.getLatitude();
                longitute = customData.getLongitude();
                holder.house_location.setVisibility(View.VISIBLE);
            } else {
                holder.house_location.setVisibility(View.INVISIBLE);
            }

            if(customData.getPhoneNo() == null){
                holder.call_icon.setVisibility(View.GONE);
            } else {
                holder.call_icon.setVisibility(View.VISIBLE);
                tel = customData.getPhoneNo();
            }

            if(customData.getName() == null){
                holder.customer_name.setVisibility(View.GONE);
                holder.customer_img.setVisibility(View.GONE);
                holder.lineView.setVisibility(View.GONE);
            } else {
                holder.customer_name.setVisibility(View.VISIBLE);
                holder.customer_img.setVisibility(View.VISIBLE);
                holder.customer_name.setText(customData.getName());
            }

            holder.chat_icon.setVisibility(View.GONE);

        } else if(category.equals(Category.HOUSE_VISIT)) {

            holder.address.setText(scheduledTaskList.get(position).getHouse().getAddress().getStreetAddress());
            latitute = scheduledTaskList.get(position).getHouse().getAddress().getLatitude();
            longitute = scheduledTaskList.get(position).getHouse().getAddress().getLongitude();
            tel = scheduledTaskList.get(position).getCustomer().getPhoneNo();

            holder.call_icon.setVisibility(View.GONE);

            if(scheduledTask.getCustomer() != null) {

                holder.chat_icon.setVisibility(View.VISIBLE);

                final String firstName = scheduledTask.getCustomer().getUser().getFirstName();
                final String lastName = scheduledTask.getCustomer().getUser().getLastName();
                holder.customer_name.setText(firstName+ " "+lastName);

                Picasso.get()
                        .load(scheduledTaskList.get(position).getCustomer().getProfilePicThumbnailUrl())
                        .into(holder.customer_img);


                holder.chat_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c.startActivity(new Intent(c, ChatWindow.class)
                                .putExtra("conversation", scheduledTask.getId())
                                .putExtra("first_name", firstName)
                                .putExtra("last_name", lastName)
                                .putExtra("profile_pic_url", scheduledTask.getCustomer().getProfilePicUrl())
                                .putExtra("phone_number", scheduledTask.getCustomer().getPhoneNo()));
                    }
                });
            } else {
                holder.customer_img.setVisibility(View.GONE);
                holder.chat_icon.setVisibility(View.GONE);
                holder.lineView.setVisibility(View.GONE);
            }

        }

        holder.house_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" +
                        latitute + "," + longitute + "(House)"));
                c.startActivity(intent);
            }
        });

        holder.lin_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduledTask scheduledTask = scheduledTaskList.get(position);

                if (scheduledTask.getCategory().getName().equalsIgnoreCase(Category.MOVE_OUT)) {
                    Intent intent = new Intent(c, MoveOutActivity.class);
                    intent.putExtra("id", scheduledTaskList.get(position).getId());
                    ((Activity) c).startActivityForResult(intent, TASK_CLICKED);

                } else if (scheduledTask.getCategory().getName().equalsIgnoreCase(Category.HOUSE_VISIT)) {
                    Intent intent = new Intent(c, TaskActivity.class);
                    intent.putExtra("id", scheduledTaskList.get(position).getId());
                    ((Activity) c).startActivityForResult(intent, TASK_CLICKED);
                } else if (scheduledTask.getCategory().getName().equalsIgnoreCase(Category.PROPERTY_ONBOARDING)) {
                    Intent intent = new Intent(c, OnboardingActivity.class);
                    intent.putExtra(Constants.TASK_ID, scheduledTaskList.get(position).getId());
                    intent.putExtra(Constants.TASK_EARNING, scheduledTaskList.get(position).getEarning());
                    ((Activity) c).startActivityForResult(intent, TASK_CLICKED);
                }
            }
        });

        holder.call_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_VIEW);
                    callIntent.setData(Uri.parse("tel:" + tel));
                    c.startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Toast.makeText(c, "Calling a Phone Number Call failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return scheduledTaskList.size();
    }


    public class HomesViewHolder extends RecyclerView.ViewHolder {

        ImageView customer_img, house_location, chat_icon, call_icon;
        TextView task_name, address, earning, date_time, customer_name;
        LinearLayout lin_layout;
        RelativeLayout tenantLayout;
        View lineView;

        public HomesViewHolder(View itemView) {
            super(itemView);

            task_name = itemView.findViewById(R.id.task_name);
            address = itemView.findViewById(R.id.address);
            earning = itemView.findViewById(R.id.earning);
            date_time = itemView.findViewById(R.id.date_time);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_img = itemView.findViewById(R.id.customer_img);
            house_location = itemView.findViewById(R.id.house_location);
            call_icon = itemView.findViewById(R.id.call_icon);
            chat_icon = itemView.findViewById(R.id.chat_icon);
            lin_layout = itemView.findViewById(R.id.ll_layout);
            tenantLayout = itemView.findViewById(R.id.tenantLayout);
            lineView = itemView.findViewById(R.id.line_view);


        }


    }


    public boolean isPreviousDay(String scheduledDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.getDefault());
        try {
            Date date = dateFormat.parse(scheduledDate);
            Log.i("TaskAdapter", "isPreviousDay: date.getTime:" + date.getTime() + " current:" + System.currentTimeMillis() + " diff:" + (date.getTime() - System.currentTimeMillis()));
            if (date.getTime() - System.currentTimeMillis() <= 24 * 60 * 60 * 1000)
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return false;
    }


}
