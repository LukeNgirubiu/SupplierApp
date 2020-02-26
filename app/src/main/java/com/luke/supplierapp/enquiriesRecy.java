package com.luke.supplierapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Calendar.getInstance;

public class enquiriesRecy extends RecyclerView.Adapter<enquiriesRecy.Viewing> {
    private List<setEnqChat> list;
    private Context context;


    public enquiriesRecy(List<setEnqChat> list,Context context) {
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
        final setEnqChat enq = list.get(position);
        final sqlite st = new sqlite(context);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firestore.collection("Users");
DocumentReference getuser=reference.document(enq.getUsedId());
getuser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        userDetails users=documentSnapshot.toObject(userDetails.class);
        holder.first_Name.setText(users.getFirstName());
        holder.second_Name.setText(users.getSecondName());
        Picasso.get().load(users.getImagePath()).into(holder.image);
    }
});
        Calendar calendar = getInstance();
        calendar.setTime(enq.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        Calendar calendar1 = getInstance();
        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String[] monthsNames = {"January", " February", "March", "April", "May", "June", "July", "August", "September", "Octomber", "November", "December"};
        if (year == year1) {
            if (month == month1) {
                if (day == day1) {
                    String h = Integer.toString(hour);
                    String m = Integer.toString(minute);
                    holder.chattime.setText("Today" + " " + h + ":" + m);
                } else if (day == day1 - 1) {
                    String h = Integer.toString(hour);
                    String m = Integer.toString(minute);
                    holder.chattime.setText("Yesterday" + " " + h + ":" + m);
                } else {
                    String h = Integer.toString(month);
                    holder.chattime.setText("This month " + "on " + day + "th");
                }

            } else {
                String h = Integer.toString(day);
                holder.chattime.setText(h + " " + monthsNames[month] + " " + year);
            }

        } else {

            String h = Integer.toString(day);
            holder.chattime.setText(h + " " + monthsNames[month] + " " + year);
        }
         CollectionReference collectionReference = firestore.collection("Cart").
                document(st.getUser()).collection("cart");
        collectionReference.whereEqualTo("sellerId",enq.getUsedId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        holder.count.setText(Integer.toString(queryDocumentSnapshots.size()));
                    }
                });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,cartProducts.class);
                intent.putExtra("Id",enq.getUsedId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewing extends RecyclerView.ViewHolder {
        CircleImageView image;
        CardView card;
        TextView first_Name, second_Name, count, chattime;

        public Viewing(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile);
            first_Name = itemView.findViewById(R.id.firstName);
            second_Name = itemView.findViewById(R.id.secondName);
            count = itemView.findViewById(R.id.count);
            chattime = itemView.findViewById(R.id.date);
            card=itemView.findViewById(R.id.cards);

        }
    }
}
