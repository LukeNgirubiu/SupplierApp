package com.luke.supplierapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Respond extends AppCompatActivity {
   private Toolbar toolbar;
private EditText response;
private ImageButton send;
private Double lat,longi;
private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond);
        toolbar=findViewById(R.id.toolbar);
        response=findViewById(R.id.response);
        send=findViewById(R.id.submit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Response");
        toolbar.setTitleTextColor(Color.WHITE);
        Bundle bundle=getIntent().getExtras();
        orderId=bundle.getString("id");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore dbreference=FirebaseFirestore.getInstance();
                final DocumentReference documentReference=dbreference.collection("Orders").document(orderId);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        setQrders orders=documentSnapshot.toObject(setQrders.class);
                       CollectionReference collect= dbreference.collection("Users");
                       collect.whereEqualTo("contact",orders.getBuyer()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                           @Override
                           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                               for(QueryDocumentSnapshot query:queryDocumentSnapshots){
                                   userDetails user=query.toObject(userDetails.class);
                                   lat=user.getLatitude();
                                   longi=user.getLongitude();
                                   try{
                                       SmsManager sendingSMs=SmsManager.getDefault();
                                       sendingSMs.sendTextMessage(user.getContact(),null,response.getText().toString().trim(),null,null);
                                       Toast.makeText(getBaseContext(),"Sent",Toast.LENGTH_SHORT).show();
                                       Intent back=new Intent(getApplicationContext(),Homes.class);
                                       startActivity(back);
                                   }
                                   catch (Exception y){
                                       y.printStackTrace();
                                       Intent back=new Intent(getApplicationContext(),Homes.class);
                                       startActivity(back);
                                       Toast.makeText(getBaseContext(),"Failed to send",Toast.LENGTH_SHORT).show();

                                   }
                               }
                           }
                       });

                    }
                });

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.directions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.direction){//get the location of the buyer using the Map app
            String longit=Double.toString(longi);
            String lati=Double.toString(lat);
            String coordinate="google.navigation:q="+lati+","+longit;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(coordinate));
            //"google.navigation:q=0.126455,37.721175"
            intent.setPackage("com.google.android.apps.maps");
            if(intent.resolveActivity(getApplicationContext().getPackageManager())!=null){
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
