package com.htwberlin.geolist.notification.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.LocationRepositoryImpl;
import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.location.interfaces.LocationFacadeFactory;
import com.htwberlin.geolist.location.interfaces.LocationFacadeImpl;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.notification.helper.NotificationSenderImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
    public static boolean serviceIsRunning = false;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!serviceIsRunning) {
            serviceIsRunning = true;
            startMyOwnForeground();
        }
        super.onStartCommand(intent, flags, startId);

        NotificationSenderImpl sender = new NotificationSenderImpl();

        Runnable notifyRunner = () -> {
            LocationFacadeImpl locationInterface = LocationFacadeFactory.getInstance(getApplicationContext());
            LocationRepositoryImpl repo = (LocationRepositoryImpl) GeoListLogic.getStorage().getLocationRepo();
            ArrayList<MarkerLocation> locations = locationInterface.getLocationsInRadius(500);
            for (MarkerLocation loc : locations) {
                if (loc.getLastNotification() == null || isAtLeast30MinutesAgo(loc.getLastNotification())) {
                    loc.setLastNotification(new Date());
                    repo.saveLocation(loc);
                    sender.sendNotification("Sie haben Aufgaben in der Nähe!", getApplicationContext());
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(notifyRunner, 0, 30, TimeUnit.SECONDS);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceIsRunning = false;
    }

    private boolean isAtLeast30MinutesAgo(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        Instant twentyMinutesAgo = Instant.now().minus(Duration.ofMinutes(30));

        return instant.isBefore(twentyMinutesAgo);
    }


    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "ForegroundService";
        String channelName = "GeoList Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("GeoList stellt sicher, dass du nichts verpasst!")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}