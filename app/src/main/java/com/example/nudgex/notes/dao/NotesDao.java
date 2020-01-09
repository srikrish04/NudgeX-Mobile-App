package com.example.nudgex.notes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nudgex.notes.entities.Note;

import java.util.List;

@Dao
public interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY id")
    List<Note> getNotes();

    @Query("SELECT * FROM notes ORDER BY id")
    LiveData<List<Note>> getLiveNotes();

    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(int noteId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Update
    void updateNote(Note note);

    @Query("DELETE from notes")
    void deleteAll();
}
