package com.example.nudgex.board.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "board_column")
public class Column {
    @PrimaryKey(autoGenerate = true)
    public int columnId;
    public int boardId;
    public String name;
    public String description;
    public int columnSort;
}
