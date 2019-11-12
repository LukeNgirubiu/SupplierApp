package com.luke.supplierapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class imageShowRecy extends RecyclerView.Adapter<imageShowRecy.Veiwer> {
    Context context;
    List<Images> image;
    String userId;

    public imageShowRecy(Context context, List<Images> image, String userId) {
        this.context = context;
        this.image = image;
        this.userId = userId;
    }

    @NonNull
    @Override
    public Veiwer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_frame, parent, false);
        return new Veiwer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Veiwer holder, final int position) {
        final Images img = image.get(position);
        Picasso.get().load(img.getImageString()).into(holder.imgs);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              sqlite sql=new sqlite(context);
              if(sql.getUser().equals(userId)){
                  AlertDialog.Builder builder = new AlertDialog.Builder(context);
                  builder.setTitle("Deleting image");
                  builder.setMessage("Delete this image?");
                  builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          FirebaseFirestore firestore=FirebaseFirestore.getInstance();
                          CollectionReference refer = firestore.collection("Productimges").
                                  document(userId)
                                  .collection(img.getProductId());
                          DocumentReference dr=refer.document(img.getImageString());
                          dr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  Toast.makeText(context,"Deleted successful",Toast.LENGTH_SHORT).show();
                              }
                          });
                          image.remove(position);
                          notifyItemRemoved(position);
                          notifyItemRangeChanged(position, image.size());


                      }
                  });
                  builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                      }
                  });
                  AlertDialog alert=builder.create();
                  alert.show();

              }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return image.size();
    }

    public class Veiwer extends RecyclerView.ViewHolder {
        ImageView imgs;
        CardView cardView;

        public Veiwer(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardImage);
            imgs = itemView.findViewById(R.id.prodImage);
        }
    }
}
