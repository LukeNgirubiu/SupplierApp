package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class updateForProducts extends AppCompatActivity {
private EditText productname,prodquant,productp,unit;
private Button send;
private  BroadcastReceiver broadcastReceiver;
private long price,prodq;
private FirebaseFirestore firestore;
private Double Longitude,Latitude;
private String prodname,Units,category,Id,picture;
    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Longitude = intent.getDoubleExtra("Longi", 0);
                    Latitude = intent.getDoubleExtra("Latit", 0);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("Locations"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_for_products);
         productname = findViewById(R.id.productName);
        prodquant = findViewById(R.id.quantity);
         productp = findViewById(R.id.prodPrice);
         unit = findViewById(R.id.units);
        send = findViewById(R.id.submit);
        Bundle bundle=getIntent().getExtras();
        prodname=bundle.getString("Name");
        prodq=bundle.getLong("QUANTITY");
        price=bundle.getLong("PRICE");
        Units=bundle.getString("UNITS");
        picture=bundle.getString("IMAGE");
        category=bundle.getString("CATEGORY");
        Id=bundle.getString("ID");
        productname.setText(prodname);
        prodquant.setText(Long.toString(prodq));
        productp.setText(Long.toString(price));
        unit.setText(Units);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlite sql=new sqlite(getBaseContext());

                final CollectionReference collectionReference = firestore.collection("Category").document(category).collection("category");
                DocumentReference prodC = firestore.collection("Products").document(sql.getUser()).collection("products").document(Id);
                if((productname.getText().toString().length()>3)&(prodquant.getText().toString().length()>0)&(productp .getText().toString().length()>1)&(unit.getText().toString().length()>0)){
                    DocumentReference doc = collectionReference.document(sql.getUser());
                    categorySet catg = new categorySet();
                    catg.setLatitude(Latitude);
                    catg.setLongitude(Longitude);
                    doc.set(catg);
                    productSet prod = new productSet();
                    prod.setCategoryId(category);
                    prod.setProductName(productname.getText().toString());
                    prod.setProductPicture(picture);
                    prod.setQuantity(Long.parseLong(prodquant.getText().toString()));
                    prod.setProductPrice(Long.parseLong(productp.getText().toString()));
                    prod.setUnits(unit.getText().toString());
                    prodC.set(prod).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent tohomes = new Intent(getBaseContext(), Homes.class);
                            startActivity(tohomes);

                        }
                    });
                }
                else {

                }
            }
        });

    }
}
