package com.example.nudgex.notes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nudgex.R;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview_whole);
        ImageView iv = findViewById(R.id.imageView);
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        File imgFile = new File(imageUrl);
        Bitmap bitmap=null;
        try {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            iv.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
