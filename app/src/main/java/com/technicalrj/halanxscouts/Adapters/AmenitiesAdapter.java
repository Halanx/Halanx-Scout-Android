package com.technicalrj.halanxscouts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.technicalrj.halanxscouts.Home.MoveOut.AmenitiesResponse;
import com.technicalrj.halanxscouts.R;

import java.util.ArrayList;

public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AmenitiesResponse.Amenity> amenityArrayList;
    private String TAG = AmenitiesAdapter.class.getSimpleName();
    private OnAmenityCheckedListener listener;

    public AmenitiesAdapter(Context context, ArrayList<AmenitiesResponse.Amenity> amenityArrayList,
                            OnAmenityCheckedListener listener) {
        this.context = context;
        this.amenityArrayList = amenityArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_adapter_row, parent, false);
        ViewHolder holder = new ViewHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AmenitiesResponse.Amenity amenity = amenityArrayList.get(position);
        holder.sn.setText(amenity.getId());
        holder.amenities.setText(amenity.getName());

        if(position%2==1){
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.colorGrayAdapter));
        }


    }

    @Override
    public int getItemCount() {
        return amenityArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView sn,amenities;
        RadioGroup radioGroup;

        public ViewHolder(final View itemView, final OnAmenityCheckedListener listener) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rel_layout);
            sn = itemView.findViewById(R.id.sn1);
            amenities = itemView.findViewById(R.id.amenities1);
            radioGroup = itemView.findViewById(R.id.radiogroup);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i){
                    listener.onAmenityChecked(radioGroup, i, itemView);
                }
            });

        }


    }


    public interface OnAmenityCheckedListener{
        void onAmenityChecked(RadioGroup radioGroup, int radioButtonId, View rootView);
    }
}
