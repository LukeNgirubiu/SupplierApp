package com.luke.supplierapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class enquiriRecy extends RecyclerView.Adapter<enquiriRecy.Veiwing> {
    private List<setEnqChat> chat;
    private Context context;

    public enquiriRecy(List<setEnqChat> chat, Context context) {
        this.chat = chat;
        this.context = context;
    }

    @NonNull
    @Override
    public Veiwing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatitemlayout, parent, false);
        return new Veiwing(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Veiwing holder, int position) {
        final setEnqChat cha=chat.get(position);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        holder.itemlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Enquire.class);
                intent.putExtra("user",cha.getUsedId());
                context.startActivity(intent);
            }
        });
        CollectionReference reference = firestore.collection("Users");
        DocumentReference doc=reference.document(cha.getUsedId());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails user=documentSnapshot.toObject(userDetails.class);
                holder.userContact.setText(user.getContact());
                holder.userName.setText(user.getSurName());
                Picasso.get().load(user.getImagePath()).into(holder.circle);
            }
        });
        Calendar calendar = getInstance();
        calendar.setTime(cha.getDate());
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
                    holder.userTime.setText("Today" + " " + h + ":" + m);
                } else if (day == day1 - 1) {
                    String h = Integer.toString(hour);
                    String m = Integer.toString(minute);
                    holder.userTime.setText("Yesterday" + " " + h + ":" + m);
                } else {
                    String h = Integer.toString(month);
                    holder.userTime.setText("This month " + "on " + day+ "th");
                }

            } else {
                String h = Integer.toString(day);
                holder.userTime.setText(h + " " + monthsNames[month] + " " + year);
            }

        } else {

            String h = Integer.toString(day);
            holder.userTime.setText(h + " " + monthsNames[month] + " " + year);
        }
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class Veiwing extends RecyclerView.ViewHolder {
        CircleImageView circle;
        TextView userName, userContact, userTime;
        RelativeLayout itemlayout;

        public Veiwing(View itemView) {
            super(itemView);
            circle = itemView.findViewById(R.id.profile);
            userName = itemView.findViewById(R.id.name);
            userContact = itemView.findViewById(R.id.contact);
            userTime = itemView.findViewById(R.id.time);
            itemlayout = itemView.findViewById(R.id.itemlayout);
        }
    }
}
