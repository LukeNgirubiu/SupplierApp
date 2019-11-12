package com.luke.supplierapp;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class cartProductsRecy extends RecyclerView.Adapter<cartProductsRecy.Viewer> {
    private List<cartProductSet> list;
    private Context context;

    public cartProductsRecy(List<cartProductSet> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cartproductsitems, null);
        return new Viewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewer holder, int position) {
        final cartProductSet product = list.get(position);
        holder.prodtotal.setText(Long.toString(product.getTotal()));
        holder.prodprice.setText(Long.toString(product.getProductPrice()) + "per " + product.getUnits());
        holder.prodname.setText(product.getProductName());
        holder.prodquant.setText(Long.toString(product.getQuantity()) + product.getUnits());
        holder.lout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sqlite sql = new sqlite(context);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                final DocumentReference refer2 = firestore.collection("Cart").document(sql.getUser()).collection("products").document(product.getProductId());
                final PopupMenu pop = new PopupMenu(context, holder.lout);
                pop.inflate(R.menu.removeitem);
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int pressedItem = item.getItemId();
                        switch (pressedItem) {
                            case R.id.remove:
                                refer2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, product.getProductName() + " Removed", Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            case R.id.update:
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LayoutInflater inflating = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflating.inflate(R.layout.cartadding, null);
                                final TextView totalcost = view.findViewById(R.id.totalcost);
                                final TextView productsname = view.findViewById(R.id.productName);
                                final EditText quantity = view.findViewById(R.id.quantity);
                                Button send = view.findViewById(R.id.addingProducts);
                                totalcost.setText(Long.toString(product.getTotal()));
                                productsname.setText(product.getProductName());
                                quantity.setText(Long.toString(product.getQuantity()) + " " + product.getUnits());
                                send.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cartProductSet cart = new cartProductSet();
                                        cart.setCategoryId(product.getCategoryId());
                                        cart.setProductName(product.getProductName());
                                        cart.setProductPrice(product.getProductPrice());
                                        cart.setQuantity(product.getQuantity());
                                        cart.setCategoryId(product.getCategoryId());
                                        cart.setUnits(product.getUnits());
                                        cart.setTotal(product.getProductPrice() * product.getQuantity());
                                        refer2.set(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, Long.toString(product.getQuantity()) + " " + product.getUnits() + "added", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Updating failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                alert.setView(view);
                                AlertDialog dialog=alert.create();
                                dialog.show();
                                break;
                        }
                        return false;
                    }
                });
                pop.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewer extends RecyclerView.ViewHolder {
        TextView prodname, prodquant, prodprice, prodtotal;
        LinearLayout lout;

        public Viewer(View itemView) {
            super(itemView);
            lout = itemView.findViewById(R.id.layout);
            prodname = itemView.findViewById(R.id.productName);
            prodquant = itemView.findViewById(R.id.prodQuantity);
            prodprice = itemView.findViewById(R.id.prodPrice);
            prodtotal = itemView.findViewById(R.id.totalcost);
        }
    }
}
