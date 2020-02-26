package com.luke.supplierapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
    String userd;
    private RecyclerView recyclerView;
    private productSetRecycler prodc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_show);
        toolbar = findViewById(R.id.toolbar);
        cart = findViewById(R.id.cartitems);
        firestore = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        userd = bundle.getString("Id");
        String category = bundle.getString("Category");
        reference = firestore.collection("Products").document( userd).collection("products");
        products = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlite sql=new sqlite(getBaseContext());
                CollectionReference refer2 = firestore.collection("Cart").
                        document(sql.getUser()).collection(userd);
                refer2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            Intent intent = new Intent(productsShow.this, cartProducts.class);
                            intent.putExtra("Id", userd);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getBaseContext(),"No products in the cart",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        if (category == null) {
            whereNoCategory(userd);
        } else {
            whereCategory(category,userd);
        }

    }

    private void whereNoCategory(final String userid) {
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

    private void whereCategory(String id, final String user) {
        reference.whereEqualTo("categoryId", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                products.clear();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    productSet product = query.toObject(productSet.class);
                    product.setProductId(query.getId());
                    products.add(product);
                }
                prodc = new productSetRecycler(products, getBaseContext(),user);
                recyclerView.setAdapter(prodc);
            }
        });

        recyclerView.setHasFixedSize(true);
    }

}
