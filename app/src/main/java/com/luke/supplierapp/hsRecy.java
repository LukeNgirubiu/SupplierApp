package com.luke.supplierapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class hsRecy extends RecyclerView.Adapter<hsRecy.Viewing> {
    List<prepareS> list;
    Context context;

    public hsRecy(List<prepareS> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public hsRecy.Viewing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customerview, parent, false);
        return new Viewing(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final hsRecy.Viewing holder, int position) {
        final prepareS prep = list.get(position);
        holder.dashitem.setText(prep.getSupplierMenu());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference reference;
        sqlite sql = new sqlite(context);
        if (prep.getSupplierMenu().equals("Products")) {

            reference = firestore.collection("Products").document(sql.getUser()).collection("products");
            reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int number = queryDocumentSnapshots.size();
                    if (number > 0) {
                        holder.dashquantity.setText(Integer.toString(number));
                    } else {
                        holder.dashquantity.setText("");
                    }
                }
            });
        }
        if (prep.getSupplierMenu().equals("Enquiries")) {

            reference = firestore.collection("Enquiry");
            reference.whereEqualTo("toId",sql.getUser()).whereEqualTo("seen",false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int numba = queryDocumentSnapshots.size();
                    if (numba > 0) {
                        holder.dashquantity.setText(Integer.toString(numba));
                    } else {
                        holder.dashquantity.setText("");
                    }
                }
            });
        }
        if (prep.getSupplierMenu().equals("Subsriptions")) {
            reference = firestore.collection("Subscription").document(sql.getUser()).collection("subscription");
            reference.whereEqualTo("seen",false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int number = queryDocumentSnapshots.size();
                    if (number > 0) {
                        holder.dashquantity.setText(Integer.toString(number));
                    } else {
                        holder.dashquantity.setText("");
                    }
                }
            });
        }
        if(prep.getSupplierMenu().equals("Orders")){
            reference=firestore.collection("Orders").document(sql.getUser()).collection("order");
            reference.whereEqualTo("supplierId",sql.getUser()).whereEqualTo("seen",false).whereEqualTo("seller",0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int number=queryDocumentSnapshots.size();
                    if (number > 0) {
                        holder.dashquantity.setText(Integer.toString(number));
                    } else {
                        holder.dashquantity.setText("");
                    }
                }
            });
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prep.getSupplierMenu().equals("Orders")){
                    Intent send=new Intent(context,Qrders.class);
                    send.putExtra("userType","supplierId");
                    context.startActivity(send);
                }
                if (prep.getSupplierMenu().equals("Subsriptions")) {
                    Intent notify=new Intent(context,Notifiying.class);
                    context.startActivity(notify);
                }
                if (prep.getSupplierMenu().equals("Enquiries")) {
                    Intent intent=new Intent(context,Enquiries.class);
                    context.startActivity(intent);
                }
                if(prep.getSupplierMenu().equals("Products")){
                    Intent intent=new Intent(context,productSeller.class);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewing extends RecyclerView.ViewHolder {
        TextView dashitem, dashquantity;
        CardView cardView;

        public Viewing(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.card);
            dashitem = itemView.findViewById(R.id.item);
            dashquantity = itemView.findViewById(R.id.quantity);
        }
    }
}
