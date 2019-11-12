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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Registration extends AppCompatActivity {
    private EditText firstName, secondName, surName;
    private Button sendingDetails, getImage;
    private int userType;
    private Uri profilePath;
    private Double Longitude, Latitude;
    private RadioGroup radioGroup;
    private FirebaseFirestore firestore;
    private CollectionReference reference;
    private StorageReference storage;
    private String cellno;
    private Toolbar toolbar;
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
        setContentView(R.layout.activity_registration);
        userType = 0;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
        secondName = findViewById(R.id.secondName);
        surName = findViewById(R.id.surName);
        firstName = findViewById(R.id.firstName);
        sendingDetails = findViewById(R.id.sendingDetails);
        getImage = findViewById(R.id.getImage);
        radioGroup = findViewById(R.id.radioGroup);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference("profiles");
        reference = firestore.collection("usersDetails");
        Bundle bundle = getIntent().getExtras();
        cellno = bundle.getString("Contact");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.type1:
                        userType = 1;
                        break;
                    case R.id.type2:
                        userType = 2;
                        break;
                }
            }
        });
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage();
            }
        });
        sendingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((firstName.getText().toString().length() <= 2) && (secondName.getText().toString().length() <= 3) && (surName.getText().toString().length() <= 3) && (userType != 0) && (profilePath != null)) {

                    final StorageReference storing = storage.child(System.currentTimeMillis() + "." + getPathExtension(profilePath));
                    UploadTask uploadTask = storing.putFile(profilePath);
                    storing.putFile(profilePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return storing.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                userDetails users = new userDetails();
                                users.setFirstName(firstName.getText().toString());
                                users.setSecondName(secondName.getText().toString());
                                users.setSurName(surName.getText().toString());
                                users.setLatitude(Latitude);
                                users.setLongitude(Longitude);
                                users.setType(userType);
                                users.setContact(cellno);
                                users.setImagePath(task.getResult().toString());

                                reference.add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        sqlite sql = new sqlite(getApplicationContext());
                                        sql.addId(documentReference.getId());
                                        CollectionReference coll = firestore.collection("Cellphone");
                                        CollectionReference collect = firestore.collection("Contacts");
                                        cellContacts contacts = new cellContacts();
                                        contacts.setCellNo(cellno);
                                        DocumentReference cl = collect.document(documentReference.getId());
                                        cl.set(contacts);
                                        contacts cont = new contacts();
                                        cont.setUserId(documentReference.getId());
                                        cont.setContact(cellno);
                                        coll.add(cont);
                                        if (userType == 1) {
                                            Intent intent = new Intent(getBaseContext(), Homes.class);
                                            startActivity(intent);
                                            Intent startservice = new Intent(Registration.this, OrderNotification.class);
                                            startService(startservice);
                                        } else {
                                            Intent intent = new Intent(getBaseContext(), Homec.class);
                                            startActivity(intent);
                                        }
                                    }
                                });

                            }
                        }
                    });

                }
                if (profilePath == null) {
                    Toast.makeText(getBaseContext(), "Provide an Image", Toast.LENGTH_SHORT).show();
                }
                if (userType == 0) {
                    Toast.makeText(getBaseContext(), "Provide either a buyer or seller", Toast.LENGTH_SHORT).show();
                }
                if ((firstName.getText().toString().length() < 2) && (secondName.getText().toString().length() < 3) && (surName.getText().toString().length() < 3)) {
                    Toast.makeText(getBaseContext(), "Enter name feild correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void profileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 9);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9 && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
