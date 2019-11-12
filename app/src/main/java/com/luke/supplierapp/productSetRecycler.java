package com.luke.supplierapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class productSetRecycler extends RecyclerView.Adapter<productSetRecycler.Veiwing> {
    private List<productSet> products;
    private Context context;
    private String id;

    public productSetRecycler(List<productSet> products, Context context, String id) {
        this.products = products;
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public Veiwing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.productard, parent, false);
        return new Veiwing(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Veiwing holder, int position) {
        final productSet setProduct = products.get(position);
        Picasso.get().load(setProduct.getProductPicture()).into(holder.imagings);
        holder.productPrice.setText("Ksh " + setProduct.getProductPrice());
        holder.productQuantity.setText(getItemCount() + " " + setProduct.getUnits());
        holder.producName.setText(setProduct.getProductName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.cardView);
                popupMenu.inflate(R.menu.productmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        sqlite sql = new sqlite(context);
                        final DocumentReference collectionReference = firestore.collection("Cart").
                                document(sql.getUser()).collection("cart").document(setProduct.getProductId());
                        final DocumentReference refer2 = firestore.collection("Cart").document(sql.getUser());

                        if (id == R.id.buy) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            LayoutInflater inflating = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflating.inflate(R.layout.cart, null);
                            final TextView totalcost = view.findViewById(R.id.totalcost);
                            final TextView productsname = view.findViewById(R.id.productName);
                            final EditText quantity = view.findViewById(R.id.quantity);
                            Button send = view.findViewById(R.id.addingProducts);
                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        cartProductSet productset = documentSnapshot.toObject(cartProductSet.class);
                                        productsname.setText(productset.getProductName());
                                        quantity.setText(Long.toString(productset.getQuantity()));

                                    } else {
                                        productsname.setText(setProduct.getProductName());
                                    }
                                }
                            });
                            refer2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        setQrders order = documentSnapshot.toObject(setQrders.class);
                                        totalcost.setText(Long.toString(order.getProductPrice()));
                                    }
                                }
                            });
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String quant = quantity.getText().toString().trim();
                                    if (!quant.isEmpty()) {
                                        if (setProduct.getQuantity() >= Long.parseLong(quant)) {
                                            cartProductSet cart = new cartProductSet();
                                            cart.setCategoryId(setProduct.getCategoryId());
                                            cart.setProductName(setProduct.getProductName());
                                            cart.setProductPrice(setProduct.getProductPrice());
                                            cart.setQuantity(Long.parseLong(quant));
                                            cart.setCategoryId(setProduct.getCategoryId());
                                            cart.setUnits(setProduct.getUnits());
                                            cart.setTotal(setProduct.getProductPrice() * Long.parseLong(quant));
                                            collectionReference.set(cart);
                                        } else {

                                            Toast.makeText(context, "Product amount is not available", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            alert.setView(view);
                            AlertDialog show = alert.create();
                            show.show();
                        }

                        if (id == R.id.details) {
                            Intent intent = new Intent(context, productsImages.class);
                            intent.putExtra("productid", setProduct.getProductId());
                            intent.putExtra("userId",id);
                            context.startActivity(intent);
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class Veiwing extends RecyclerView.ViewHolder {
        CardView cardView;
        AppCompatImageView imagings;
        TextView productPrice, productQuantity, producName;

        public Veiwing(View itemView) {
            super(itemView);
            producName = itemView.findViewById(R.id.productName);
            cardView = itemView.findViewById(R.id.product);
            imagings = itemView.findViewById(R.id.productImage);
            productPrice = itemView.findViewById(R.id.price);
            productQuantity = itemView.findViewById(R.id.quantity);
        }
    }
}
