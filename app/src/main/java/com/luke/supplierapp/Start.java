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

        locationPermissions();
        boolean network=networkInfo!=null&&networkInfo.isAvailable()&&networkInfo.isConnected();
        if(network==true){
            locationPermissions();

            int row=localdb.CountRows();
            if(row==1){
                Toast.makeText(this,"Available",Toast.LENGTH_LONG).show();
                reference = firestore.collection("usersDetails").document(localdb.getUser());
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       userDetails users=documentSnapshot.toObject(userDetails.class);
                        if(users.getType()==1){
                            Intent intent=new Intent(getBaseContext(),Homes.class);
                            startActivity(intent);
                            Intent startservice=new Intent(Start.this,OrderNotification.class);
                            startService(startservice);
                        }
                        else{
                            Intent intent=new Intent(getBaseContext(),Homec.class);
                            startActivity(intent);
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
    private boolean locationPermissions(){
        if(Build.VERSION.SDK_INT>=23&& ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},20);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==20){
            if(!(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                locationPermissions();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
