package com.abiralsingh.donatetwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class main_page extends AppCompatActivity {

    private MyAdapter adapter;
    FirebaseAuth mAuth;
    boolean found_in_cart = true; //used to decide wether to add to cart or not
    DataSnapshot mySnapshot;
    String oMAil="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        setUpRecyclerView();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void setUpRecyclerView() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("UserPost");

        FirebaseRecyclerOptions<Donate_Item> options = new FirebaseRecyclerOptions
                .Builder<Donate_Item>().setQuery(query,Donate_Item.class)
                .build();

        adapter= new MyAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int position) {
                mySnapshot=dataSnapshot;
                getOwnerEmail();
                String uid;
                uid=FirebaseAuth.getInstance().getUid();

                if(dataSnapshot.child("uid").getValue().toString().equals(uid)){
                    Toast.makeText(getApplicationContext(),"Can't Claim you are the owner",Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("Cart")
                        .child(uid).orderByChild("pid")
                        .equalTo(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            Toast.makeText(getApplicationContext(),"Saved in Cart",Toast.LENGTH_SHORT).show();
                            found_in_cart =true;
                            return;
                        }else{
                            found_in_cart = false;
                            return;
                          }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(found_in_cart == false){

                    Intent final_claim = new Intent(main_page.this,Final_claim.class);
                    //fill intent with data
                    final_claim.putExtra("cMail",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    final_claim.putExtra("pTitle",mySnapshot.child("postTitle").getValue().toString());
                    final_claim.putExtra("pDesc",mySnapshot.child("postDesc").getValue().toString());
                    final_claim.putExtra("oMail",oMAil);
                    //start activity for result if success make entry
                    startActivityForResult(final_claim,1);
                    mySnapshot=dataSnapshot;

                }
                found_in_cart=true;

            }
        });
    }

    public void getOwnerEmail(){
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mySnapshot.child("uid").getValue().toString())
                .child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    oMAil = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode== Activity.RESULT_OK && mySnapshot.exists()){
                DatabaseReference r =  FirebaseDatabase.getInstance().getReference().child("Cart").child(FirebaseAuth.getInstance().getUid()).child(mySnapshot.getKey());
                r.child("pid").setValue(mySnapshot.getKey());
                r.child("postDesc").setValue(mySnapshot.child("postDesc").getValue());
                r.child("postTitle").setValue(mySnapshot.child("postTitle").getValue());
                r.child("tag").setValue(mySnapshot.child("tag").getValue());
                mySnapshot.getRef().child("claimed").push().child("url").setValue(r.toString());
                Toast.makeText(getApplicationContext(),"Claimed",Toast.LENGTH_SHORT).show();
            }
        }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_donate) {
            Intent i_donate = new Intent(main_page.this, Donate.class);
            startActivity(i_donate);
        } else if (item.getItemId() == R.id.action_setting) {

        } else if (item.getItemId() == R.id.action_about_us) {

        }else if (item.getItemId() == R.id.action_sign_out) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent i_signOut =new Intent(main_page.this,MainActivity.class);
            Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_LONG).show();
            finish();
            startActivity(i_signOut);
        }else if(item.getItemId()==R.id.action_items_for_donation){
            Intent i_for_donate = new Intent(main_page.this, items_donated.class);
            startActivity(i_for_donate);
        }else if(item.getItemId()==R.id.action_cart){
            Intent i_for_donate = new Intent(main_page.this, Cart.class);
            startActivity(i_for_donate);
        }
        return super.onOptionsItemSelected(item);
    }
}
