package com.example.nudgex.notes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nudgex.notes.entities.Image;

import java.util.List;

@Dao
public interface NoteImageDao {
    @Query("SELECT * FROM note_image WHERE noteId=:noteId ORDER BY imageId")
    List<Image> getImagesByNoteId(int noteId);

    @Query("SELECT * FROM note_image WHERE noteId=:noteId ORDER BY imageId")
    LiveData<List<Image>> getLiveImagesByNoteId(int noteId);

    @Query("SELECT * FROM note_image WHERE imageId=:imageId")
    Image getImageById(int imageId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertImage(Image image);

    @Delete
    void deleteImage(Image image);

    @Update
    void updateImage(Image image);

    @Query("DELETE from note_image")
    void deleteAll();

}
