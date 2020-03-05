package com.luke.supplierapp;

import android.content.Context;
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
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Iterators;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private Toolbar toolbar;
    private List<cartProductSet> list;
    private cartProductsRecy products;
    private String Id, orderId;
    private Context context;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_products);
        context = this;
        firestore = FirebaseFirestore.getInstance();
        floating = findViewById(R.id.order);
        toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cart products");
        recyclerView = findViewById(R.id.recycler);
        Bundle bundle = getIntent().getExtras();
        Id = bundle.getString("Id");
        LinearLayoutManager manage = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manage);
        DividerItemDecoration divide = new DividerItemDecoration(this, manage.getOrientation());
        recyclerView.addItemDecoration(divide);
        list = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long totalCost = 0;
                for (cartProductSet product : list) {
                    totalCost = totalCost + product.getTotal();
                }
                if(totalCost>0){
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("Making an Order");
                builder.setMessage("Order goods worthy Ksh" + Long.toString(totalCost)+" ?");
                final long finalTotalCost1 = totalCost;
                builder.setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeOrder(finalTotalCost1);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setTitle("Cart Empty");
                    builder.setMessage("No product in the cart");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    private void makeOrder(Long totalCost) {
        final CollectionReference reference;
        final sqlite sql = new sqlite(getBaseContext());
        setQrders order = new setQrders();
        order.setCustomerId(sql.getUser());
        order.setSupplierId(Id);
        order.setSeen(false);
        order.setBuyer(0);
        order.setNotified(false);
        order.setSeller(0);
        order.setProductPrice(totalCost);
        reference = firestore.collection("Orders");

        reference.add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                orderId = documentReference.getId();
                CollectionReference collect = reference.document(orderId).collection("products");
                DocumentReference addingDoc;
                cartProductSet enter = new cartProductSet();
                for (cartProductSet prodct : list) {
                    enter.setTotal(prodct.getTotal());
                    enter.setUnits(prodct.getUnits());
                    enter.setProductName(prodct.getProductName());
                    enter.setCategoryId(prodct.getCategoryId());
                    enter.setProductPrice(prodct.getProductPrice());
                    enter.setQuantity(prodct.getQuantity());
                    addingDoc = collect.document(prodct.getProductId());
                    addingDoc.set(enter);
                    DocumentReference collectionReference = firestore.collection("Cart").
                            document(sql.getUser()).collection(Id).document(prodct.getProductId());
                    collectionReference.delete();
                }
                list.clear();
                DocumentReference refering = firestore.collection("Cart").document(sql.getUser());
                refering.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Order sent", Toast.LENGTH_LONG).show();
                        Intent backtoProducts = new Intent(getBaseContext(), Homec.class);
                        startActivity(backtoProducts);
                        DocumentReference dc = firestore.collection("Carting").document(sql.getUser())
                                .collection("cart").document(Id);
                        dc.delete();

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final sqlite sql = new sqlite(this);

        CollectionReference refer2 = firestore.collection("Cart").
                document(sql.getUser()).collection(Id);
        refer2.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                       @Override
                                       public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                           list.clear();
                                           if (queryDocumentSnapshots.size() != 0) {
                                               for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                                   cartProductSet setproduct = query.toObject(cartProductSet.class);
                                                   setproduct.setProductId(query.getId());
                                                   list.add(setproduct);
                                               }
                                               products = new cartProductsRecy(list, context, "allow");
                                               recyclerView.setAdapter(products);
                                           }
                                       }

                                   }
        );
    }
}
