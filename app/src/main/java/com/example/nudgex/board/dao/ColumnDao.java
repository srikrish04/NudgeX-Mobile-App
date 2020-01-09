package com.example.nudgex.board.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.example.nudgex.board.entities.Column;


import java.util.List;

@Dao
public interface ColumnDao {
    @Query("SELECT * FROM board_column")
    LiveData<List<Column>> getColumns();

    @Query("SELECT * FROM board_column WHERE boardId = :boardId ORDER BY columnId DESC")
    LiveData<List<Column>> getLiveColumnByBoardId(int boardId);

    @Query("SELECT * FROM board_column WHERE boardId = :boardId ORDER BY columnSort")
    List<Column> getColumnByBoardId(int boardId);

    @Query("SELECT * FROM board_column WHERE columnId = :columnId")
    Column getColumnById(int columnId);

    @Query("SELECT * FROM board_column WHERE boardId = :boardId AND columnSort = :columnSort")
    Column getColumnByBoardIdAndSort(int boardId, int columnSort);

    @Query("UPDATE board_column SET columnSort = columnSort + 1 WHERE boardId = :boardId AND " +
            "(columnSort >= :lowColumnSort AND columnSort < :highColumnSort )")
    void increaseColumnSort(int boardId, int lowColumnSort, int highColumnSort);

    @Query("UPDATE board_column SET columnSort = columnSort - 1 WHERE boardId = :boardId AND" +
            " (columnSort > :lowColumnSort AND columnSort <= :highColumnSort) ")
    void decreaseColumnSort(int boardId, int lowColumnSort, int highColumnSort);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertColumn(Column column);

    @Delete
    void deleteColumn(Column column);

    @Update
    void updateColumn(Column column);

    @Query("DELETE from board_column")
    void deleteAll();
}
