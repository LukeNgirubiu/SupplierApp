package com.luke.supplierapp;

import android.content.Intent;
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
    FirebaseAuth auth;
    private FloatingActionButton product;
    CollectionReference reference;
    private List<productSet> products;
    private RecyclerView recyclerView;
    private productSellerRecy prodc;
    private String userId;
    private FloatingActionButton action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_seller_activity);
        toolbar = findViewById(R.id.toolbar);
        product = findViewById(R.id.productitem);
        action=findViewById(R.id.productitem);
        auth=FirebaseAuth.getInstance();
        userId=auth.getUid();
        refering=firestore.collection("Particulars").document(userId);
        refering.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    userDetails user=documentSnapshot.toObject(userDetails.class);
                    if(user.getType()==2){
                        action.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });

        firestore = FirebaseFirestore.getInstance();
        sqlite sqlite = new sqlite(this);
        reference = firestore.collection("Products").document(sqlite.getUser()).collection("products");
        products = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        whereNoCategory();
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

        recyclerView.setHasFixedSize(true);
    }


}
