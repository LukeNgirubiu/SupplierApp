package com.luke.supplierapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CartUpdate extends AppCompatActivity {
    TextView productsname, quantity, cost;
    Toolbar toolbar;
    Button send;
EditText number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar = findViewById(R.id.toolbar);
        productsname = findViewById(R.id.p_Name);
        cost = findViewById(R.id.cost);
        send = findViewById(R.id.send);
        quantity = findViewById(R.id.Quant);
        number=findViewById(R.id.number);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cart");
        Bundle bundle = getIntent().getExtras();
        //productId,sellerid,
        final Long quantit=bundle.getLong("ProductN");
        String productId = bundle.getString("productid");
        final String userId = bundle.getString("userId");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        sqlite sql = new sqlite(getBaseContext());
        final DocumentReference collectionReference = firestore.collection("Cart").
                document(sql.getUser()).collection(userId).document(productId);
        final DocumentReference refer2 = firestore.collection("Cart").document(sql.getUser());
        final DocumentReference prodC = firestore.collection("Products").document(userId).collection("products").document(productId);
        prodC.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                prodC.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        productSet productSet = documentSnapshot.toObject(productSet.class);
                        productSet.setProductId(documentSnapshot.getId());
                        productsname.setText(productSet.getProductName());
                        quantity.setText("Available units " + Long.toString(productSet.getQuantity()));
                        cost.setText(Long.toString(productSet.getProductPrice()));
                        number.setText(Long.toString(quantit));
                    }
                });
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String quant = number.getText().toString().trim();
                if (!quant.isEmpty()) {
                    prodC.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            productSet setProduct = documentSnapshot.toObject(productSet.class);
                            if (setProduct.getQuantity() >= Long.parseLong(quant)) {
                                cartProductSet cart = new cartProductSet();
                                cart.setCategoryId(setProduct.getCategoryId());
                                cart.setProductName(setProduct.getProductName());
                                cart.setProductPrice(setProduct.getProductPrice());
                                cart.setProductId(setProduct.getProductId());
                                cart.setSellerId(userId);
                                cart.setQuantity(Long.parseLong(quant));
                                cart.setCategoryId(setProduct.getCategoryId());
                                cart.setUnits(setProduct.getUnits());
                                cart.setTotal(setProduct.getProductPrice() * Long.parseLong(quant));
                                collectionReference.set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                       Intent intent=new Intent(getBaseContext(),customerSupplier.class);
                                        intent.putExtra("Determiner",1);
                                        finishAffinity();
                                    }
                                });
                            } else {
                                Toast.makeText(getBaseContext(), "Product amount is not available", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "Enter amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
