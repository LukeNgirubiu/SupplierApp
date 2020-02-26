package com.luke.supplierapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import android.graphics.Color;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



import java.util.ArrayList;
import java.util.List;

public class Enquire extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private RecyclerView recy;
    private enquiriesRecy show;
    List<setEnqChat> listing;
    Toolbar toolbar;
    private CollectionReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiries);
        recy = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Carts");
        toolbar.setTitleTextColor(Color.RED);
        listing = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        final sqlite sql = new sqlite(this);
        CollectionReference dc = firestore.collection("Carting").document(sql.getUser())
                .collection("cart");
        dc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                        setEnqChat chat = query.toObject(setEnqChat.class);
                        chat.setUsedId(query.getId());
                        listing.add(chat);
                    }
                }
                show = new enquiriesRecy(listing, getApplicationContext());
                recy.setAdapter(show);
            }
        });

        recy.setHasFixedSize(true);

    }
}
    /*setSupportActionBar(tool);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.showuserchat, null);
        actionBar.setCustomView(view);
        final CircleImageView cir = view.findViewById(R.id.profilepicture);
        final TextView firstname = view.findViewById(R.id.buyername);
        final TextView secondName = view.findViewById(R.id.secondNamebuyername);
        final TextView contact = view.findViewById(R.id.contacts);
        refere.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userDetails user = documentSnapshot.toObject(userDetails.class);
                Picasso.get().load(user.getImagePath()).into(cir);
                firstname.setText(user.getFirstName());
                secondName.setText(user.getSecondName());
                contact.setText(user.getContact());
            }

        });
        recy.setLayoutManager(new LinearLayoutManager(this));*/