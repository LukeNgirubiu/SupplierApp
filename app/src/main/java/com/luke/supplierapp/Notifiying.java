package com.luke.supplierapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notifiying extends AppCompatActivity {
private FirebaseFirestore firestore;
private List<setSubsriptions> list;
private RecyclerView recyclerView;
private subscriberDetails subss;
private Toolbar tool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiying);
        recyclerView=findViewById(R.id.recycler);
        list=new ArrayList<>();
        tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setTitle("Subscribers");
        tool.setTitleTextColor(Color.WHITE);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        DividerItemDecoration deco=new DividerItemDecoration(this,manager.getOrientation());
        recyclerView.addItemDecoration(deco);
        recyclerView.setLayoutManager(manager);
        firestore=FirebaseFirestore.getInstance();
        sqlite sql=new sqlite(this);
         CollectionReference collect=firestore.collection("Subscription").document(sql.getUser()).collection("subscription");

collect.orderBy("seen", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
        if(!queryDocumentSnapshots.isEmpty()){
        for(QueryDocumentSnapshot query:queryDocumentSnapshots){
            setSubsriptions sub=query.toObject(setSubsriptions.class);
            sub.setId(query.getId());
            list.add(sub);
        }}
        else {
            Toast.makeText(getBaseContext(),"No subscription",Toast.LENGTH_SHORT).show();}
        subss=new subscriberDetails(list,getBaseContext());
        recyclerView.setAdapter(subss);
    }
});
recyclerView.setHasFixedSize(true);
for(setSubsriptions subs:list){
    DocumentReference doc=collect.document(subs.getId());
    setSubsriptions sub=new setSubsriptions();
    sub.setSeen(true);
    sub.setBuyerId(subs.getBuyerId());
    doc.set(sub);
}
    }
}
