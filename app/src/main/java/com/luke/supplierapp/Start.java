package com.luke.supplierapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.security.Permission;

public class Start extends AppCompatActivity {
    public sqlite localdb;
    private FirebaseFirestore firestore;
    private DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPermissions();
        localdb = new sqlite(this);
        firestore = FirebaseFirestore.getInstance();
        ConnectivityManager connect = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connect.getActiveNetworkInfo();
        boolean network = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        if (network == true) {

            int row = localdb.CountRows();
            if (row == 1) {
                final ProgressDialog progressDialog = new ProgressDialog(Start.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                reference = firestore.collection("Users").document(localdb.getUser());
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Intent intentl = new Intent(getApplicationContext(), location2.class);
                            startService(intentl);
                            userDetails users = documentSnapshot.toObject(userDetails.class);
                            if (users.getType() == 1) {
                                Intent startservice = new Intent(Start.this, OrderNotification.class);
                                startService(startservice);
                                Intent intent = new Intent(getBaseContext(), Homes.class);
                                startActivity(intent);
                                finishAffinity();
                                progressDialog.dismiss();
                            }
                            if (users.getType() == 2) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(getBaseContext(), Homec.class);
                                startActivity(intent);
                                finishAffinity();

                            }
                        } else {
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                Intent intent = new Intent(this, checkContact.class);
                startActivity(intent);
                finishAffinity();

            }
        } else {

            //\ Toast.makeText(getApplicationContext(),localdb.getUser(),Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_start);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private boolean locationPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 20);

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                locationPermissions();
            }

        }
    }
}
