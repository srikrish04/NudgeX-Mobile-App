package com.example.nudgex.board.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "boards")
public class Board {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "boardId")
    public int boardId = 0;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="description")
    public String description;

}
