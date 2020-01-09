package com.example.nudgex.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nudgex.NewReminderActivity;
import com.example.nudgex.R;
import com.example.nudgex.Task;

import java.util.ArrayList;

public class CardViewRecyclerViewLocationAdapter  extends RecyclerView.Adapter<CardViewRecyclerViewLocationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Object> tasks = new ArrayList<>();

    public CardViewRecyclerViewLocationAdapter(Context context, ArrayList<Object> tasks) {
        this.context = context;
        this.tasks = tasks;
    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_location, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(tasks.get(position));

        Task task =(Task) tasks.get(position);

        holder.taskTime.setText(task.getTaskTime());
        holder.taskTitle.setText(task.getNewTask());
        holder.taskDate.setText(task.getTaskDate());
    }

    @Override
    public int getItemCount() {
        return tasks.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView taskTime;
        public TextView taskTitle;
        public TextView taskDate;

        public ViewHolder(View itemView) {
            super(itemView);

            taskDate = (TextView) itemView.findViewById(R.id.taskDate) ;
            taskTime = (TextView) itemView.findViewById(R.id.taskTime);
            taskTitle = (TextView) itemView.findViewById(R.id.taskTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task task = (Task) view.getTag();

                    Toast.makeText(context, task.getNewTask(), Toast.LENGTH_SHORT).show();
                    String invoke_from = "CardViewAdapter";

                    Bundle extras = new Bundle();
                    extras.putString("INVOKE_FROM", invoke_from);
                    extras.putString("TASK_TITLE", task.getNewTask());

                    extras.putString("TASK_DATE", task.getTaskDate());
                    extras.putString("TASK_TIME", task.getTaskTime());
                    extras.putString("TASK_DESC", task.getTaskDescription());
                    extras.putString("TASK_ID", task.getTaskId());
                    extras.putString("TASK_LOCATION", task.getLocationName());
                    extras.putDouble("TASK_LATITUDE", task.getLatitude());
                    extras.putDouble("TASK_LONGITUDE", task.getLongitude());

                    Intent intent = new Intent(context, NewReminderActivity.class);
                    intent.putExtras(extras);
                    //starting the activity
                    context.startActivity(intent);
                }
            });

        }
    }

    public void clear(){
        tasks.clear();
    }

}
