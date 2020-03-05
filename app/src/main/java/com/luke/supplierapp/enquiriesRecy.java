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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.getInstance;

public class enquiriesRecy extends RecyclerView.Adapter<enquiriesRecy.Viewing> {
    private List<setEnqChat> list;
    private Context context;


    public enquiriesRecy(List<setEnqChat> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viw = LayoutInflater.from(context).inflate(R.layout.enquirechat, parent, false);
        return new Viewing(viw);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewing holder, int position) {
        final setEnqChat users = list.get(position);
        sqlite sql=new sqlite(context);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firestore.collection("Users").document(users.getUsedId());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails users = documentSnapshot.toObject(userDetails.class);
                holder.first_Name.setText(users.getFirstName());
            }
        });
         CollectionReference collectionReference = firestore.collection("Cart").
                document(sql.getUser()).collection(users.getUsedId());
         collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 if(queryDocumentSnapshots.size()!=0){
                     holder.numberOfProducts.setText(Integer.toString(queryDocumentSnapshots.size()));
                     Long totalAmount= Long.valueOf(0);
                 for(QueryDocumentSnapshot query:queryDocumentSnapshots){
                     cartProductSet cart=query.toObject(cartProductSet.class);
                     totalAmount=totalAmount+cart.getTotal();
                 }
                 holder.totalAmount.setText(Long.toString(totalAmount));
             }}
         });
        Calendar calendar = getInstance();
        calendar.setTime(users.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        Calendar calendar1 = getInstance();
        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DATE);
        int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int minute1 = calendar1.get(Calendar.MINUTE);
        String[] monthsNames = {"January", " February", "March", "April", "May", "June", "July", "August", "September", "Octomber", "November", "December"};
        if (year == year1) {
            if (month == month1) {
                if (day == day1) {
                    String h = Integer.toString(hour);
                    String m = Integer.toString(minute);
                    if ((day == day1) && (hour == hour1) && (minute == minute1)) {
                        holder.time.setText("Now");
                    } else {
                        if (minute < 10) {
                            holder.time.setText("Today" + " " + h + ":" + "0" + m);
                        } else {
                            holder.time.setText("Today" + " " + h + ":" + m);
                        }
                    }
                } else if (day == day1 - 1) {
                    String h = Integer.toString(hour);
                    String m = Integer.toString(minute);
                    if (minute < 10) {
                        holder.time.setText("Yesterday" + " " + h + ":" + "0" + m);
                    } else {
                        holder.time.setText("Yesterday" + " " + h + ":" + m);
                    }
                } else {
                    String h = Integer.toString(month);
                    holder.time.setText("This month " + "on " + day + "th");
                }

            } else {
                String h = Integer.toString(day);
                holder.time.setText(h + " " + monthsNames[month] + " " + year);
            }

        } else {

            String h = Integer.toString(day);
            holder.time.setText(h + " " + monthsNames[month] + " " + year);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, cartProducts.class);
                intent.putExtra("Id",users.getUsedId() );
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewing extends RecyclerView.ViewHolder {

        TextView time,first_Name,totalAmount,numberOfProducts;
        CardView cardView;

        public Viewing(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            first_Name = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.cards);
            totalAmount=itemView.findViewById(R.id.productsValue);
            numberOfProducts=itemView.findViewById(R.id.productNumber);
        }
    }
}
