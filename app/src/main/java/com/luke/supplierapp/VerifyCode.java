package com.luke.supplierapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class VerifyCode extends AppCompatActivity {
    private String recievedCode, cellno;
    private EditText inputcode;
    private FirebaseFirestore firestore;
    private CollectionReference cellphone;
    private BroadcastReceiver reciever;
    private FirebaseAuth auth;
    private Button send;

    @Override
    protected void onResume() {
        super.onResume();
        if (reciever == null) {
            reciever = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    recievedCode = intent.getStringExtra("phonecode");
                }
            };
        }
        registerReceiver(reciever, new IntentFilter("Code"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        Bundle bundle = getIntent().getExtras();
        cellno = bundle.getString("cellno");
        firestore = FirebaseFirestore.getInstance();
        cellphone = firestore.collection("CellphoneNumbers");
        inputcode = findViewById(R.id.code);
        send = findViewById(R.id.send);
        auth = FirebaseAuth.getInstance();
    }
    public void verify(View view){
        Toast.makeText(getApplicationContext(),"High",Toast.LENGTH_LONG).show();
        String code = inputcode.getText().toString();
        if (code.length() >= 3) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(recievedCode, code);
            if (!credential.equals("")) {
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            cellphone.whereEqualTo("cellNo", cellno).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        boolean wheatherEmpty = task.getResult().isEmpty();
                                        if (wheatherEmpty == true) {
                                            //Register
                                            Intent intents = new Intent(getBaseContext(), Registration.class);
                                            intents.putExtra("Contact", cellno);
                                            startActivity(intents);

                                        } else {
                                            for (DocumentSnapshot query : task.getResult()) {
                                                final cellContacts contacts = query.toObject(cellContacts.class);
                                                sqlite sql = new sqlite(getBaseContext());
                                                sql.addId(contacts.getId());
                                                DocumentReference getting = firestore.collection("usersDetails").document(contacts.getId());
                                                getting.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        userDetails details = documentSnapshot.toObject(userDetails.class);
                                                        int type = details.getType();
                                                        if (type == 1) {
                                                            Intent intents = new Intent(getBaseContext(), Homes.class);
                                                            intents.putExtra("Id", contacts.getId());
                                                            startActivity(intents);
                                                        }
                                                        if (type == 2) {
                                                            Intent intentc = new Intent(getBaseContext(), Homec.class);
                                                            intentc.putExtra("Id", contacts.getId());
                                                            startActivity(intentc);
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        } else {
            Toast.makeText(getBaseContext(), "Povide the right code ", Toast.LENGTH_LONG).show();
        }
    }
}
