package com.luke.supplierapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Products extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private List<cartProductSet> list;
    private RecyclerView recyclerView;
    private cartProductsRecy products;
    private String id,userId;
    private Toolbar toolbar;
    private TextView order;
    private FloatingActionButton floater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        firestore = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("orderId");
        userId=bundle.getString("userId");
        toolbar = findViewById(R.id.toolbar);
        order=findViewById(R.id.order);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ordered products");
        toolbar.setTitleTextColor(Color.WHITE);
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        floater = findViewById(R.id.respond);
        LinearLayoutManager manage = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manage);
        DividerItemDecoration divide = new DividerItemDecoration(this, manage.getOrientation());
        recyclerView.addItemDecoration(divide);
        sqlite sql = new sqlite(getApplicationContext());
        DocumentReference gettype = firestore.collection("Users").document(sql.getUser());
        gettype.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails usa = documentSnapshot.toObject(userDetails.class);
                if (usa.getType() == 1) {
                    floater.setVisibility(View.VISIBLE);
                    order.setTextColor(View.VISIBLE);
                }
            }
        });
        CollectionReference collectionReference = firestore.collection("Orders").document(id).collection("products");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    cartProductSet setproduct = query.toObject(cartProductSet.class);
                    setproduct.setProductId(query.getId());
                    list.add(setproduct);
                }
                products = new cartProductsRecy(list, getBaseContext(),"not");
                recyclerView.setAdapter(products);
            }
        });
        recyclerView.setHasFixedSize(true);
        floater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedback = new Intent(getBaseContext(), Respond.class);
                feedback.putExtra("id", id);
                startActivity(feedback);
            }
        });
    }


}
