package com.technicalrj.halanxscouts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.technicalrj.halanxscouts.R;

public class AmenetiesAdapter extends RecyclerView.Adapter<AmenetiesAdapter.ViewHolder> {

    Context context;

    public AmenetiesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_adapter_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        if(position%2==1){
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.colorGrayAdapter));
        }

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView sn,amenities;
        RadioGroup radioGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rel_layout);
            sn = itemView.findViewById(R.id.sn1);
            amenities = itemView.findViewById(R.id.amenities1);
            radioGroup = itemView.findViewById(R.id.radiogroup);
        }


    }
}
