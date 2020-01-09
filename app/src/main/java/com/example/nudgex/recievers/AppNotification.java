package com.example.nudgex.recievers;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppNotification extends Application {
    public static final String CHANNEL_ID = "nudge1";
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "nudge",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription("Check nudge notification");
            System.out.println("inside reciever of createNotificationChannel");

            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
