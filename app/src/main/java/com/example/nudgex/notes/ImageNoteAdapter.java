package com.example.nudgex.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nudgex.R;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.File;
import java.util.ArrayList;

public class ImageNoteAdapter extends RecyclerView.Adapter<ImageNoteAdapter.ImageHolder> {
    ArrayList<String> images;
    Context context;
    public ImageNoteAdapter(Context context, ArrayList<String> objects) {
        this.context = context;
        images = objects;
    }
    public void setImages(ArrayList<String> images){
//        this.images.clear();
        this.images = images;

    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_note_card_item, parent,
                        false);
        ImageHolder holder = new ImageHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        String image = images.get(position);
        ImageView ivImage = holder.itemView.findViewById(R.id.image);
        File imgFile = new File(image);
        if (imgFile.exists()) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }catch (Exception e){
                e.printStackTrace();
            }
            if(bitmap != null) {
                Bitmap scaledBitmap = scaleDown(bitmap, 300, true);
                ivImage.setImageBitmap(scaledBitmap);
            }
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtra("imageUrl", image);
                    context.startActivity(intent);
                }
            });
            ViewGroup.LayoutParams lp = ivImage.getLayoutParams();
            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexboxLp =
                        (FlexboxLayoutManager.LayoutParams) ivImage.getLayoutParams();
                flexboxLp.setFlexGrow(0.5f);
            }
        }
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
//            int origWidth = bitmap.getWidth();
//            int origHeight = bitmap.getHeight();
//            Bitmap scaledBitmap = scaleDown(bitmap, 300, true);
//            ivImage.setImageBitmap(scaledBitmap);
//            ivImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, ImageViewActivity.class);
//                    intent.putExtra("imageUrl", image);
//                    context.startActivity(intent);
//                }
//            });
//            ViewGroup.LayoutParams lp = ivImage.getLayoutParams();
//            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
//                FlexboxLayoutManager.LayoutParams flexboxLp =
//                        (FlexboxLayoutManager.LayoutParams) ivImage.getLayoutParams();
//                flexboxLp.setFlexGrow(1.0f);
//            }
//        } catch (Exception e) {
//        }
    }


    @Override
    public int getItemCount() {
        System.out.println("Item Count " + images.size());
        return images.size();
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {


        public ImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
