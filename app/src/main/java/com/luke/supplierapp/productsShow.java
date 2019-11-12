package com.luke.supplierapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class productsShow extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseFirestore firestore;
    private FloatingActionButton cart;
    CollectionReference reference;
    private List<productSet> products;
    private RecyclerView recyclerView;
    private productSetRecycler prodc;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_show);
        toolbar = findViewById(R.id.toolbar);
        cart = findViewById(R.id.cartitems);
        firestore = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
         userid = bundle.getString("id");
        String category = bundle.getString("Category");
        reference = firestore.collection("Products").document( userid).collection("products");
        products = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(productsShow.this, cartProducts.class);
                intent.putExtra("id", userid);
                startActivity(intent);
            }
        });
        if (category == null) {
            whereNoCategory();
        } else {
            whereCategory(category);
        }

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
                prodc= new productSetRecycler(products, getBaseContext(),userid);
                recyclerView.setAdapter(prodc);
            }
        });

        recyclerView.setHasFixedSize(true);
    }

    private void whereCategory(String id) {
        reference.whereEqualTo("categoryId", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                products.clear();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    productSet product = query.toObject(productSet.class);
                    products.add(product);
                }
                prodc = new productSetRecycler(products, getBaseContext(),userid);
                recyclerView.setAdapter(prodc);
            }
        });

        recyclerView.setHasFixedSize(true);
    }

}
