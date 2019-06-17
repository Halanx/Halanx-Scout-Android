package com.technicalrj.halanxscouts.Adapters;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Home.ChatWindow;
import com.technicalrj.halanxscouts.Home.TaskActivity;
import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.R;

import java.util.ArrayList;

/**
 * Created by Nishant on 19/12/17.
 */


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.HomesViewHolder> {

    Context c;
    ArrayList<ScheduledTask> scheduledTaskList;

    public TaskAdapter(Context context, ArrayList<ScheduledTask> scheduledTaskList) {
        c = context;
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



        holder.task_name.setText(scheduledTaskList.get(position).getCategory().getName());
        holder.address.setText(scheduledTaskList.get(position).getHouse().getAddress().getStreetAddress());
        holder.earning.setText("₹ "+scheduledTaskList.get(position).getEarning()+"");

        String[] parts = scheduledTaskList.get(position).getScheduledAt().split(" ") ;
        String date = parts[0]+" "+parts[1].substring(0,3).toUpperCase() +" ("+ parts[3]+" "+parts[4]+")";
        holder.date_time.setText(date);


        holder.house_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double latitute = scheduledTaskList.get(position).getHouse().getAddress().getLatitude();
                double longitute = scheduledTaskList.get(position).getHouse().getAddress().getLongitude();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:28.6514074,77.2400794?q="+latitute+","+longitute+"(House)"));
                c.startActivity(intent);




            }
        });

        holder.lin_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, TaskActivity.class);
                intent.putExtra("id",scheduledTaskList.get(position).getId());
                c.startActivity(intent);
            }
        });

        if(scheduledTaskList.get(position).getCustomer()==null){
            holder.lin_layout.setVisibility(View.INVISIBLE);
            return;
        }


        final String firstName = scheduledTaskList.get(position).getCustomer().getUser().getFirstName();
        final String lastName = scheduledTaskList.get(position).getCustomer().getUser().getLastName();

        holder.customer_name.setText( firstName.substring(0,1).toUpperCase() + firstName.substring(1)  +" "+ lastName.substring(0,1).toUpperCase() + lastName.substring(1) );



        Picasso.get()
                .load( scheduledTaskList.get(position).getCustomer().getProfilePicThumbnailUrl())
                .into(holder.customer_img);


        holder.chat_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.startActivity(new Intent(c, ChatWindow.class)
                        .putExtra("id",scheduledTaskList.get(position).getConversation())
                        .putExtra("name",firstName+" "+lastName));
            }
        });



        holder.call_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent callIntent = new Intent(Intent.ACTION_VIEW );
                    callIntent.setData(Uri.parse("tel:"+scheduledTaskList.get(position).getCustomer().getPhoneNo()));
                    c.startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Toast.makeText(c,"Calling a Phone Number Call failed",Toast.LENGTH_SHORT).show();
                }
            }
        });







    }

    @Override
    public int getItemCount() {
        return scheduledTaskList.size();
    }


    public class HomesViewHolder extends RecyclerView.ViewHolder {

        ImageView customer_img,house_location , chat_icon ,call_icon;
        TextView task_name,address,earning,date_time,customer_name;
        LinearLayout lin_layout;

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


        }




    }

}
