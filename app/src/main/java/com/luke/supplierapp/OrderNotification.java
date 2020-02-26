package com.luke.supplierapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class OrderNotification extends Service {
    private CollectionReference reference, referenc;
    private FirebaseFirestore firestore;
    private String fname, sname;
    private Double Altitude,longitude,latitude;
    private List<setQrders> list;
    private BroadcastReceiver broadcastReceiver;
    private DocumentReference collect;
    public static final String CHANNEL_ID = "SJTE19";
    public static final String CHANNEL_NAME = "Suppliers";
    public static final String CHANNEL_DESCRIPTION = "Supplier describe";
    @Override
    public void onCreate() {
        super.onCreate();
        list=new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        referenc = firestore.collection("usersDetails");
        reference = firestore.collection("Orders");
        sqlite sql = new sqlite(getBaseContext());
        reference.whereEqualTo("supplierId", sql.getUser()).whereEqualTo("notified", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    setQrders odr = documentChange.getDocument().toObject(setQrders.class);
                    odr.setOrderId(documentChange.getDocument().getId());
                    switch (documentChange.getType()) {

                        case ADDED:
                            referenc.document(odr.getCustomerId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    userDetails users = documentSnapshot.toObject(userDetails.class);
                                    fname = users.getFirstName();
                                    sname = users.getSurName();
                                }
                            });
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                                    .setContentTitle("Order from" + " " + fname + " " + sname)
                                    .setContentText("Worth " + "Ksh  " +Long.toString(odr.getProductPrice()))
                                    .setSmallIcon(R.drawable.cart1)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                            Intent intent = new Intent(getBaseContext(), Qrders.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
                            builder.setContentIntent(pendingIntent);
                            Notification notification=builder.build();
                            NotificationManagerCompat compat=NotificationManagerCompat.from(getBaseContext());
                            int time= (int) System.currentTimeMillis();
                            compat.notify(time,notification);
                            break;
                    }

                }

            }
        });

    }

    private Notification notifying() {
        if (Build.VERSION.SDK_INT > 25) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manage;
            manage = getSystemService(NotificationManager.class);
            channel.setDescription(CHANNEL_DESCRIPTION);
            manage.createNotificationChannel(channel);
            Notification.Builder build = new Notification.Builder(getApplicationContext(), CHANNEL_NAME);
            return build.build();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
