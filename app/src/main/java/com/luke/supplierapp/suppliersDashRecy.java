package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class suppliersDashRecy extends RecyclerView.Adapter<suppliersDashRecy.Veiwing> {
    private Context context;
    private List<prepareC> listing;
private BroadcastReceiver broadcastReceiver;
    public suppliersDashRecy(Context context, List<prepareC> listing) {
        this.context = context;
        this.listing = listing;
    }

    @NonNull
    @Override
    public Veiwing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.customerview,parent,false);
        return new Veiwing(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Veiwing holder, int position) {
         final prepareC name=listing.get(position);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        sqlite sql=new sqlite(context);
        String user=sql.getUser();
        holder.homeItem.setText(name.getCategory());
        if (name.getCategory().equals("Products")) {

           CollectionReference reference = firestore.collection("Products").document(sql.getUser()).collection("products");
            reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int number = queryDocumentSnapshots.size();
                    if (number > 0) {
                        holder.quantity.setText(Integer.toString(number));
                    } else {
                        holder.quantity.setText("");
                    }
                }
            });
        }
        if (name.getCategory().equals("Subsriptions")) {
            CollectionReference subscription = firestore.collection("Subscription").document(sql.getUser()).collection("subscription");
            subscription.whereEqualTo("seen",false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int number = queryDocumentSnapshots.size();
                    if (number > 0) {
                        holder.quantity.setText(Integer.toString(number));
                    } else {
                        holder.quantity.setText("");
                    }
                }
            });
        }
        if(name.getCategory().equals("Orders")){
            CollectionReference orders=firestore.collection("Orders").document(sql.getUser()).collection("order");
            orders.whereEqualTo("supplierId",sql.getUser()).whereEqualTo("seen",false).whereEqualTo("seller",0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int number=queryDocumentSnapshots.size();
                    if (number > 0) {
                        holder.quantity.setText(Integer.toString(number));
                    } else {
                        holder.quantity.setText("");
                    }
                }
            });
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getCategory().equals("Orders")){
                    Intent send=new Intent(context,Qrders.class);
                    send.putExtra("userType","supplierId");
                    context.startActivity(send);
                }
                if (name.getCategory().equals("Subscriptions")) {
                    Intent notify=new Intent(context,Notifiying.class);
                    context.startActivity(notify);
                }

                if(name.getCategory().equals("Products")){
                    Intent intent=new Intent(context,productSeller.class);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return listing.size();
    }

    public class Veiwing extends RecyclerView.ViewHolder {
        TextView homeItem,quantity;
        CardView cardView;
        public Veiwing(View itemView) {
            super(itemView);
            homeItem=itemView.findViewById(R.id.item);
            quantity=itemView.findViewById(R.id.quantity);
            cardView=itemView.findViewById(R.id.card);
        }
    }
}
