package com.luke.supplierapp;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Calendar.getInstance;

public class enquiriesRecy extends RecyclerView.Adapter<enquiriesRecy.Viewing> {
    private List<setEnquire> list;
    private Context context;
    String imagepath;

    public enquiriesRecy(List<setEnquire> list, Context context) {
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
        setEnquire enq = list.get(position);
        sqlite st = new sqlite(context);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firestore.collection("usersDetails");
        DocumentReference dc = reference.document(enq.getFromId());
        dc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails us = documentSnapshot.toObject(userDetails.class);
                imagepath = us.getImagePath();
                holder.lastN.setText(us.getSurName());
            }
        });
        if (!st.getUser().equals(enq.getFromId())) {
            Picasso.get().load(imagepath).into(holder.image);
        }
        holder.chatmessage.setText(enq.getMessage());
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
                    holder.chattime.setText("This month " + "on " + day+ "th");
                }

            } else {
                String h = Integer.toString(day);
                holder.chattime.setText(h + " " + monthsNames[month] + " " + year);
            }

        } else {

            String h = Integer.toString(day);
            holder.chattime.setText(h + " " + monthsNames[month] + " " + year);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewing extends RecyclerView.ViewHolder {
        CircleImageView image;
        RelativeLayout chatlayout;
        TextView lastN, chattime, chatmessage;

        public Viewing(View itemView) {
            super(itemView);
            chatlayout = itemView.findViewById(R.id.chatLayout);
            image = itemView.findViewById(R.id.image);
            lastN = itemView.findViewById(R.id.lastName);
            chattime = itemView.findViewById(R.id.time);
            chatmessage = itemView.findViewById(R.id.text);
        }
    }
}
