package com.luke.supplierapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;


public class locations extends JobIntentService {
    private LocationListener locationListener;
    private LocationManager locationManager;
    Double h,w;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

Log.e("first","Jobintent service created");
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent locations = new Intent("Locations");
                locations.putExtra("Longi", location.getLongitude());
                locations.putExtra("Latit", location.getLatitude());
                locations.putExtra("Altitude", location.getAltitude());
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
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
       locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30, 0, locationListener);
        Log.e("first","Onjob handled");
      }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
