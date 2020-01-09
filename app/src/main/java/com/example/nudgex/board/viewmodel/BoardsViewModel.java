package com.example.nudgex.board.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nudgex.database.AppDatabase;
import com.example.nudgex.board.entities.Board;

import java.util.List;

public class BoardsViewModel extends AndroidViewModel {
    private MutableLiveData title = new MutableLiveData<String>();
    public LiveData<List<Board>> boards;
    private AppDatabase appDB;

    public void updateActionBarTitle(String title){
        this.title.postValue(title);
    }
    public BoardsViewModel(Application application){
        super(application);
        appDB = AppDatabase.getOfflineDatabase(application);
        refreshBoardsList();
    }
    public LiveData<String> getTitle(){
        return title;
    }

    public void refreshBoardsList(){
        this.boards = appDB.boardDao().getBoards();
    }
    public LiveData<List<Board>> getBoardsList() {
        return boards;
    }

}
