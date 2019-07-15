package com.technicalrj.halanxscouts.Profile;

import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.R;

public class DocumentImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_image);

        String imgUrl = getIntent().getStringExtra("image_url");
        ImageView imageView = findViewById(R.id.imageView);

        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("bitmap_url");

       if(imgUrl==null){

           imageView.setImageBitmap(bitmap);
       }else {
           Picasso.get()
                   .load(imgUrl)
                   .into(imageView);
       }




    }
}
