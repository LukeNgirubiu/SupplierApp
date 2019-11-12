package com.luke.supplierapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
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
    private FloatingActionButton product;
    CollectionReference reference;
    private List<productSet> products;
    private RecyclerView recyclerView;
    private productSellerRecy prodc;
    private FloatingActionButton action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_seller_activity);
        toolbar = findViewById(R.id.toolbar);
        product = findViewById(R.id.productitem);
        action=findViewById(R.id.productitem);
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
                products.clear();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    productSet product = query.toObject(productSet.class);
                    product.setProductId(query.getId());
                    products.add(product);
                }
                prodc = new productSellerRecy(products, getBaseContext());
                recyclerView.setAdapter(prodc);
            }
        });

        recyclerView.setHasFixedSize(true);
    }


}
