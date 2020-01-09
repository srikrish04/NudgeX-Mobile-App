package com.example.nudgex.board.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.nudgex.R;
import com.example.nudgex.board.entities.Task;
import com.example.nudgex.database.AppDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddTaskActivity extends AppCompatActivity {
    AppDatabase appDB;
    int taskCount;
    Task old_task;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_board_task_activity);

        appDB = AppDatabase.getOfflineDatabase(this);

        TextInputEditText edtTxtTaskName= findViewById(R.id.taskName);
        TextInputEditText edtTxtTaskDesctiption= findViewById(R.id.taskDescription);
        MaterialButton btnAddTask = findViewById(R.id.btnAddTask);

        taskCount = 0;
        Intent intent = getIntent();
        int columnId = intent.getIntExtra("columnId", -1);
        int taskId = intent.getIntExtra("taskId", -1);
        if (taskId != -1){
            btnAddTask.setText("Update Task");
            old_task = appDB.taskDao().getTaskById(taskId);
            edtTxtTaskName.setText(old_task.name);
            edtTxtTaskDesctiption.setText(old_task.description);
        }

        appDB.taskDao().getLiveTasksByColumnId(columnId).observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskCount = tasks.size();
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtTxtTaskName.getText().toString();
                String description = edtTxtTaskDesctiption.getText().toString();
                Task task = new Task();
                task.columnId = columnId;
                task.description = description;
                task.name = name;
                if(taskId == -1){
                    task.rowNumber = taskCount;
                    appDB.taskDao().insertTask(task);
                }else{
                    task.rowNumber = appDB.taskDao().getTaskById(taskId).rowNumber;
                    task.taskId = taskId;
                    appDB.taskDao().updateTask(task);
                }
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (old_task != null) {
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
                deleteTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void deleteTask(){
        appDB.taskDao().deleteTask(old_task);
        finish();
    }
}
