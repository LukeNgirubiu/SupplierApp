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
import android.provider.Settings;
import android.content.Context;



public class locations extends Service {
    private LocationListener locationListener;
    private LocationManager locationManager;
    public static final String CHANNEL_ID = "SJTE19";
    public static final String CHANNEL_NAME = "Suppliers";
    public static final String CHANNEL_DESCRIPTION = "Supplier describe";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
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
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        locationManager=(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,300,0,locationListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
    }


}
