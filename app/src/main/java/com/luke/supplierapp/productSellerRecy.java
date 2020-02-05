package com.luke.supplierapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class productSellerRecy extends RecyclerView.Adapter<productSellerRecy.Viewer> {
    List<productSet> products;
    Context context;

    public productSellerRecy(List<productSet> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.productard, parent, false);
        return new Viewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewer holder, final int position) {
        final productSet setProduct = products.get(position);
        Picasso.get().load(setProduct.getProductPicture()).into(holder.imagings);
        holder.productPrice.setText("Ksh " + setProduct.getProductPrice());
        holder.productQuantity.setText(getItemCount() + " " + setProduct.getUnits());
        holder.producName.setText(setProduct.getProductName());
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                PopupMenu menu = new PopupMenu(context, holder.cardView);
                menu.inflate(R.menu.updateproduct);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        sqlite sqlite = new sqlite(context);
                        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        final CollectionReference reference = firestore.collection("Products").document(sqlite.getUser()).collection("products");
                        switch (item.getItemId()) {
                            case R.id.addimage:
                                Intent nud = new Intent(context, addingImage.class);
                                nud.putExtra("productId", setProduct.getProductId());
                                context.startActivity(nud);

                                break;
                            case R.id.delete:
                                sqlite sql = new sqlite(context);
                                final List<Images> list = new ArrayList<>();
                                CollectionReference refer = firestore.collection("Productimges").document(sql.getUser()).collection(setProduct.getProductId());
                                DocumentReference delet = reference.document(setProduct.getProductId());
                                delet.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show();
                                    }
                                });
                                refer.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            Images images = doc.toObject(Images.class);
                                            images.setImgeId(doc.getId());
                                            list.add(images);
                                        }
                                    }
                                });
                                for (Images img : list) {
                                    DocumentReference del = refer.document(img.getProductId());
                                    del.delete();
                                }
                                break;
                            case R.id.viewImage:
                                Intent intent = new Intent(context, productsImages.class);
                                intent.putExtra("productid", setProduct.getProductId());
                                intent.putExtra("userId", sqlite.getUser());
                                context.startActivity(intent);
                                break;
                            case R.id.details:
                               Intent intent1=new Intent(context,updateForProducts.class);
                               intent1.putExtra("Name",setProduct.getProductName());
                               intent1.putExtra("ID",setProduct.getProductId());
                               intent1.putExtra("CATEGORY",setProduct.getCategoryId());
                               intent1.putExtra("IMAGE",setProduct.getProductPicture());
                               intent1.putExtra("PRICE",setProduct.getProductPrice());
                               intent1.putExtra("QUANTITY",setProduct.getQuantity());
                               intent1.putExtra("UNITS",setProduct.getUnits());
                               context.startActivity(intent1);
                                break;
                        }
                        return false;
                    }
                });
                menu.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class Viewer extends RecyclerView.ViewHolder {
        CardView cardView;
       ImageView imagings;
        TextView productPrice, productQuantity, producName;

        public Viewer(View itemView) {
            super(itemView);
            producName = itemView.findViewById(R.id.productName);
            cardView = itemView.findViewById(R.id.product);
            imagings = itemView.findViewById(R.id.productImage);
            productPrice = itemView.findViewById(R.id.price);
            productQuantity = itemView.findViewById(R.id.quantity);
        }
    }

}
