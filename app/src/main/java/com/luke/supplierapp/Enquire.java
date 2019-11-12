package com.luke.supplierapp;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class Enquire extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private RecyclerView recy;
    private Toolbar tool;
    private ImageButton send;
    private EditText enquire;
    private enquiriesRecy enq;
    List<setEnquire> listing;
    private CollectionReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquire);
        tool = findViewById(R.id.toolbar);
        send = findViewById(R.id.submit);
        recy = findViewById(R.id.queries);
        enquire = findViewById(R.id.enquiry);
        Bundle bundle = getIntent().getExtras();
        listing = new ArrayList<>();
        final String supplier = bundle.getString("user");
        firestore = FirebaseFirestore.getInstance();
        sqlite sql = new sqlite(this);
        DocumentReference refere = firestore.collection("usersDetails").document(supplier);
        final String queryId;
        if (supplier.length() > sql.getUser().length()) {
            queryId = supplier + sql.getUser();
        } else {
            queryId = sql.getUser() + supplier;
        }
        reference = firestore.collection("Enquiry").document(queryId).collection("queries");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View view = getLayoutInflater().inflate(R.layout.showuserchat, null);
        actionBar.setCustomView(view);
        final CircleImageView cir = view.findViewById(R.id.profilepicture);
        final TextView firstname = view.findViewById(R.id.buyername);
        final TextView secondName = view.findViewById(R.id.secondNamebuyername);
        final TextView contact = view.findViewById(R.id.contacts);
        refere.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails user = documentSnapshot.toObject(userDetails.class);
                Picasso.get().load(user.getImagePath()).into(cir);
                firstname.setText(user.getFirstName());
                secondName.setText(user.getSecondName());
                contact.setText(user.getContact());
            }

        });
        recy.setLayoutManager(new LinearLayoutManager(this));
        reference.orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override

            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                listing.clear();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    setEnquire enq = query.toObject(setEnquire.class);
                    enq.setEnquiryId(query.getId());
                    listing.add(enq);
                }
                enq = new enquiriesRecy(listing, getBaseContext());
                recy.setAdapter(enq);
            }

        });
        recy.setHasFixedSize(true);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(enquire.getText().toString())) {
                    final sqlite sql = new sqlite(getBaseContext());
                    setEnquire query=new setEnquire();
                    query.setFromId(sql.getUser());
                    query.setToId(supplier);
                    query.setMessage(enquire.getText().toString());
                    reference.add(query);
                    final CollectionReference refer = firestore.collection("Numenq").document(supplier).collection("enquire");
                    final setEnqChat sb=new setEnqChat();

                    final DocumentReference chats=refer.document(sql.getUser());//for the suppliers screen
                    chats.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                            setEnqChat chat=documentSnapshot.toObject(setEnqChat.class);
                            int q=chat.getSeen();
                            if(q>0){
                                sb.setUsedId(sql.getUser());
                                sb.setSeen(q+1);
                                chats.set(sb);
                            }

                        }
                        else {
                              sb.setSeen(1);
                              sb.setUsedId(sql.getUser());
                              chats.set(sb);
                            }}
                    });

                } else {
                    Toast.makeText(getBaseContext(), "Make a chart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
