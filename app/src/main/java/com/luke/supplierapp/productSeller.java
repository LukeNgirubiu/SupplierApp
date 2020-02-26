package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class productSeller extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseFirestore firestore;
    private DocumentReference refering;
    private FloatingActionButton product;
    private CollectionReference reference;
    private Double Longitude,Latitude;
    private List<productSet> products;
    private RecyclerView recyclerView;
    private productSellerRecy prodc;
    private BroadcastReceiver broadcastReceiver;
    private String userId;
    int type;
    private FloatingActionButton action;
    private sqlite sqlite;
    @Override
    protected void onResume() {
        super.onResume();
        whereNoCategory();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_seller_activity);
        toolbar = findViewById(R.id.toolbar);
        product = findViewById(R.id.productitem);
        action=findViewById(R.id.productitem);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        sqlite = new sqlite(this);
        userId=sqlite.getUser();
        firestore = FirebaseFirestore.getInstance();
        Intent intent=new Intent(getApplicationContext(),location2.class);
        startService(intent);
       refering=firestore.collection("Users").document(userId);
        refering.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails users = documentSnapshot.toObject(userDetails.class);
                type=users.getType();
                if(type==2){
                    action.setVisibility(View.VISIBLE);
                }
            }
        });

        reference = firestore.collection("Products").document(userId).collection("products");
        products = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),category.class);
                intent.putExtra("dec",2);
                startActivity(intent);
            }
        });
    }



    private void whereNoCategory() {
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                products.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    productSet product = query.toObject(productSet.class);
                    product.setProductId(query.getId());
                    products.add(product);
                }
                prodc = new productSellerRecy(products, getBaseContext());
                recyclerView.setAdapter(prodc);
            }else {
                    Toast.makeText(getBaseContext(),"No product",Toast.LENGTH_SHORT).show();
                }}
        });


    }


}
