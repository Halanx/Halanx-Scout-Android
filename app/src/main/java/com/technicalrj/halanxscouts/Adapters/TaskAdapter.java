package com.technicalrj.halanxscouts.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technicalrj.halanxscouts.R;

/**
 * Created by Nishant on 19/12/17.
 */


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.HomesViewHolder> {

    Context c;


    public TaskAdapter(Context context) {

    }

    @Override
    public HomesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tasks_rv, parent, false);
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

        RecyclerView rv_bed_detail;
        TextView tv_bed;

        public HomesViewHolder(View itemView) {
            super(itemView);
        }
    }

}
