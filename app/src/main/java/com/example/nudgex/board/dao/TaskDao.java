package com.example.nudgex.board.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.example.nudgex.board.entities.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM board_task")
    LiveData<List<Task>> getTasks();

    @Query("SELECT * FROM board_task WHERE columnId = :columnId ORDER BY rowNumber")
    LiveData<List<Task>> getLiveTasksByColumnId(int columnId);

    @Query("SELECT * FROM board_task WHERE columnId = :columnId ORDER BY rowNumber")
    List<Task> getTasksByColumnId(int columnId);

    @Query("SELECT * FROM board_task WHERE taskId = :taskId")
    Task getTaskById(int taskId);

    @Query("SELECT * FROM board_task WHERE columnId = :columnId AND rowNumber = :rowNumber")
    Task getTaskByColumnIdAndRowNumber(int columnId,int rowNumber);

    @Query("UPDATE board_task SET rowNumber = rowNumber + 1 WHERE columnId = :columnId AND rowNumber >= :rowNumber")
    void increaseTaskRowOnMove(int columnId, int rowNumber);

    @Query("UPDATE board_task SET rowNumber = rowNumber - 1 WHERE columnId = :columnId AND rowNumber > :rowNumber")
    void decreaseTaskRowOnMove(int columnId, int rowNumber);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Update
    void updateTask(Task task);

    @Query("DELETE from board_task")
    void deleteAll();
}
