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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class categoryRecy extends RecyclerView.Adapter<categoryRecy.Viewing> {
    List<categoryItems> list;
    Context context;
    int number;
    int dec;

    public categoryRecy(List<categoryItems> list, Context context, int dec) {
        this.list = list;
        this.context = context;
        this.dec = dec;
    }

    @NonNull
    @Override
    public Viewing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categoryitems, parent, false);
        return new Viewing(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewing holder, int position) {
        final categoryItems item = list.get(position);
        holder.categoryName.setText(item.getCategoryName());
        if (dec == 1) {
            FirebaseFirestore referece = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = referece.collection("Category").document(item.getCategoryId()).collection("category");
            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot queries = task.getResult();
                    number = queries.size();
                    if (number > 0) {
                        holder.numberOfSuppliers.setText(Integer.toString(number) + " suppliers");
                    } else {
                        holder.numberOfSuppliers.setText("Suppliers not availabe");
                    }

                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (number > 0) {
                        Intent intent = new Intent(context, customerSupplier.class);
                        intent.putExtra("Determiner", 2);
                        intent.putExtra("categoryId", item.getCategoryId());
                        context.startActivity(intent);
                    }
                }
            });
        }
        else{
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent add=new Intent(context,addProduct.class);
                    add.putExtra("categoryId",item.getCategoryId());
                    add.putExtra("categoryName",item.getCategoryName());
                    context.startActivity(add);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewing extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView categoryName, numberOfSuppliers;

        public Viewing(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            categoryName = itemView.findViewById(R.id.category);
            numberOfSuppliers = itemView.findViewById(R.id.numberSupp);
        }
    }
}
