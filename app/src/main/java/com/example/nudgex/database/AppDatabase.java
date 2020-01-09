package com.example.nudgex.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.nudgex.board.dao.BoardDao;
import com.example.nudgex.board.dao.ColumnDao;
import com.example.nudgex.board.dao.TaskDao;
import com.example.nudgex.board.entities.Board;
import com.example.nudgex.board.entities.Column;
import com.example.nudgex.board.entities.Task;
import com.example.nudgex.notes.dao.NoteImageDao;
import com.example.nudgex.notes.entities.Note;
import com.example.nudgex.notes.dao.NotesDao;
import com.example.nudgex.notes.entities.Image;

@Database(entities = {Board.class, Column.class, Task.class, Note.class, Image.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase INSTANCE;

    public abstract BoardDao boardDao();
    public abstract ColumnDao columnDao();
    public abstract TaskDao taskDao();
    public abstract NotesDao notesDao();
    public abstract NoteImageDao noteImageDao();

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
//            databaseWriteExecutor.execute(() -> {
//                // Populate the database in the background.
//                // If you want to start with more words, just add them.
//                WordDao dao = INSTANCE.wordDao();
//                dao.deleteAll();
//
//                Word word = new Word("Hello");
//                dao.insert(word);
//                word = new Word("World");
//                dao.insert(word);
//            });
        }
    };

    public static AppDatabase getOfflineDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,"app-database")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void closeInstance(){
        INSTANCE.close();
        INSTANCE = null;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }

}
