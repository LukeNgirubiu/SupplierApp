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
import android.widget.ImageView;
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
    public List<productSet> products;
    public Context context;
    public String Id;

    public productSetRecycler(List<productSet> products, Context context, String Id) {
        this.products = products;
        this.context = context;
        this.Id = Id;

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
                        if (id == R.id.buy) {
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            sqlite sql = new sqlite(context);
                            final DocumentReference collectionReference = firestore.collection("Cart").
                                    document(sql.getUser()).collection("cart").document(setProduct.getProductId());
                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        Toast.makeText(context,"The product already available in the cart",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(context, Cart.class);
                                    intent.putExtra("productid", setProduct.getProductId());
                                    intent.putExtra("userId",Id);
                                    context.startActivity(intent);
                                } else{
                                        Intent intent = new Intent(context, Cart.class);
                                        intent.putExtra("productid", setProduct.getProductId());
                                        intent.putExtra("userId",Id);
                                        context.startActivity(intent);
                                    }}
                            });

                        }

                        if (id == R.id.details) {
                            Intent intent = new Intent(context, productsImages.class);
                            intent.putExtra("productid", setProduct.getProductId());
                            intent.putExtra("userId",Id);
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
        ImageView imagings;
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
