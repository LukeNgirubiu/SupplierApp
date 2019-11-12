package com.luke.supplierapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.google.firebase.firestore.FirebaseFirestore;

import io.grpc.Context;

public class locations extends Service {
    private LocationListener locationListener;
    private LocationManager manager;
    public static final String CHANNEL_ID = "SJTE19";
    public static final String CHANNEL_NAME = "Suppliers";
    public static final String CHANNEL_DESCRIPTION = "Supplier describe";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1222, notifying());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent locations = new Intent("Locations");
                locations.putExtra("Longi",location.getLongitude());
                locations.putExtra("Latit",location.getLatitude());
                sendBroadcast(locations);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        manager = (LocationManager) getApplicationContext().getSystemService(android.content.Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0, locationListener);

    }

//enable a service run forever in android version 8.0 and above
   private Notification notifying() {
        if (Build.VERSION.SDK_INT > 25) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manage;
            manage = getSystemService(NotificationManager.class);
            channel.setDescription(CHANNEL_DESCRIPTION);
            manage.createNotificationChannel(channel);
            Notification.Builder build = new Notification.Builder(getApplicationContext(), CHANNEL_NAME);
            return build.build();
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
