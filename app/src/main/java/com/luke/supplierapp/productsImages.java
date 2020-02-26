package com.luke.supplierapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class productsImages extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private String userImage;
    private RecyclerView recyc;
    String productId;
    private Toolbar toolbar;
    List<Images> imges;
    private imageShowRecy showRecy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_images);
        firestore = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        recyc = findViewById(R.id.rec);
        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Photos");
        userImage = bundle.getString("userId");
        productId=bundle.getString("productid");
        imges = new ArrayList<>();

        recyc.setLayoutManager(new LinearLayoutManager(this));
        CollectionReference refer = firestore.collection("Productimges").
                document(userImage)
                .collection(productId);
        refer.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot qr : queryDocumentSnapshots) {
                    Images img = qr.toObject(Images.class);
                    img.setImgeId(qr.getId());
                    imges.add(img);
                }
                showRecy=new imageShowRecy(getBaseContext(),imges,userImage);
                recyc.setAdapter(showRecy);
            }

        });

    }
}
