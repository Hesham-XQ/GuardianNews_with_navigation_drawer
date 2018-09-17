package com.example.android.guardiannews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by SAMO on 9/12/2018.
 */

public class goldPrices extends AppCompatActivity {

    TextView price_24;
    TextView price_22;
    TextView price_21;
    TextView price_18;
    TextView price_14;
    TextView price_12;
    TextView price_9;
    TextView goldenPound;

    DatabaseReference mrootref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference m24gold = mrootref.child("24");
    DatabaseReference m22gold = mrootref.child("22");
    DatabaseReference m21gold = mrootref.child("21");
    DatabaseReference m18gold = mrootref.child("18");
    DatabaseReference m14gold = mrootref.child("14");
    DatabaseReference m12gold = mrootref.child("12");
    DatabaseReference m9gold = mrootref.child("9");
    DatabaseReference mpound = mrootref.child("pound");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gold_activity);

        price_24 = (TextView) findViewById(R.id.price24);
        price_22 = (TextView) findViewById(R.id.price22);
        price_21 = (TextView) findViewById(R.id.price21);
        price_18 = (TextView) findViewById(R.id.price18);
        price_14 = (TextView) findViewById(R.id.price14);
        price_12 = (TextView) findViewById(R.id.price12);
        price_9 = (TextView) findViewById(R.id.price9);
        goldenPound = (TextView) findViewById(R.id.pound);


    }

    @Override
    protected void onStart() {
        super.onStart();


        m24gold.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                price_24.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        m22gold.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                price_22.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        m21gold.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                price_21.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        m18gold.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                price_18.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        m14gold.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                price_14.setText(text);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        m12gold.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                price_12.setText(text);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        m9gold.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                price_9.setText(text);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mpound.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue().toString();
                goldenPound.setText(text);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
