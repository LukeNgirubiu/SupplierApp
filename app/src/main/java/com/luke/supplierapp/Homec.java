package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Homec extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<prepareC> list;
    private FirebaseFirestore firestore;
    private Double longitude, latitude;
    private BroadcastReceiver broadcastReceiver;
    private RecycForC recycForC;
    private DocumentReference reference;
    private String fname, sname, srname, imageString, contacts;
    private int type;
    private Date date;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    longitude = intent.getDoubleExtra("Longi", 0);
                    latitude = intent.getDoubleExtra("Latit", 0);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("Locations"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homec);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        setSupportActionBar(toolbar);
        list = new ArrayList<>();
        getSupportActionBar().setTitle("Home");
        sqlite sdl = new sqlite(this);
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("usersDetails").document(sdl.getUser());
        userDetails details = new userDetails();
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails users = documentSnapshot.toObject(userDetails.class);
                fname = users.getFirstName();
                sname = users.getSecondName();
                srname=users.getSurName();
                contacts=users.getContact();
                imageString=users.getImagePath();
                type=users.getType();
                date=users.getDate();
            }
        });
        details.setLongitude(longitude);
        details.setLatitude(latitude);
        details.setSurName(srname);
        details.setSecondName(sname);
        details.setContact(contacts);
        details.setFirstName(fname);
        details.setImagePath(imageString);
        details.setType(type);
        details.setDate(date);
        reference.set(details);
        list.add(new prepareC("Suppliers"));
        list.add(new prepareC("Categories"));
        list.add(new prepareC("My orders"));
        list.add(new prepareC("Enquiries"));
        recycForC = new RecycForC(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recycForC);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
