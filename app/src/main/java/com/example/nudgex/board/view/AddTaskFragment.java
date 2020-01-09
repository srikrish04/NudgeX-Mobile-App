package com.example.nudgex.board.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.nudgex.R;
import com.example.nudgex.board.entities.Task;
import com.example.nudgex.database.AppDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddTaskFragment extends Fragment {
    AppDatabase appDB;
    int taskCount;
    public AddTaskFragment(){

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.add_board_task_activity);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.add_board_task_activity, container, false);
        appDB = AppDatabase.getOfflineDatabase(getActivity());

        TextInputEditText edtTxtTaskName= view.findViewById(R.id.taskName);
        TextInputEditText edtTxtTaskDesctiption= view.findViewById(R.id.taskDescription);
        MaterialButton btnAddTask = view.findViewById(R.id.btnAddTask);

        taskCount = 0;

        int columnId = getArguments().getInt("columnId", -1);
        int taskId = getArguments().getInt("taskId", -1);
        if (taskId != -1){
            btnAddTask.setText("Update Task");
            Task old_task = appDB.taskDao().getTaskById(taskId);
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
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        return view;
    }
}
