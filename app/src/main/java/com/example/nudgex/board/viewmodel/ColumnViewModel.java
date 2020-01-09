package com.example.nudgex.board.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nudgex.board.entities.Column;
import com.example.nudgex.board.entities.Task;
import com.example.nudgex.database.AppDatabase;

import java.util.List;

public class ColumnViewModel extends AndroidViewModel {
    public List<Column> columns;
    private AppDatabase appDB;

    public ColumnViewModel(@NonNull Application application) {
        super(application);
        appDB = AppDatabase.getOfflineDatabase(application);
    }

    public List<Column> getColumnList(int boardId) {
        columns = appDB.columnDao().getColumnByBoardId(boardId);
        return columns;
    }

}
