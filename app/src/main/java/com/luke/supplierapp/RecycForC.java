package com.luke.supplierapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RecycForC extends RecyclerView.Adapter<RecycForC.Viewing> {
private Context context;
private List<prepareC> list;

    public RecycForC(Context context, List<prepareC> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Viewing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.customerview,parent,false);
        return new Viewing(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewing holder, int position) {
        final prepareC name=list.get(position);
        FirebaseFirestore dbreference=FirebaseFirestore.getInstance();
        sqlite sqlit=new sqlite(context);
        holder.homeItem.setText(name.getCategory());
   if(name.getCategory().equals("My orders")){
    CollectionReference documentReference=dbreference.collection("Orders");
   documentReference.whereEqualTo("buyerId",sqlit.getUser()).whereEqualTo("seen",false).get().
           addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
        int number=queryDocumentSnapshots.size();
        if(number>1){
        holder.quantity.setText(Integer.toString(number));}

    }
});
}
if(name.getCategory().equals("Carts")){
    CollectionReference dc=dbreference.collection("Carting").document(sqlit.getUser())
            .collection("cart");
    dc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
         int number=queryDocumentSnapshots.size();
         if(number>0){
             holder.quantity.setText(Integer.toString(number));
         }
        }
    });
}

holder.cardView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent;
        if(name.getCategory().equals("Suppliers")){
             intent=new Intent(context,customerSupplier.class);
             intent.putExtra("Determiner",1);
             context.startActivity(intent);
        }
        if(name.getCategory().equals("My orders")){
            intent=new Intent(context,Qrders.class);
            intent.putExtra("userType","customerId");
            context.startActivity(intent);
        }
        if(name.getCategory().equals("Categories")){
            intent=new Intent(context,category.class);
            intent.putExtra("dec",1);
            context.startActivity(intent);
        }
        if(name.getCategory().equals("Carts")){
            intent=new Intent(context,Enquire.class);
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
        TextView homeItem,quantity;
        CardView cardView;
        public Viewing(android.view.View itemView) {
            super(itemView);
            homeItem=itemView.findViewById(R.id.item);
            quantity=itemView.findViewById(R.id.quantity);
            cardView=itemView.findViewById(R.id.card);
        }
    }
}
