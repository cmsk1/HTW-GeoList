package com.htwberlin.geolist.location.services;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.location.interfaces.LocationFacadeFactory;
import com.htwberlin.geolist.location.interfaces.LocationFacadeImpl;

public class LocationService extends Service {

    public static boolean serviceIsRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationFacadeImpl locationInterface = LocationFacadeFactory.getInstance(getApplicationContext());
        if (!serviceIsRunning) {
            serviceIsRunning = true;
            startMyOwnForeground();
        }

        LocationListener locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationInterface.setCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10, locationListenerGPS, Looper.getMainLooper());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceIsRunning = false;
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
