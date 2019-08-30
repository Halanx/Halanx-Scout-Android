package com.technicalrj.halanxscouts.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        holder.sn.setText(""+amenity.getId());
        holder.amenities.setText(amenity.getName());
        holder.quantityTextView.setText("" + amenity.getQuantity());

        if(amenity.getStatus().equalsIgnoreCase(AmenitiesResponse.STATUS_OK)){
            holder.spinner.setSelection(1);
        } else if(amenity.getStatus().equalsIgnoreCase(AmenitiesResponse.STATUS_DAMAGED)){
            holder.spinner.setSelection(2);
        } else if(amenity.getStatus().equalsIgnoreCase(AmenitiesResponse.STATUS_MISSING)){
            holder.spinner.setSelection(3);
        } else {
            holder.spinner.setSelection(0);
        }

//
        if(position%2==1){
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.colorGrayAdapter));
        }


    }

    @Override
    public int getItemCount() {
        return amenityArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout rootLayout;
        TextView sn,amenities;
        Spinner spinner;
        Button plusButton, minusButton;
        TextView quantityTextView;

        public ViewHolder(final View itemView, final OnAmenityCheckedListener listener) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rel_layout);
            sn = itemView.findViewById(R.id.sn1);
            amenities = itemView.findViewById(R.id.amenities1);
            spinner = itemView.findViewById(R.id.spinner);
            plusButton = itemView.findViewById(R.id.plus_button);
            minusButton = itemView.findViewById(R.id.minus_button);
            quantityTextView = itemView.findViewById(R.id.count_text_view);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.item_status_array,
                    R.layout.my_spinner_item_text_view);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemSelected: "+parent.getItemAtPosition(position).toString());
                    listener.onAmenityStatusSelected(itemView, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d(TAG, "onNothingSelected: ");
                }
            });

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlusClicked(itemView);
                }
            });

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMinusClicked(itemView);
                }
            });

        }


    }


    public interface OnAmenityCheckedListener{

        void onAmenityStatusSelected(View rootView, int selectedPosition);

        void onPlusClicked(View rootView);

        void onMinusClicked(View rootView);
    }
}
