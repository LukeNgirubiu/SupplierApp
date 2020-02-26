package com.luke.supplierapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Enquiries extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private Toolbar tool;
    private Button send;
    private EditText message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquire);
        tool = findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        tool.setTitleTextColor(Color.RED);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        firestore = FirebaseFirestore.getInstance();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    sendUpdates(message.getText().toString());
                }
            }
        });
    }

    private void sendUpdates(final String message) {
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Sending messages");
        dialog.setMessage("Please wait...");
        dialog.show();
        sqlite sql = new sqlite(this);
        CollectionReference collect = firestore.collection("Subscription").document(sql.getUser()).collection("subscription");
        collect.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    setSubsriptions suscribe = query.toObject(setSubsriptions.class);
                    DocumentReference reference = firestore.collection("Users").document(suscribe.getId());
                    reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                userDetails user = documentSnapshot.toObject(userDetails.class);
                                SmsManager sendingSMs = SmsManager.getDefault();
                                sendingSMs.sendTextMessage(user.getContact(), null, message, null, null);
                                Toast.makeText(getBaseContext(), "Sent", Toast.LENGTH_SHORT).show();
                                Intent back = new Intent(getApplicationContext(), Homes.class);
                                dialog.dismiss();
                                startActivity(back);
                            } catch (Exception y) {
                                y.printStackTrace();
                                Intent back = new Intent(getApplicationContext(), Homes.class);
                                startActivity(back);
                                Toast.makeText(getBaseContext(), "Failed to send", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                        }
                    });
                }

            }
        });

    }
}
