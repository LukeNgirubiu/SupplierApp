package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Testlocation extends AppCompatActivity {
    private TextView editText1, editText2,textView1,textView2;
    private BroadcastReceiver broadcastReceiver,broadcastReceiver2;
    private Double Longitude, Latitude, Longi, Latitu, Altitude;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Longitude = intent.getDoubleExtra("Longitude", 0);
                    Latitude = intent.getDoubleExtra("Latitude", 0);
                    Altitude = intent.getDoubleExtra("Altitude", 0);
                    editText1.append("Intent service  "+Double.toString(Longitude)+"\n");
                    editText2.append("Intent service  "+Double.toString(Latitude)+"\n");
                }
            };
            /*broadcastReceiver2=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Longi = intent.getDoubleExtra("Longitude", 0);
                    Latitu= intent.getDoubleExtra("Latitude", 0);
                    Altitude = intent.getDoubleExtra("Altitude", 0);
                    textView1.append("Job Intent service  "+Double.toString(Longi)+"\n");
                    textView2.append("Job Intent service  "+Double.toString(Latitu)+"\n");
                }
            };*/
        }
        //registerReceiver(broadcastReceiver2,new IntentFilter("Locations"));
        registerReceiver(broadcastReceiver, new IntentFilter("Coordinates"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testlocation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText1 = findViewById(R.id.latitude);
        editText2 = findViewById(R.id.longitude);
        textView1=findViewById(R.id.longitude1);
        textView2=findViewById(R.id.latitude1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
