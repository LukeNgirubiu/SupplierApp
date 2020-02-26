package com.luke.supplierapp;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
private String name;

    public cartProductsRecy(List<cartProductSet> list, Context context, String name) {
        this.list = list;
        this.context = context;
        this.name = name;
    }

    @NonNull
    @Override
    public Viewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cartproductsitems, null);
        return new Viewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewer holder, final int position) {
        final cartProductSet product = list.get(position);
        holder.prodtotal.setText("Ksh "+Long.toString(product.getTotal()));
        holder.prodprice.setText(Long.toString(product.getProductPrice()) + " per " + product.getUnits());
        holder.prodname.setText(product.getProductName());
        holder.prodquant.setText(Long.toString(product.getQuantity()) + product.getUnits());
        holder.lout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(name.equals("allow")){
                sqlite sql = new sqlite(context);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                final DocumentReference refer2 = firestore.collection("Cart").document(sql.getUser()).collection("cart").document(product.getProductId());
                final PopupMenu pop = new PopupMenu(context, holder.lout);
                pop.inflate(R.menu.removeitem);
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int pressedItem = item.getItemId();
                        switch (pressedItem) {
                            case R.id.remove:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Delete the Order");
                                builder.setMessage("Continue with deleting the order?");
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                refer2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,list.size());
                                    }
                                });
                                break;
                            case R.id.update:
                                Intent intent = new Intent(context, CartUpdate.class);
                                intent.putExtra("productid", product.getProductId());
                                intent.putExtra("userId",product.getSellerId());
                                intent.putExtra("ProductN",product.getQuantity());
                                context.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                pop.show();
            }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewer extends RecyclerView.ViewHolder {
        TextView prodname, prodquant, prodprice, prodtotal;
        CardView lout;

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
