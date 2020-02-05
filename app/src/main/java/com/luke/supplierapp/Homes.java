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
    private String fname, sname, srname, imageString, contacts;
    private FirebaseFirestore firestore;
    private DocumentReference reference;
    private BroadcastReceiver broadcastReceiver;
    private Double longitude, latitude;
    private suppliersDashRecy dashboard;
    private List<prepareC> list;
    private Date date;
    private int type;
    private DocumentReference businesName;

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
        setContentView(R.layout.activity_homes);
        sqlite sqlite = new sqlite(this);
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Supplier");
        //Products,Enquiries,Subscriptions,Orders

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("Particulars").document(sqlite.getUser());
        businesName = firestore.collection("BusinessName").document(sqlite.getUser());
        userDetails details = new userDetails();
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails users = documentSnapshot.toObject(userDetails.class);
                fname = users.getFirstName();
                sname = users.getSecondName();
                srname = users.getSurName();
                contacts = users.getContact();
                imageString = users.getImagePath();
                type = users.getType();
                date = users.getDate();
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
        list = new ArrayList<>();
        list.add(new prepareC("Products"));
        list.add(new prepareC("Enquiries"));
        list.add(new prepareC("Subscriptions"));
        list.add(new prepareC("Orders"));
        dashboard = new suppliersDashRecy(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dashboard);
        recyclerView.setHasFixedSize(true);

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