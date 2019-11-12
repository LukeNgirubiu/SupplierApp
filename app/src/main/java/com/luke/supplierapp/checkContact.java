package com.luke.supplierapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class checkContact extends AppCompatActivity {
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private EditText cellPhoneNumber,inputcode;
    private Button sending;
    private CollectionReference cellphone;
    String phoneCode;
    private Toolbar toolbar;
    private CollectionReference collect;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_contact);
        cellPhoneNumber = findViewById(R.id.cellNo);
        toolbar = findViewById(R.id.toolbar);
        inputcode = findViewById(R.id.code);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Starting");
        firestore=FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
         collect = firestore.collection("Contacts");
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {


            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                phoneCode = s;
            }

        };



    }

    public void send(View view) {
        String cellno = cellPhoneNumber.getText().toString().trim();
        if (cellno.length() == 13) {
            Toast.makeText(getBaseContext(), "Wait for code", Toast.LENGTH_SHORT).show();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(cellno, 80, TimeUnit.SECONDS, this, callbacks);
        } else {
            Toast.makeText(getBaseContext(), "Enter collect cellphone number", Toast.LENGTH_SHORT).show();
        }
    }
    public void verify(View view){
        final String cellno = cellPhoneNumber.getText().toString().trim();
        String code = inputcode.getText().toString().trim();
        if (code.length() >= 3) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneCode, code);
            if (!credential.equals("")) {

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"High",Toast.LENGTH_LONG).show();
                            collect.whereEqualTo("cellNo", cellno).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    boolean wheatherEmpty = queryDocumentSnapshots.isEmpty();
                                    if (wheatherEmpty == true) {
                                        Intent intents = new Intent(getBaseContext(), Registration.class);
                                        intents.putExtra("Contact", cellno);
                                        startActivity(intents);
                                    } else {
                                        for (DocumentSnapshot query : queryDocumentSnapshots) {
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
                            });
                        }
                    }
                });
            }
        } else {
            Toast.makeText(getBaseContext(), "Provide the right code ", Toast.LENGTH_LONG).show();
        }
    }
}
