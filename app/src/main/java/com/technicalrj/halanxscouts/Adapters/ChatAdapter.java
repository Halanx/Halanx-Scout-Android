package com.technicalrj.halanxscouts.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    TextView last_chat_date = null;

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

        Result result = results.get(position);

        String[] parts = result.getCreatedAt().split(" ");
        String time = parts[3]+" "+parts[4];
        String date = parts[0] +" "+parts[1];





        if (result.getRole().equals("sender")){


            holder.send_layout.setVisibility(View.VISIBLE);
            holder.mssg_send_box.setText(result.getContent());
            holder.reciever_layout.setVisibility(View.GONE);

            if(result.getIsRead()){
                holder.seen_status.setVisibility(View.GONE);
                holder.seen_status.setText("SEEN");
            }else {
                holder.seen_status.setVisibility(View.GONE);
            }
            holder.send_time.setText(time);





        } else{

            holder.reciever_layout.setVisibility(View.VISIBLE);
            holder.send_layout.setVisibility(View.GONE);
            holder.seen_status.setVisibility(View.GONE);


            holder.mssg_rcv_box.setText(result.getContent());
            holder.recivied_time.setText(time);
            Log.i("InfoText","resule reciver text:"+holder.reciever_layout.getVisibility());




        }


        //For showing date or not

        holder.chat_date.setText(date);
        holder.chat_date.setVisibility(View.VISIBLE);
        if(position+1<results.size()){

            String[] parts2 = results.get(position+1).getCreatedAt().split(" ");
            String nextDate = parts2[0] +" "+parts2[1];

            if(!date.equals(nextDate)){
                holder.chat_date.setVisibility(View.VISIBLE);
            }else {
                holder.chat_date.setVisibility(View.GONE);
            }
        }



    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    public class HomesViewHolder extends RecyclerView.ViewHolder {

        LinearLayout reciever_layout,send_layout;
        TextView send_time,recivied_time,seen_status;
        TextView mssg_rcv_box,mssg_send_box;
        TextView chat_date;

        public HomesViewHolder(View itemView) {
            super(itemView);
            mssg_rcv_box = itemView.findViewById(R.id.mssg_rcv_box);
            mssg_send_box = itemView.findViewById(R.id.mssg_send_box);

            reciever_layout = itemView.findViewById(R.id.reciever_layout);
            send_layout = itemView.findViewById(R.id.send_layout);
            send_time = itemView.findViewById(R.id.send_time);
            recivied_time = itemView.findViewById(R.id.recivied_time);
            seen_status = itemView.findViewById(R.id.seen_status);
            chat_date = itemView.findViewById(R.id.chat_date);

        }
    }

}
