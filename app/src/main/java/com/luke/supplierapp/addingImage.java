package com.luke.supplierapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class addingImage extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private Uri productImage;
    private TextView imagestring;
    private Button submit, addimage;
    private StorageReference store;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_image);
        imagestring = findViewById(R.id.imageString);
        addimage = findViewById(R.id.selectimage);
        submit = findViewById(R.id.Addimage);
        sqlite sql = new sqlite(this);
        firestore = FirebaseFirestore.getInstance();
        final Bundle bundle=getIntent().getExtras();
        productId=bundle.getString("productId");
        store = FirebaseStorage.getInstance().getReference("productImages");
        final CollectionReference refer = firestore.collection("Productimges").document(sql.getUser()).collection(productId);
        if (productImage != null) {
            imagestring.setText("Image added");
        }
        refer.get().addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() < 4) {

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
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Images img = new Images();
                                        img.setProductId(productId);
                                        img.setImageString(task.getResult().toString());
                                        refer.add(img).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getBaseContext(), "Image added", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(getBaseContext(),"You cann't be added",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
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
}
