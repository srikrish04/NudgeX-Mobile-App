package com.example.nudgex.board.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nudgex.board.entities.Board;

import java.util.List;

@Dao
public interface BoardDao {
    @Query("SELECT * FROM boards ORDER BY boardId")
    LiveData<List<Board>> getBoards();

    @Query("SELECT * FROM boards WHERE boardId = :boardId")
    Board getBoardById(int boardId);

//    @Query("UPDATE boards SET name=:boardName, description=:boardDescription WHERE boardId = :boardId")
//    void updateBoardById(int boardId, String boardName, String boardDescription);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBoard(Board board);

    @Delete
    void deleteBoard(Board board);

    @Update
    void updateBoard(Board board);

    @Query("DELETE from boards")
    void deleteAll();
}
