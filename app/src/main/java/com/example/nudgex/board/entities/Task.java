package com.example.nudgex.board.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "board_task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int taskId;
//    public int boardId;
    public int columnId;
    public int rowNumber;
    public String name;
    public String description;


}
