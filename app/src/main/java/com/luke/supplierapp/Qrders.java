package com.luke.supplierapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class Qrders extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private String user;
    private Toolbar toolbar;
    private qrderRecy qorders;
    private RecyclerView recyclerView;
    private CollectionReference collectionReference;
    private List<setQrders> list;
    private DocumentReference collect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrders);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Orders");
        recyclerView = findViewById(R.id.recyclerview);
        firestore = FirebaseFirestore.getInstance();
        list=new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("userType");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    public void onResume() {
        super.onResume();
        sqlite sql = new sqlite(this);
        collectionReference = firestore.collection("Orders");
        collectionReference.whereEqualTo(user, sql.getUser()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                list.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                        setQrders setqorders = query.toObject(setQrders.class);
                        setqorders.setOrderId(query.getId());
                        list.add(setqorders);
                    }
                    qorders = new qrderRecy(getBaseContext(), list, user);
                    recyclerView.setAdapter(qorders);}else {
                    Toast.makeText(getBaseContext(),"No order",Toast.LENGTH_SHORT).show();}}

        });
        recyclerView.setHasFixedSize(true);
        for(setQrders oder:list){
            collect =   collectionReference.document(oder.getOrderId());
            setQrders nword=new setQrders();
            nword.setProductPrice(oder.getProductPrice());
            nword.setSeller(0);
            nword.setSeen(false);
            nword.setSupplierId(oder.getSupplierId());
            nword.setCustomerId(oder.getCustomerId());
            nword.setBuyer(0);
            nword.setNotified(true);
            nword.setDate(oder.getDate());
            collect.set(nword);
        }
    }
}
