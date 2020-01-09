package com.example.nudgex;

import java.util.Date;

public class Task implements  Comparable{
    String taskId;
    String newTask;
    String taskDate;
    String taskTime;
    String taskDescription;
    Double latitude;
    Double longitude;
    String locationName;
    public Task(){

    }

    public Task(String taskId, String newTask, String taskDate, String taskTime, String taskDescription, Double latitude, Double longitude, String locationName) {
        this.taskId = taskId;
        this.newTask = newTask;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.taskDescription = taskDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }




    public String getTaskId() {
        return taskId;
    }

    public String getNewTask() {
        return newTask;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    @Override
    public int compareTo(Object o) {
        String compareDate=((Task)o).getTaskDate();
        /* For Ascending order*/
        Date checkDate = new Date(compareDate);
        Date taskDate = new Date(this.taskDate);
        return taskDate.before(checkDate)?1:0;
    }
}
