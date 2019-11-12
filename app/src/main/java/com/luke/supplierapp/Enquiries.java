package com.luke.supplierapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Enquiries extends AppCompatActivity {
    FirebaseFirestore firestore;
    private Toolbar tool;
    private RecyclerView recy;
    private enquiriRecy chats;
    private List<setEnqChat> enque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiries);
        tool = findViewById(R.id.too);
        recy = findViewById(R.id.recyclerview);
        firestore = FirebaseFirestore.getInstance();
        sqlite sqlite = new sqlite(this);
        enque = new ArrayList<>();
        LinearLayoutManager layout=new LinearLayoutManager(this);
        DividerItemDecoration deco=new DividerItemDecoration(this,layout.getOrientation());
        recy.addItemDecoration(deco);
        recy.setLayoutManager(layout);
        final CollectionReference refer = firestore.collection("Numenq").document(sqlite.getUser()).collection("enquire");
        refer.orderBy("seen", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                enque.clear();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    setEnqChat eq = query.toObject(setEnqChat.class);
                    enque.add(eq);
                }
                chats = new enquiriRecy(enque, getBaseContext());
                recy.setAdapter(chats);
            }

        });
recy.setHasFixedSize(true);
for(setEnqChat chating:enque){
    DocumentReference dc=refer.document(chating.getUsedId());
    setEnqChat cht=new setEnqChat();
    cht.setUsedId(chating.getUsedId());
    cht.setSeen(0);
    dc.set(cht);

}
    }
}
