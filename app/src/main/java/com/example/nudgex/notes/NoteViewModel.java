package com.example.nudgex.notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.nudgex.database.AppDatabase;
import com.example.nudgex.notes.entities.Image;

import java.util.ArrayList;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    ArrayList<String> imageUriList;
    ArrayList<String> databaseStrings;
    MutableLiveData<ArrayList<String>> imageUriLiveList;
    LiveData<List<Image>> liveDatabaseImageList;
    public AppDatabase appDB;
    long noteId;
    LifecycleOwner activity;
    public NoteViewModel(@NonNull Application application) {
        super(application);
        imageUriList = new ArrayList<>();
        appDB = AppDatabase.getOfflineDatabase(getApplication());
    }
    public void setActivity(LifecycleOwner activity){
        this.activity = activity;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public ArrayList<String> getImageUriList() {
        return imageUriList;
    }


    public void clearList() {
        imageUriList.clear();
    }

    public void insertImageUri(String s) {
        imageUriList.add(s);
        upDateLists();
    }

    public void upDateLists() {
        databaseStrings = new ArrayList<>();
        if (noteId != -1) {
            getDatabaseImageUriList(noteId).observe(activity, new Observer<List<Image>>() {
                @Override
                public void onChanged(List<Image> images) {
                    for (Image i : images) {
                        databaseStrings.add(i.imageUrl);
                    }
                }
            });
        }
        databaseStrings.addAll(imageUriList);
    }

    public LiveData<List<Image>> getDatabaseImageUriList(long noteId) {
        if(liveDatabaseImageList == null) {
            liveDatabaseImageList = appDB.noteImageDao().getLiveImagesByNoteId((int)noteId);
        }
        return liveDatabaseImageList;
    }

    public MutableLiveData<ArrayList<String>> getLiveImageUriList() {
        if (imageUriLiveList == null) {
            imageUriLiveList = new MutableLiveData<>();
        }
        upDateLists();
        imageUriLiveList.setValue(databaseStrings);
        return imageUriLiveList;
    }

}
