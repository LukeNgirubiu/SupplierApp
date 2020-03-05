package com.luke.supplierapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Cart extends AppCompatActivity {
    TextView productsname, quantity, cost;
    Toolbar toolbar;
    private String userId;
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
        String productId = bundle.getString("productid");
        userId = bundle.getString("userId");
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final sqlite sql = new sqlite(getBaseContext());
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
                                cart.setQuantity(Long.parseLong(quant));
                                cart.setCategoryId(setProduct.getCategoryId());
                                cart.setUnits(setProduct.getUnits());
                                cart.setSellerId(userId);
                                cart.setTotal(setProduct.getProductPrice() * Long.parseLong(quant));
                                collectionReference.set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DocumentReference dc=firestore.collection("Carting").document(sql.getUser()).collection("cart").document(userId);
                                        setEnqChat eq=new setEnqChat();
                                        dc.set(eq);
                                        Intent intent=new Intent(getBaseContext(),customerSupplier.class);
                                        intent.putExtra("Determiner",1);
                                        finish();
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
