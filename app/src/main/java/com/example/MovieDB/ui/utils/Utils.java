package com.example.MovieDB.ui.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.example.MovieDB.ui.activity.NowPlaying_OnTheAir;

public class Utils {

    private static final String NEW_MOVIE_CHANNEL_ID = "new_movie_id";
    private static final String NEW_MOVIE_CHANNEL_NAME = "Movie Notification";
    private static final int NEW_MOVIE_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
    private static final int NOTIFICATION_ID = 14983;

    public static void pushNotification(Context context, String title, String message, int icon, int largeIcon) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NEW_MOVIE_CHANNEL_ID,
                    NEW_MOVIE_CHANNEL_NAME,
                    NEW_MOVIE_CHANNEL_IMPORTANCE);
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(context, NEW_MOVIE_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        Notification notification = builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                .setStyle(new Notification.BigTextStyle()
                        .bigText(message)
                ).setAutoCancel(true)
                .build();
        Intent i = new Intent(context, NowPlaying_OnTheAir.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 123, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        manager.notify(NOTIFICATION_ID, notification);
    }
}
