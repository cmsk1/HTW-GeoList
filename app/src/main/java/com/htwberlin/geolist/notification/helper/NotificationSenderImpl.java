package com.htwberlin.geolist.notification.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.gui.activity.MainActivity;

import java.util.Date;

public class NotificationSenderImpl implements NotificationSender {
    public void sendNotification(String text, Context context) {

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "95")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(text)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Klicken Sie hier um die Aufgaben anzuzeigen"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        CharSequence name = "Notification_Channel";
        String description = "Notification_for_GeoList";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(String.valueOf(95), name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);



        notificationManager.notify((int) new Date().getTime(), builder.build());
    }
}
