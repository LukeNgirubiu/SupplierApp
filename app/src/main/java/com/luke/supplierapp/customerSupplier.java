
package com.luke.supplierapp;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

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
    private Button button;
    private String categoryId;
    private DocumentReference user;
    private BroadcastReceiver broadcastReceiver;
private Double myLongitude,myLatitude;
    private List<userDetails> suppliers;
    showSuppRecycler show;
    private CollectionReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_supplier);
        button=findViewById(R.id.press);
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("Users");
        sqlite sq=new sqlite(this);
        user=reference.document(sq.getUser());
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Suppliers");
        Bundle bundels = getIntent().getExtras();
        determint = bundels.getInt("Determiner");
        categoryId = bundels.getString("categoryId");
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        suppliers = new ArrayList<>();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplication().getBaseContext());
                builder.setTitle("Delete the Order");
                builder.setIcon(R.drawable.addproduct);
                builder.setMessage("Continue with deleting the order?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails users = documentSnapshot.toObject(userDetails.class);
                myLongitude=users.getLongitude();
                myLatitude =users.getLatitude();
                if (determint == 1) {
                    getSuppliersByCoord(myLatitude,myLongitude);
                }
                if (determint == 2) {
                    getBasedOnCategory(categoryId,myLatitude,myLongitude);
                }



            }
        });
    }
    private void getSuppliersByCoord(final Double Latitude, final Double Longitude) {
     reference.whereEqualTo("type",1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                suppliers.clear();
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                        userDetails details = query.toObject(userDetails.class);
                        Double lat2, long2;
                        lat2 = details.getLatitude();
                        long2 = details.getLongitude();
                        Double latd = Math.toRadians(lat2 - Latitude);//myLatitude
                        Double longd = Math.toRadians(long2 -Longitude );//myLongitude
                        Double a = Math.sin(latd / 2) * Math.sin(latd / 2) + Math.cos(Math.toRadians(0.020523)) * Math.cos(Math.toRadians(lat2))
                                * Math.sin(longd / 2) * Math.sin(longd / 2);
                        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                        Double distance;
                        distance = 6371 * c * 1000;
                        Double mostd = Math.sqrt(Math.pow(distance, 2) + Math.pow((0), 2));
                        if(mostd/1000<35){
                        details.setId(query.getId());
                        suppliers.add(details);
                    }}
                }
                show = new showSuppRecycler(suppliers, getBaseContext(), null);
                recyclerView.setAdapter(show);
            }
        });
        recyclerView.setHasFixedSize(true);
    }

    private void getBasedOnCategory(final String id, final Double Latitude, final Double Longitude) {

        FirebaseFirestore firestor=FirebaseFirestore.getInstance();
       CollectionReference collectionReference = firestor.collection("Category").document(id).collection("category");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                suppliers.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    categorySet categ = query.toObject(categorySet.class);
                    Double lat2, long2;
                    lat2 = categ.getLatitude();
                    long2 = categ.getLongitude();
                    Double latd = Math.toRadians(lat2 - Latitude);
                    Double longd = Math.toRadians(long2 - Longitude);
                    Double a = Math.sin(latd / 2) * Math.sin(latd / 2) + Math.cos(Math.toRadians(myLatitude)) * Math.cos(Math.toRadians(lat2))
                            * Math.sin(longd / 2) * Math.sin(longd / 2);
                    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    Double distance;
                    distance = 6371 * c * 1000;
                    Double mostd = Math.sqrt(Math.pow(distance, 2) + Math.pow((0), 2));
                    if (mostd/1000<35) {

                        DocumentReference documentReference = firestore.collection("Users").document(query.getId());
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                userDetails user = documentSnapshot.toObject(userDetails.class);
                                user.setId(documentSnapshot.getId());
                                suppliers.add(user);
                                show = new showSuppRecycler(suppliers, getBaseContext(), id);
                                recyclerView.setAdapter(show);
                            }

                        });                    }

                }
                 }
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
