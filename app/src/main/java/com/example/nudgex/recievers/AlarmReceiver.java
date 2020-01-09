package com.example.nudgex.recievers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nudgex.R;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    String TAG = "AlarmReceiver";
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification Channel";
    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";
    int idCount = 0;
    private NotificationManagerCompat notificationManagerCompat ;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManagerCompat =  NotificationManagerCompat.from(context);

        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
        System.out.println("inside reciever of alarm");

        long timeInMillis = Long.parseLong(intent.getExtras().get("TIME_TO_SET_ALARM").toString());

            System.out.println("show when:" + timeInMillis);
            String title = intent.getExtras().get("TASK_TITLE").toString();
            Notification notification = new NotificationCompat.Builder(context, AppNotification.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_nudgex_launcher_round).setContentTitle("To Do!!")
                    .setContentText(title).setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setShowWhen(true).setWhen(timeInMillis)
                    .setCategory(NotificationCompat.CATEGORY_EVENT).build();

            idCount += 1;
            notificationManagerCompat.notify(idCount, notification);




    }




}
