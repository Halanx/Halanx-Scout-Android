package com.technicalrj.halanxscouts.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Home.Onboarding.fragments.UploadPhotosFragment;
import com.technicalrj.halanxscouts.Pojo.HouseImage;
import com.technicalrj.halanxscouts.R;

import java.io.File;
import java.util.ArrayList;

public class HousePhotosAdapter extends RecyclerView.Adapter<HousePhotosAdapter.ViewHolder> {


    Context context;
    private OnPhotoClick listener;
    private ArrayList<HouseImage> houseImagesArrayList;

//    public HousePhotosAdapter(Context context,OnPhotoClick listener, ArrayList<String> imageUrlList) {
//        this.context = context;
//        this.listener = listener;
//        this.imageUrlList = imageUrlList;
//    }

    public HousePhotosAdapter(Context context, OnPhotoClick listener, ArrayList<HouseImage> houseImagesArrayList) {
        this.context = context;
        this.listener = listener;
        this.houseImagesArrayList = houseImagesArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_pic_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HouseImage houseImage = houseImagesArrayList.get(position);

        if(houseImage.getStatus() == HouseImage.UPLOADING){
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.retryLayout.setVisibility(View.INVISIBLE);
        } else if(houseImage.getStatus() == HouseImage.ERROR){
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.retryLayout.setVisibility(View.VISIBLE);
        } else if(houseImage.getStatus() == HouseImage.UPLOADED){
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.retryLayout.setVisibility(View.INVISIBLE);
        }

        holder.imageView.setImageBitmap(houseImage.getBitmap());

    }

    @Override
    public int getItemCount() {
        return houseImagesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        LinearLayout retryLayout;
        ProgressBar progressBar;


        public ViewHolder(@NonNull final View itemView, final OnPhotoClick listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            retryLayout = itemView.findViewById(R.id.retry_layout);
            progressBar = itemView.findViewById(R.id.progressBar2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPhotoClick(itemView);
                }
            });

        }



    }

    public interface OnPhotoClick{
        void onPhotoClick(View view);
    }
}
