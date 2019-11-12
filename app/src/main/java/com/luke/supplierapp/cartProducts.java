package com.luke.supplierapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Iterators;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

public class cartProducts extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FloatingActionButton floating;
    private CollectionReference collect;
    private List<cartProductSet> list;
    private cartProductsRecy products;
    private String Id, orderId;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_products);
        firestore = FirebaseFirestore.getInstance();
        floating = findViewById(R.id.recycler);
        recyclerView = findViewById(R.id.recycler);
        Bundle bundle = getIntent().getExtras();
        Id = bundle.getString("id");
        LinearLayoutManager manage = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manage);
        DividerItemDecoration divide = new DividerItemDecoration(this, manage.getOrientation());
        recyclerView.addItemDecoration(divide);
        sqlite sql = new sqlite(this);
        list = new ArrayList<>();
        final CollectionReference refer2 = firestore.collection("Cart").document(sql.getUser()).collection("products");
        refer2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.size() != 0) {
                        cartProductSet setproduct = query.toObject(cartProductSet.class);
                        setproduct.setProductId(query.getId());
                        list.add(setproduct);
                    } else {
                        AlertDialog alert = new AlertDialog.Builder(getBaseContext()).create();
                        alert.setTitle("Cart products");
                        alert.setIcon(R.drawable.cart1);
                        alert.setMessage("No products");
                        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                }
                products = new cartProductsRecy(list, getBaseContext());
                recyclerView.setAdapter(products);
            }
        });
        recyclerView.setHasFixedSize(true);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlite sql = new sqlite(getBaseContext());
                CollectionReference reference;

                long totalCost = 0;
                for (cartProductSet product : list) {
                    totalCost = totalCost + product.getTotal();
                }
                setQrders order = new setQrders();
                order.setCustomerId(sql.getUser());
                order.setSupplierId(Id);
                order.setSeen(false);
                order.setBuyer(0);
                order.setNotified(false);
                order.setSeller(0);
                order.setNotified(false);
                order.setProductPrice(totalCost);
                reference = firestore.collection("Orders");

                reference.add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        orderId = documentReference.getId();
                    }
                });
                CollectionReference collect = reference.document(orderId).collection("products");
                DocumentReference addingDoc;
                cartProductSet enter=new cartProductSet();
                for (cartProductSet prodct : list) {
                    enter.setTotal(prodct.getTotal());
                    enter.setUnits(prodct.getUnits());
                    enter.setProductName(prodct.getProductName());
                    enter.setCategoryId(prodct.getCategoryId());
                    enter.setProductPrice(prodct.getProductPrice());
                    enter.setQuantity(prodct.getQuantity());
                    addingDoc=collect.document(prodct.getProductId());
                    addingDoc.set(enter).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(),"Order posted",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                DocumentReference refering = firestore.collection("Cart").document(sql.getUser());
                refering.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(),"Order sent",Toast.LENGTH_SHORT).show();
                        Intent backtoProducts=new Intent(getBaseContext(),productsShow.class);
                        startActivity(backtoProducts);

                    }
                });
                list.clear();
                Intent toShowProducts = new Intent(getBaseContext(), productsShow.class);
                startActivity(toShowProducts);
            }
        });
    }
}
