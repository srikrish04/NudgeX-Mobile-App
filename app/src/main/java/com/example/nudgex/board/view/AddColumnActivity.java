package com.example.nudgex.board.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nudgex.R;
import com.example.nudgex.board.entities.Board;
import com.example.nudgex.board.entities.Column;
import com.example.nudgex.database.AppDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddColumnActivity extends AppCompatActivity {
    int boardId,columnId;
    Column col;
    AppDatabase appDB;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_column);
        appDB = AppDatabase.getOfflineDatabase(getApplicationContext());
        Intent intent = getIntent();
        columnId = intent.getIntExtra("columnId", -1);
        boardId = intent.getIntExtra("boardId", -1);
        MaterialButton btnCreateBoard = findViewById(R.id.btnCreateBoard);
        final TextInputEditText boardName = findViewById(R.id.newBoard);
        final TextInputEditText boardDescription = findViewById(R.id.boardDescription);
        if (columnId != -1){
            btnCreateBoard.setText("Update Column");
            col = appDB.columnDao().getColumnById(columnId);
            boardName.setText(col.name);
            boardDescription.setText(col.description);
        }
        btnCreateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = boardName.getText().toString();
                String description = boardDescription.getText().toString();
                Column column = new Column();
                column.name = name;
                column.description = description;
                column.boardId = boardId;
                int columnSort = appDB.columnDao().getColumnByBoardId(boardId).size();
                if (columnId != -1) {
                    column.columnId = columnId;
                    appDB.columnDao().updateColumn(column);
                }else{
                    column.columnSort = columnSort;
                    appDB.columnDao().insertColumn(column);
                }
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (col != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.board_view_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.deleteBoard:
                deleteColumn();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void deleteColumn(){
        appDB.columnDao().deleteColumn(col);
        finish();
    }
}
