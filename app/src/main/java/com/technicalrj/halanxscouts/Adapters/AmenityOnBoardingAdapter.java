package com.technicalrj.halanxscouts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Pojo.AmenityOnBoarding;
import com.technicalrj.halanxscouts.R;

import java.util.ArrayList;

public class AmenityOnBoardingAdapter extends RecyclerView.Adapter<AmenityOnBoardingAdapter.AmenityOnBoardingViewHolder> {

    private Context context;
    private ArrayList<AmenityOnBoarding> amenityArrayList;
    private OnAmenityOnBoardingClickListener listener;

    public AmenityOnBoardingAdapter(Context context, ArrayList<AmenityOnBoarding> amenityArrayList,
                                    OnAmenityOnBoardingClickListener listener) {
        this.context = context;
        this.amenityArrayList = amenityArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AmenityOnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.amenity_onboarding_item, parent, false);
        return new AmenityOnBoardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityOnBoardingViewHolder holder, int position) {
        AmenityOnBoarding amenityOnBoarding = amenityArrayList.get(position);
        Picasso.get()
                .load(amenityOnBoarding.getImage())
                .into(holder.imageView);
        holder.selectImageView.setActivated(amenityOnBoarding.isSelected());
    }

    @Override
    public int getItemCount() {
        return amenityArrayList.size();
    }

    public class AmenityOnBoardingViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private ImageView selectImageView;

        public AmenityOnBoardingViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            selectImageView = itemView.findViewById(R.id.select_image_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAmenityClicked(itemView, selectImageView);
                }
            });
        }
    }

    public interface OnAmenityOnBoardingClickListener{
        void onAmenityClicked(View rootView, ImageView selectImageView);
    }

}
