package com.example.nudgex;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.nudgex.database.AppDatabase;
import com.example.nudgex.board.entities.Board;

import java.util.List;

public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static Board addUser(final AppDatabase db, Board user) {
        db.boardDao().insertBoard(user);
        return user;
    }

    private static void populateWithTestData(AppDatabase db) {
        Board user = new Board();
        user.name = "Ajay";
        addUser(db, user);

        LiveData<List<Board>> userList = db.boardDao().getBoards();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userList.getValue().size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }
}

