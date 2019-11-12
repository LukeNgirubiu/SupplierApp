package com.luke.supplierapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class showSuppliers extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editText, slogan;
    private FirebaseFirestore firestore;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_suppliers);
        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.buName);
        slogan = findViewById(R.id.buSlogan);
        button = findViewById(R.id.sendingDetails);
        setSupportActionBar(toolbar);
        Bundle bundle=getIntent().getExtras();
        editText.setText(bundle.getString("businessName"));
        slogan.setText(bundle.getString("slogan"));
        getSupportActionBar().setTitle("Adding Business Name");
        firestore = FirebaseFirestore.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((editText.getText().toString().length() > 2) && (slogan.getText().toString().length() > 2)) {
                    final sqlite sqlite = new sqlite(getBaseContext());
                    final DocumentReference collectionReference = firestore.collection("BusinessNames").document(sqlite.getUser());
                    businessNames business = new businessNames();
                    business.setBusinessName(editText.getText().toString());
                    business.setSlogan(slogan.getText().toString());
                    collectionReference.set(business).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(), "Changes made successfully", Toast.LENGTH_SHORT).show();
                            Intent send=new Intent(getBaseContext(),Homes.class);
                            startActivity(send);
                        }
                    });

                } else {
                    Toast.makeText(getBaseContext(), "Make a valid entry", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
