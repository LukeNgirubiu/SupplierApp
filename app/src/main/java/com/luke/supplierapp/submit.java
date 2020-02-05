package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class submit extends AppCompatActivity {
Toolbar toolbar;
Button button,send;
private String firstname,secondname,surname,contact,path;
private int type;

FirebaseAuth authenticate;
private String userId;
private DocumentReference reference, contacts,referenc;
    private StorageReference storage;
    private Double Longitude, Latitude;
userDetails users,user1;
FirebaseFirestore firestore;
private Uri profilePath;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Longitude = intent.getDoubleExtra("Longi", 0);
                    Latitude = intent.getDoubleExtra("Latit", 0);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("Locations"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        toolbar=findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
        send=findViewById(R.id.send);
        button=findViewById(R.id.getimage);
        users = new userDetails();
        user1=new userDetails();
        firestore = FirebaseFirestore.getInstance();
        Bundle bundles=getIntent().getExtras();
        firstname=bundles.getString("FIRSTNAME");
        secondname=bundles.getString("SECONDNAME");
        surname=bundles.getString("THIRDNAME");
        contact=bundles.getString("Contact");
        type=bundles.getInt("USERTYPE");
         userId=bundles.getString("Id");
        reference=firestore.collection("Particulars").document(userId);
        referenc=firestore.collection("Doc").document("doc");
        storage = FirebaseStorage.getInstance().getReference("profiles");
        contacts=firestore.collection("Contacts").document(userId);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profilePath!=null){
                    Toast.makeText(getBaseContext(),"Please wait...",Toast.LENGTH_LONG).show();
                    final StorageReference store = storage.child(System.currentTimeMillis() + "." + getPathExtension(profilePath));
                    UploadTask uploadTask = store.putFile(profilePath);
                    store.putFile(profilePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return store.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            path=task.getResult().toString();
                            users.setContact(contact);
                            users.setFirstName(firstname);
                            users.setSecondName(secondname);
                            users.setSurName(surname);
                            users.setType(type);
                            users.setImagePath(path);
                            users.setLatitude(Latitude);
                            users.setLongitude(Longitude);
                            reference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   sqlite sql = new sqlite(getApplicationContext());
                                   long s=sql.addId(userId);
                                   if(s!=-1){
                                       CollectionReference coll = firestore.collection("Cellphone");
                                       DocumentReference cont = firestore.collection("Contacts").document(userId);
                                       cellContacts contacts = new cellContacts();
                                       contacts.setCellNo(contact);
                                       cont.set(contacts);
                                       contacts conts = new contacts();
                                       conts.setUserId(userId);
                                       conts.setContact(contact);
                                       coll.add(conts);
                                       user1.setContact(contact);
                                       user1.setFirstName(firstname);
                                       user1.setSecondName(secondname);
                                       user1.setSurName(surname);
                                       user1.setType(type);
                                       user1.setImagePath(path);
                                       user1.setLatitude(Latitude);
                                       user1.setLongitude(Longitude);
                                       referenc.set(user1);
                                       //Toast.makeText(getApplicationContext(),"Registration successful"+Double.toString(Latitude),Toast.LENGTH_SHORT).show();
                                       if (type == 1) {
                                           Intent intent = new Intent(getBaseContext(), Homes.class);
                                           startActivity(intent);
                                           Intent startservice = new Intent(submit.this, OrderNotification.class);
                                           startService(startservice);
                                           finishAffinity();
                                       } else {
                                           Intent intent = new Intent(getBaseContext(), Homec.class);
                                           startActivity(intent);
                                           finishAffinity();
                                       }
                                   }
                                   else{
                                       Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_LONG).show();
                                   }

                               }
                           });

                        }

                    });
                }
            }
        });
    }
    public void profileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePath = data.getData();
        }
    }

    private String getPathExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }

    }
}

