package com.luke.supplierapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class category extends AppCompatActivity {
    private Toolbar toolbar;
    List<categoryItems> categoryitems;
    private RecyclerView recyclerView;
    private int de;
    categoryRecy getIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        Bundle bundle =getIntent().getExtras();
        de=bundle.getInt("dec");
                recyclerView = findViewById(R.id.recyclerview);
        categoryitems = new ArrayList<>();
        categoryitems.add(new categoryItems("Breads and Confectionary ", "1"));
        categoryitems.add(new categoryItems("Oils & Fats", "2"));
        categoryitems.add(new categoryItems("Soaps & Detergents", "3"));
        categoryitems.add(new categoryItems("Soft Drinks", "4"));
        categoryitems.add(new categoryItems("Milk & Products", "5"));
        categoryitems.add(new categoryItems("Hard Drinks", "6"));
        categoryitems.add(new categoryItems("Meat & Products ", "7"));
        categoryitems.add(new categoryItems("Petroleum & Products ", "8"));
        getIO = new categoryRecy(categoryitems, this,de);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(getIO);
        recyclerView.setHasFixedSize(true);


    }
}
