package com.abiralsingh.donatetwo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Donate extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    String userId;
    String author = "anonymous";
    String tag, postTitle, postContent;
    EditText prodName;
    EditText prodDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        //Keyboard setting
        findViewById(R.id.relativeLayout_donate_page).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    //do nothing
                }
                return true;
            }
        });

        prodName = (EditText) findViewById(R.id.product_name_text);
        prodDesc = (EditText) findViewById(R.id.product_des_text);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_donate);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Donate");

        Spinner spinner = (Spinner) findViewById(R.id.donate_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.tags, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("spinner", adapterView.getItemAtPosition(i).toString());
                tag = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference = mDatabaseReference.child("UserPost").push();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        menu.findItem(R.id.action_donate).setVisible(false);
        menu.findItem(R.id.action_cart).setVisible(false);
        menu.findItem(R.id.action_sign_out).setVisible(false);
        menu.findItem(R.id.action_items_for_donation).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public void donate(View view) {

        if (getDonationData()) {
            mDatabaseReference.child("uid").setValue(userId);
            mDatabaseReference.child("author").setValue(author);
            mDatabaseReference.child("postDesc").setValue(postContent);
            mDatabaseReference.child("postTitle").setValue(postTitle);
            mDatabaseReference.child("tag").setValue(tag).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.getException() != null)
                        Log.i("Donation", task.getException().getMessage());
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Donation Successful", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Donation failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            prodDesc.setText("");
            prodName.setText("");
        }
        finish();
    }

    public boolean getDonationData() {
        postTitle = "";
        postContent = "";
        if (prodName != null && !prodName.getText().toString().isEmpty()) {
            postTitle = prodName.getText().toString();
            if (prodDesc != null) {
                postContent = prodDesc.getText().toString();
            }
            return true;

        } else {
            Toast.makeText(getApplicationContext(), "Post Title can't be empty", Toast.LENGTH_LONG).show();
        }

        return false;
    }
}
