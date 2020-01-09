package com.example.nudgex;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class CustomComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        String[] stDt = ((Task)o1).getTaskTime().split(":");
        String[] o2stDt = ((Task)o2).getTaskTime().split(":");


        return Integer.parseInt(stDt[0]) - Integer.parseInt(o2stDt[0]);


    }
}
