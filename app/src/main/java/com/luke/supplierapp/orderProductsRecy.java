package com.luke.supplierapp;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class orderProductsRecy extends RecyclerView.Adapter<orderProductsRecy.Viewer> {
    private List<cartProductSet> list;
    private Context context;

    public orderProductsRecy(List<cartProductSet> list, Context context) {
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
        cartProductSet product = list.get(position);
        holder.prodtotal.setText(Long.toString(product.getTotal()));
        holder.prodprice.setText(Long.toString(product.getProductPrice()) + "per " + product.getUnits());
        holder.prodname.setText(product.getProductName());
        holder.prodquant.setText(Long.toString(product.getQuantity()));
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
