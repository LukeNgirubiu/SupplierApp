package com.luke.supplierapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class Registration extends AppCompatActivity {
    private EditText firstName, secondName, surName;
    private Button sendingDetails;
    private int userType;
    private RadioGroup radioGroup;
    private String cellno;
    private Toolbar toolbar;
private String userId;
private FirebaseAuth authenticate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPermissions();
        setContentView(R.layout.activity_registration);
        userType = 0;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
        secondName = findViewById(R.id.secondName);
        surName = findViewById(R.id.surName);
        firstName = findViewById(R.id.firstName);
        sendingDetails = findViewById(R.id.sendingDetails);
        radioGroup = findViewById(R.id.radioGroup);
        authenticate= FirebaseAuth.getInstance();
        userId=authenticate.getUid();
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

        sendingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstname=firstName.getText().toString();
                final String secondname=secondName.getText().toString();
                final String surname=surName.getText().toString();

                if ((!firstname.isEmpty())& (!secondname.isEmpty())&(!surname.isEmpty())&(userType != 0)) {
                    Intent intent=new Intent(getBaseContext(),submit.class);
                    intent.putExtra("FIRSTNAME",firstname);
                    intent.putExtra("SECONDNAME",secondname);
                    intent.putExtra("THIRDNAME",surname);
                    intent.putExtra("Id",userId);
                    intent.putExtra("USERTYPE",userType);
                    intent.putExtra("Contact",cellno);
                    startActivity(intent);
                    Intent startservice=new Intent(getApplicationContext(),locations.class);
                    startService(startservice);
                }
              else {
                    Toast.makeText(getBaseContext(),"Provide all details",Toast.LENGTH_LONG).show();
                }

            }
        });
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

}
/*    */