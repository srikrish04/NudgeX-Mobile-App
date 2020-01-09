package com.example.nudgex.notes.entities;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_image")
public class Image {
    @PrimaryKey(autoGenerate = true)
    public int imageId;
    public int noteId;
    public String imageUrl;
}
