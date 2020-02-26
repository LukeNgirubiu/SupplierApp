package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Homes extends AppCompatActivity {
    RecyclerView recyclerView;
    Toolbar toolbar;
    private String fname, sname, srname, imageString, contacts, userdId;
    private FirebaseFirestore firestore;
    private DocumentReference reference;
    private BroadcastReceiver broadcastReceiver;
    private Double Latitude, Longitude, Altitude;
    private suppliersDashRecy dashboard;
    private List<prepareC> list;
    private Date date;
    private int type;
    private DocumentReference businesName;


    @Override
    protected void onResume() {
        super.onResume();
        list = new ArrayList<>();
        list.add(new prepareC("Products"));

        list.add(new prepareC("Subscriptions"));
        list.add(new prepareC("Orders"));
        dashboard = new suppliersDashRecy(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dashboard);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startorder = new Intent(Homes.this, OrderNotification.class);
        startService(startorder);
        setContentView(R.layout.activity_homes);
        sqlite sqlite = new sqlite(this);
        userdId = sqlite.getUser();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Supplier");
        //Products,Enquiries,Subscriptions,Order
        firestore = FirebaseFirestore.getInstance();
        businesName = firestore.collection("BusinessName").document(userdId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suppliersoptions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemIndex = item.getItemId();
        switch (itemIndex) {
            case R.id.user:
                businesName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(getBaseContext(), "Business name already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intents = new Intent(getBaseContext(), showSuppliers.class);
                            intents.putExtra("businessName", "");
                            intents.putExtra("slogan", "");
                            startActivity(intents);
                        }
                    }
                });

                break;
            case R.id.changebusiness:
                businesName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            businessNames names = documentSnapshot.toObject(businessNames.class);
                            Intent intents = new Intent(getBaseContext(), showSuppliers.class);
                            intents.putExtra("businessName", names.getBusinessName());
                            intents.putExtra("slogan", names.getSlogan());
                            startActivity(intents);

                        } else {
                            Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
            case R.id.contact:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.addingcontact, null);
                final EditText contact = view.findViewById(R.id.contact);
                Button send = view.findViewById(R.id.addcontact);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CollectionReference collect = firestore.collection("Cellphone");
                        collect.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.size() > 2) {
                                    Toast.makeText(getBaseContext(), "Not possible to add a contact", Toast.LENGTH_SHORT).show();
                                } else {
                                    sqlite sdl = new sqlite(getBaseContext());
                                    contacts cont = new contacts();
                                    cont.setUserId(sdl.getUser());
                                    cont.setContact(contact.getText().toString());
                                    collect.add(cont);
                                }
                            }
                        });
                    }
                });
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.totesta:
                Intent intents=new Intent(getApplicationContext(),location2.class);
                startService(intents);
                Intent intenttest=new Intent(getApplicationContext(),Testlocation.class);
                startActivity(intenttest);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

}
/* */