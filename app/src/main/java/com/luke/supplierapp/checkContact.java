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
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuthSettings;
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
    private EditText cellPhoneNumber, inputcode;
    private Button sendingCell, sendingCode;
    private TextView cellenrty, codeentry;
    private CollectionReference cellphone;
    public String phoneCode, cellNumber;
    private Toolbar toolbar;
    private String userId;
    private CollectionReference collect;
   private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_contact);

        cellPhoneNumber = findViewById(R.id.cellNo);
        toolbar = findViewById(R.id.toolbar);
        inputcode = findViewById(R.id.code);
        sendingCell = findViewById(R.id.sendNumber);
        sendingCode = findViewById(R.id.sendcode);
        cellenrty = findViewById(R.id.cellenrty);
        codeentry = findViewById(R.id.codeentry);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Starting");
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        collect = firestore.collection("Contacts");

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                phoneCode = s;
                sendingCell.setVisibility(View.INVISIBLE);
                cellPhoneNumber.setVisibility(View.INVISIBLE);
                cellenrty.setVisibility(View.INVISIBLE);
                sendingCode.setVisibility(View.VISIBLE);
                codeentry.setVisibility(View.VISIBLE);
                inputcode.setVisibility(View.VISIBLE);
            }

        };
        sendingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cellNumber = cellPhoneNumber.getText().toString();
                if (cellNumber.length() == 13) {
                    Toast.makeText(getBaseContext(), "Wait for code", Toast.LENGTH_SHORT).show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(cellNumber, 80, TimeUnit.SECONDS, checkContact.this, callbacks);
                } else {
                    Toast.makeText(getBaseContext(), "Enter collect cellphone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendingCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cellNumber = cellPhoneNumber.getText().toString();
                String code = inputcode.getText().toString();

                if (code.length() >= 3) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneCode, code);
                    signIn(credential);
                } else {
                    Toast.makeText(getBaseContext(), "Provide the right code ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void signIn(PhoneAuthCredential credential) {


        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userId = task.getResult().getUser().getUid();
                    Toast.makeText(getBaseContext(),userId,Toast.LENGTH_SHORT).show();
                    DocumentReference getting = firestore.collection("usersDetails").document(userId);
                    getting.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                userDetails details = documentSnapshot.toObject(userDetails.class);
                                int type = details.getType();
                                if (type == 1) {
                                    Intent intents = new Intent(getBaseContext(), Homes.class);
                                    intents.putExtra("Id", userId);
                                    startActivity(intents);

                                }
                                if (type == 2) {
                                    Intent intentc = new Intent(getBaseContext(), Homec.class);
                                    intentc.putExtra("Id", userId);
                                    startActivity(intentc);
                                }
                            } else {
                                Intent intents = new Intent(getBaseContext(), Registration.class);
                                intents.putExtra("Contact", cellNumber);
                                startActivity(intents);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
                else{

                    Toast.makeText(getBaseContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

