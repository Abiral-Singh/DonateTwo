package com.abiralsingh.donatetwo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class items_donated extends AppCompatActivity {

    MyAdapter_2 adapter;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_donated);

        userId = FirebaseAuth.getInstance().getUid();

        setUpRecyclerView();

        Toolbar mToolbar = findViewById(R.id.toolbar_items_donated);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Donated Items");

    }

    public void setUpRecyclerView() {
        Query query = FirebaseDatabase.getInstance()
                .getReference().child("UserPost")
                .orderByChild("uid")
                .equalTo(userId);

        FirebaseRecyclerOptions<Donate_Item> options = new FirebaseRecyclerOptions
                .Builder<Donate_Item>().setQuery(query, Donate_Item.class)
                .build();

        adapter = new MyAdapter_2(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_items_donated);
        // recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyAdapter_2.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int position) {
                //removing from user post
                dataSnapshot.getRef().child("claimed")
                        .orderByChild("url")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String str = snapshot.getValue().toString();
                                str = str.substring(44, str.length() - 1);
                                // Log.i("msg",str);
                                FirebaseDatabase.getInstance().getReference(str).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
