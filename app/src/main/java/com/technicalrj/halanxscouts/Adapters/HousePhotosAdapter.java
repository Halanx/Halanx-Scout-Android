package com.technicalrj.halanxscouts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Home.Onboarding.fragments.UploadPhotosFragment;
import com.technicalrj.halanxscouts.R;

import java.io.File;
import java.util.ArrayList;

public class HousePhotosAdapter extends RecyclerView.Adapter<HousePhotosAdapter.ViewHolder> {


    Context context;
    private UploadPhotosFragment.OnUploadPhotoInteractionListener listener;
    private ArrayList<String> imageUrlList;

    public HousePhotosAdapter(Context context, UploadPhotosFragment.OnUploadPhotoInteractionListener listener, ArrayList<String> imageUrlList) {
        this.context = context;
        this.listener = listener;
        this.imageUrlList = imageUrlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_pic_layout, parent, false);
        HousePhotosAdapter.ViewHolder holder = new HousePhotosAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get()
                .load(imageUrlList.get(position))
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
