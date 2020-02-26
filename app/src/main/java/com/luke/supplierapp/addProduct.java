package com.luke.supplierapp;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class addProduct extends AppCompatActivity {
    FirebaseFirestore referece;
    private DocumentReference refering;
    private EditText productNam, productPr, productQuant, productUnit;
    private Button getImage, send;
    private Uri productImage;
    private   String fname,sname,srname,contacts,imageString;
    private int type;
    private BroadcastReceiver broadcastReceiver;
    private Double Longitude;
    private Toolbar toolbar;
    private Date date;
    private Double Latitude;
    private String categName,productName ;
    private StorageReference store;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Longitude = intent.getDoubleExtra("Longitude", 0);
                    Latitude = intent.getDoubleExtra("Latitude", 0);

                }
            };}
            registerReceiver(broadcastReceiver, new IntentFilter("Coordinates"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Intent intents=new Intent(getApplicationContext(),location2.class);
        startService(intents);
        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        referece = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        productNam = findViewById(R.id.productName);
        getImage = findViewById(R.id.getImage);
        send = findViewById(R.id.send);
        productPr = findViewById(R.id.productPrice);
        categName = bundle.getString("categoryId");
       productName=bundle.getString("categoryName");
        productQuant = findViewById(R.id.productquanti);
        productUnit = findViewById(R.id.productUnits);
        final sqlite sql = new sqlite(this);
        getSupportActionBar().setTitle(productName);
        store = FirebaseStorage.getInstance().getReference("productImages");
        final DocumentReference refer = referece.collection("Productimges").document(sql.getUser());
        final CollectionReference collectionReference = referece.collection("Category").document(categName).collection("category");
        final CollectionReference prodC = referece.collection("Products").document(sql.getUser()).collection("products");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog=new ProgressDialog(addProduct.this);
                progressDialog.setTitle("Adding a product");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                if ((productNam.getText().toString().length() > 2) &
                        (productPr.getText().toString().length() > 0) &(productQuant.getText().toString().length() > 0) &
                        (productUnit.getText().toString().length() > 0) &
                        (productImage != null)) {

                    final StorageReference save = store.child(System.currentTimeMillis() + "." + getPathExtension(productImage));
                    save.putFile(productImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return save.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task) {
                            if (task.isSuccessful()) {
                                DocumentReference doc = collectionReference.document(sql.getUser());
                                categorySet catg = new categorySet();
                                catg.setLatitude(Latitude);
                                catg.setLongitude(Longitude);
                                doc.set(catg);
                                productSet prod = new productSet();
                                prod.setCategoryId(categName);
                                prod.setProductName(productNam.getText().toString());
                                prod.setProductPicture(task.getResult().toString());
                                prod.setQuantity(Long.parseLong(productQuant.getText().toString()));
                                prod.setProductPrice(Long.parseLong(productPr.getText().toString()));
                                prod.setUnits(productUnit.getText().toString());
                                prodC.add(prod).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Images img = new Images();
                                        img.setProductId(documentReference.getId());
                                        img.setImageString(task.getResult().toString());
                                        CollectionReference imadd=refer.collection(documentReference.getId());
                                        imadd.add(img);
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(),"Product added",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getBaseContext(), productSeller.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(getBaseContext(),"Make entries in all fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productImage();
            }
        });
    }

    public void productImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 9);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            productImage = data.getData();
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
    public void update() {
        sqlite sqlite = new sqlite(this);
        String userdId = sqlite.getUser();
        refering = referece.collection("Users").document(userdId);
        userDetails details = new userDetails();

        refering.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails users = documentSnapshot.toObject(userDetails.class);
                fname = users.getFirstName();
                sname = users.getSecondName();
                srname = users.getSurName();
                contacts = users.getContact();
                imageString = users.getImagePath();
                type = users.getType();
                date = users.getDate();
            }
        });
        details.setLongitude(Longitude);
        details.setLatitude(Latitude);
        details.setSurName(srname);
        details.setSecondName(sname);
        details.setContact(contacts);
        details.setFirstName(fname);
        details.setImagePath(imageString);
        details.setType(type);
        details.setDate(date);
        refering.set(details);
    }
}