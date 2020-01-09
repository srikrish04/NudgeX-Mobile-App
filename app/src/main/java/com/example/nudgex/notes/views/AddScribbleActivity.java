package com.example.nudgex.notes.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nudgex.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddScribbleActivity extends AppCompatActivity {
    CanvasView canvasView;
    ImageButton btnBlack, btnBlue, btnGreen;
    int noteId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_scribble);
        canvasView = findViewById(R.id.canvasView);
        Intent intent = getIntent();
        Button btnSaveScribble = findViewById(R.id.btnSaveScribble);
        ImageButton btnBlack = findViewById(R.id.colorBlack);
        ImageButton btnBlue = findViewById(R.id.colorBlue);
        ImageButton btnGreen = findViewById(R.id.colorGreen);
        btnSaveScribble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
        btnBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setPathColor(Color.BLACK);
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setPathColor(Color.BLUE);
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setPathColor(Color.GREEN);
            }
        });

    }

    public void saveImage() {
        Bitmap bitmap = canvasView.viewToBitmap();
        String fileName = Long.toString(System.currentTimeMillis()) + "-note.jpg";
        Intent resultIntent = new Intent();
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        resultIntent.putExtra("filePath", file.getAbsolutePath());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                    System.out.println("Saved");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
