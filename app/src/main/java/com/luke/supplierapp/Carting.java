package com.luke.supplierapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Carting extends AppCompatActivity {
    private Toolbar toolbar;
  private RecyclerView recyclerView;
  private Context context;
  private FirebaseFirestore firestore;
  private List<setEnqChat> cartlist;
private enquiriesRecy adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carting);
        context=this;
        cartlist=new ArrayList<>();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sellers & Carts");
        toolbar.setTitleTextColor(Color.RED);
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestore=FirebaseFirestore.getInstance();
        sqlite sql=new sqlite(this);
        CollectionReference dc=firestore.collection("Carting").document(sql.getUser()).collection("cart");
        dc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()!=0){
                for (QueryDocumentSnapshot quey:queryDocumentSnapshots){
                    setEnqChat enq=quey.toObject(setEnqChat.class);
                    enq.setUsedId(quey.getId());
                    cartlist.add(enq);
                }
                }
                adapter=new enquiriesRecy(cartlist,context);
                recyclerView.setAdapter(adapter);
            }
        });
        recyclerView.setHasFixedSize(true);
    }
}
