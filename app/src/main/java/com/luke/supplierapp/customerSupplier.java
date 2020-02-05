
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class customerSupplier extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private Toolbar toolbar;
    private int determint;
    private String categoryId;
    private BroadcastReceiver  broadcastReceiver;
    private Double myLongitude, myLatitude;
    private List<userDetails> suppliers;
    showSuppRecycler show;
    private CollectionReference reference;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver==null){
            broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                   // myLongitude=intent.getDoubleExtra("Longi",34.57849306574839);
                   // myLongitude=                myLatitude=intent.getDoubleExtra("Latit",1.546372819789788);
                }
            };
            registerReceiver(broadcastReceiver,new IntentFilter("Locations"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_supplier);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firestore=FirebaseFirestore.getInstance();
        getSupportActionBar().setTitle("Suppliers");
         reference = firestore.collection("Particulars");
        Bundle bundels = getIntent().getExtras();
        determint=bundels.getInt("Determiner");
        categoryId=bundels.getString("categoryId");
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        suppliers = new ArrayList<>();
        myLongitude=34.57849306574839;
        myLatitude=1.546372819789788;
        if(determint==1){
    getSuppliersByCoord();
}
if(determint==2){
    getBasedOnCategory(categoryId);
}
    }

    private void getSuppliersByCoord() {
        Double firstlat,secondlat,firstlong,secondlong;
        firstlat=myLatitude-0.45454546;
        secondlat=myLatitude+0.45454546;
        firstlong=myLongitude-0.45454546;
        secondlong=myLongitude+0.45454546;
        reference.whereGreaterThan("latitude", firstlat).whereLessThan("latitude", secondlat)
                .whereGreaterThan("longitude", firstlong).whereLessThan("longitude", secondlong).whereEqualTo("type",1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                suppliers.clear();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    userDetails details = query.toObject(userDetails.class);
                    details.setId(query.getId());
                    suppliers.add(details);
                }
                show= new showSuppRecycler(suppliers, getBaseContext(),null);
                recyclerView.setAdapter(show);
            }
        });
        recyclerView.setHasFixedSize(true);
    }
    private void getBasedOnCategory(final String id){
        Double firstlat,secondlat,firstlong,secondlong;
        firstlat=myLatitude-0.45454546;
        secondlat=myLatitude+0.45454546;
        firstlong=myLongitude-0.45454546;
        secondlong=myLongitude+0.45454546;
        CollectionReference collectionReference=firestore.collection("Category").document(id).collection("category");
        collectionReference.whereGreaterThan("latitude",firstlat)
                .whereLessThan("latitude",secondlat);
        collectionReference.whereGreaterThan(" longitude",firstlong).
                whereLessThan(" longitude",secondlong).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot query:queryDocumentSnapshots){
                    categorySet categ=query.toObject(categorySet.class);
                    DocumentReference documentReference=reference.document(categ.getSupplierId());
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                           userDetails user=documentSnapshot.toObject(userDetails.class);
                           user.setId(documentSnapshot.getId());
                           suppliers.add(user);
                        }
                    });
                }
                show = new showSuppRecycler(suppliers, getBaseContext(),id);
                recyclerView.setAdapter(show);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}
