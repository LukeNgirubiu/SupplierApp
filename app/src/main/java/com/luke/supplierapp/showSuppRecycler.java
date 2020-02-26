package com.luke.supplierapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class showSuppRecycler extends RecyclerView.Adapter<showSuppRecycler.Viewer> {
    List<userDetails> details;
    Context context;
    String categ;

    public showSuppRecycler(List<userDetails> details, Context context, String categ) {
        this.details = details;
        this.context = context;
        this.categ = categ;
    }

    @NonNull
    @Override
    public Viewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.suppliercard, parent, false);
        return new Viewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewer holder, int position) {
        final userDetails detail = details.get(position);
        holder.firstName.setText(detail.getFirstName());
        holder.secondName.setText(detail.getSecondName());
        holder.contact.setText(detail.getContact());
        Picasso.get().load(detail.getImagePath()).into(holder.circleImageView);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,productsShow.class);
                intent.putExtra("Id",detail.getId());
                intent.putExtra("Category",categ);
                context.startActivity(intent);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,holder.cardView);
                popupMenu.inflate(R.menu.subenq);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.subscribe:
                                FirebaseFirestore fire=FirebaseFirestore.getInstance();
                                sqlite sql=new sqlite(context);
                                final CollectionReference collect=fire.collection("Subscription").document(detail.getId()).collection("subscription");
                                final setSubsriptions subsriptions=new setSubsriptions();
                                subsriptions.setBuyerId(sql.getUser());
                                subsriptions.setSeen(false);
                                collect.whereEqualTo("buyerId",sql.getUser()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(queryDocumentSnapshots.size()>0){
                                            Toast.makeText(context,"You are already subscribed",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            collect.add(subsriptions).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(context,"Subscription successful",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                                break;
                            case R.id.unsubscribe:
                                FirebaseFirestore fir=FirebaseFirestore.getInstance();
                                sqlite sq=new sqlite(context);
                                final CollectionReference collec=fir.collection("Subscription").document(detail.getId()).collection("subscription");
                                collec.whereEqualTo("buyerId",sq.getUser()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(queryDocumentSnapshots.size()>0){
                                            for (QueryDocumentSnapshot query:queryDocumentSnapshots){
                                                DocumentReference doc=collec.document(query.getId());
                                                doc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(context,"Unsubscription successful",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();;
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public class Viewer extends RecyclerView.ViewHolder {
        TextView firstName, secondName, contact;
        CircleImageView circleImageView;
        CardView cardView;

        public Viewer(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cards);
            firstName = itemView.findViewById(R.id.firstName);
            secondName = itemView.findViewById(R.id.secondName);
            contact = itemView.findViewById(R.id.contact);
            circleImageView = itemView.findViewById(R.id.profile);
        }
    }
}
