package com.abiralsingh.donatetwo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Cart extends AppCompatActivity {

    MyAdapter_2 adapter;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        uid = FirebaseAuth.getInstance().getUid();

        setUpRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cart");
    }

    public void setUpRecyclerView() {
        Query query = FirebaseDatabase.getInstance()
                .getReference().child("Cart")
                .child(uid).orderByChild("pid");

        FirebaseRecyclerOptions<Donate_Item> options = new FirebaseRecyclerOptions
                .Builder<Donate_Item>().setQuery(query, Donate_Item.class)
                .build();

        adapter = new MyAdapter_2(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_cart);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyAdapter_2.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int position) {
                String pid;
                pid = dataSnapshot.getKey();

                //removing from list of claim
                FirebaseDatabase.getInstance().getReference()
                        .child("UserPost")
                        .child(pid)
                        .child("claimed")
                        .orderByChild("url")
                        .equalTo(dataSnapshot.getRef().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                //removing from cart
                dataSnapshot.getRef().removeValue();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        menu.findItem(R.id.action_donate).setVisible(false);
        menu.findItem(R.id.action_items_for_donation).setVisible(false);
        menu.findItem(R.id.action_cart).setVisible(false);
        menu.findItem(R.id.action_sign_out).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {

        } else if (item.getItemId() == R.id.action_about_us) {

        }
        return super.onOptionsItemSelected(item);
    }
}
