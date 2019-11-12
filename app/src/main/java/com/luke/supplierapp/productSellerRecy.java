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
                                AlertDialog.Builder prepare = new AlertDialog.Builder(context);
                                prepare.setTitle("Delete product");
                                prepare.setMessage(setProduct.getProductName());
                                prepare.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sqlite sql = new sqlite(context);
                                        final List<Images> list = new ArrayList<>();
                                        CollectionReference refer = firestore.collection("Productimges").document(sql.getUser()).collection(setProduct.getProductId());
                                        DocumentReference delete = reference.document(setProduct.getProductId());
                                        delete.delete().addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                products.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position,products.size());
                                                Toast.makeText(context, "Deletion successful", Toast.LENGTH_SHORT).show();
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
                                        dialog.dismiss();
                                    }
                                });
                                prepare.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog creating = prepare.create();
                                creating.show();
                                break;
                            case R.id.viewImage:
                                Intent intent = new Intent(context, productsImages.class);
                                intent.putExtra("productid", setProduct.getProductId());
                                intent.putExtra("userId", sqlite.getUser());
                                context.startActivity(intent);
                                break;
                            case R.id.details:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.update_products_details, null);
                                final EditText productname = view.findViewById(R.id.productName);
                                final EditText prodquant = view.findViewById(R.id.quantity);
                                final EditText productp = view.findViewById(R.id.prodPrice);
                                final EditText unit = view.findViewById(R.id.units);
                                Button send = view.findViewById(R.id.submit);
                                productname.setText(setProduct.getProductName());
                                prodquant.setText(Long.toString(setProduct.getQuantity()));
                                productp.setText(Long.toString(setProduct.getProductPrice()));
                                unit.setText(setProduct.getUnits());
                                send.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if ((productname.getText().toString().length() > 1)
                                                && (prodquant.getText().toString().length() > 0) &&
                                                (unit.getText().toString().length() > 0) &&
                                                (productp.getText().toString().length() > 0)) {
                                            DocumentReference doc = reference.document(setProduct.getProductId());
                                            productSet update = new productSet();
                                            update.setUnits(unit.getText().toString());
                                            update.setProductPrice(Long.parseLong(productp.getText().toString()));
                                            update.setProductName(productname.getText().toString());
                                            update.setQuantity(Long.parseLong(prodquant.getText().toString()));
                                            update.setCategoryId(setProduct.getCategoryId());
                                            update.setProductPicture(setProduct.getProductPicture());
                                            doc.set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, "Make correct entries", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.setView(view);
                                AlertDialog dialog = builder.create();
                                dialog.show();
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
        AppCompatImageView imagings;
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
