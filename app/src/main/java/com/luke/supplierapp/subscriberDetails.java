package com.luke.supplierapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class subscriberDetails extends RecyclerView.Adapter<subscriberDetails.Viewing> {
    private List<setSubsriptions> list;
    private Context context;


    public subscriberDetails(List<setSubsriptions> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public Viewing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscriberdatails, parent, false);
        return new Viewing(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewing holder, int position) {
        final setSubsriptions users = list.get(position);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        if (users.isSeen()==false){
            holder.seenS.setText("New");
        }
        else {
            holder.seenS.setText("");
        }
         DocumentReference coll=firestore.collection("usersDetails").document(users.getBuyerId());
        coll.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails us=documentSnapshot.toObject(userDetails.class);
                Picasso.get().load(us.getImagePath()).into(holder.imageP);
                holder.firstN.setText(us.getFirstName());
                holder.secondN.setText(us.getSecondName());
                holder.Contact.setText(us.getContact());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewing extends RecyclerView.ViewHolder {
        TextView firstN, secondN, Contact, seenS;
        CircleImageView imageP;

        public Viewing(View itemView) {
            super(itemView);
            firstN = itemView.findViewById(R.id.fName);
            secondN = itemView.findViewById(R.id.sName);
            Contact = itemView.findViewById(R.id.phonecontact);
            imageP = itemView.findViewById(R.id.profImage);
            seenS = itemView.findViewById(R.id.seen);
        }
    }
}
