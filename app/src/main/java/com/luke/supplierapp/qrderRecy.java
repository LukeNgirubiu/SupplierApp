package com.luke.supplierapp;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import static java.util.Calendar.getInstance;
public class qrderRecy extends RecyclerView.Adapter<qrderRecy.Viewer> {
    private Context context;
    private List<setQrders> orderlist;
    private String userType;

    public qrderRecy(Context context, List<setQrders> list, String userType) {
        this.context = context;
        this.orderlist = list;
        this.userType = userType;
    }

    @NonNull
    @Override
    public Viewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orderlayout, parent, false);
        return new Viewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewer holder, final int position) {
        final setQrders orders = orderlist.get(position);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (userType.equals("customerId")) {
                    sqlite se = new sqlite(context);
                    final PopupMenu pop = new PopupMenu(context, holder.cardView);
                    pop.inflate(R.menu.removeorder);
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int pressedItem=item.getItemId();
                            switch (pressedItem) {
                                case R.id.remove:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete the Order");
                                    builder.setMessage("Continue with deleting this order?");
                                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                            final DocumentReference reference = firestore.collection("Orders").document(orders.getOrderId());
                                            reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position,orderlist.size());
                                                    Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    break;
                            }
                            return false;
                        }
                    });
                    pop.show();

                } return true;}
        });
        holder.phonecontact.setText("Ksh " + Long.toString(orders.getProductPrice()));
        if (userType.equals("customerId")) {
            DocumentReference reference = firestore.collection("Users").document(orders.getSupplierId());
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userDetails details = documentSnapshot.toObject(userDetails.class);
                    Picasso.get().load(details.getImagePath()).into(holder.circleImageView);
                    holder.sdName.setText(details.getSecondName());
                    holder.frName.setText(details.getFirstName());
                }
            });
            holder.seen.setText("");
        } else {
            DocumentReference reference = firestore.collection("Users").document(orders.getCustomerId());
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userDetails details = documentSnapshot.toObject(userDetails.class);
                    Picasso.get().load(details.getImagePath()).into(holder.circleImageView);
                    holder.sdName.setText(details.getSecondName());
                    holder.frName.setText(details.getFirstName());
                }
            });
            if (orders.isSeen()) {
                holder.seen.setText("");
            } else {
                holder.seen.setText("New");
            }
        }
        Calendar calendar = getInstance();
        calendar.setTime(orders.getDate());
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        Calendar calendar1 = getInstance();
        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DATE);
        int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int min1 = calendar1.get(Calendar.MINUTE);

        String[] monthsNames = {"January", " February", "March", "April", "May", "June", "July", "August", "September", "Octomber", "November", "December"};
        if (year == year1) {
            if (month == month1) {
                if (day == day1) {
                    String h = Integer.toString(hour);
                    String m = Integer.toString(minute);
                    if ((day == day1) && (hour == hour1) && (minute == min1)) {
                        holder.ordertime.setText("Now");
                    } else {
                        if (minute < 10) {
                            holder.ordertime.setText("Today" + " " + h + ":" + "0" + m);
                        } else {
                            holder.ordertime.setText("Today" + " " + h + ":" + m);
                        }
                    }
                } else if (day == day1 - 1) {
                    String h = Integer.toString(hour);
                    String m = Integer.toString(minute);
                    if (minute < 10) {
                        holder.ordertime.setText("Yesterday" + " " + h + ":" + "0" + m);
                    } else {
                        holder.ordertime.setText("Yesterday" + " " + h + ":" + m);
                    }
                } else {
                    String h = Integer.toString(month);
                    holder.ordertime.setText("This month " + "on " + day + "th");
                }

            } else {
                String h = Integer.toString(day);
                holder.ordertime.setText(h + " " + monthsNames[month] + " " + year);
            }

        } else {

            String h = Integer.toString(day);
            holder.ordertime.setText(h + " " + monthsNames[month] + " " + year);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productsOrder;
                productsOrder = new Intent(context, Products.class);
                productsOrder.putExtra("orderId", orders.getOrderId());
                context.startActivity(productsOrder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderlist.size();
    }


    public class Viewer extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        CardView cardView;
        TextView phonecontact, sdName, frName, seen, ordertime;

        public Viewer(View itemView) {
            super(itemView);
            phonecontact = itemView.findViewById(R.id.cost);
            sdName = itemView.findViewById(R.id.secName);
            frName = itemView.findViewById(R.id.firstName);
            cardView = itemView.findViewById(R.id.cards);
            circleImageView = itemView.findViewById(R.id.profImage);
            seen = itemView.findViewById(R.id.seen);
            ordertime = itemView.findViewById(R.id.dayAndTime);
        }
    }
}
