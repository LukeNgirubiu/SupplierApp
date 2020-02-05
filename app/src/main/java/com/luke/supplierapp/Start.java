package com.luke.supplierapp;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.Manifest;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


import java.security.Permission;

public class Start extends AppCompatActivity {
    public sqlite localdb;
     private com.google.firebase.firestore.FirebaseFirestore firestore;
      private DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localdb=new sqlite(this);
        firestore= FirebaseFirestore.getInstance();
        ConnectivityManager connect=(ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connect.getActiveNetworkInfo();
        boolean network=networkInfo!=null&&networkInfo.isAvailable()&&networkInfo.isConnected();
        if(network==true){

            int row=localdb.CountRows();
            if(row==1){
                reference = firestore.collection("usersDetails").document(localdb.getUser());
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       userDetails users=documentSnapshot.toObject(userDetails.class);
                        if(users.getType()==1){
                            Intent intent=new Intent(getBaseContext(),Homes.class);
                            startActivity(intent);
                            finishAffinity();

                        }
                        if(users.getType()==2){
                            Intent intent=new Intent(getBaseContext(),Homec.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                });

            }
            else {
                Intent intent=new Intent(this,checkContact.class);
                startActivity(intent);

            }
        }
        else {
            setContentView(R.layout.activity_start);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
