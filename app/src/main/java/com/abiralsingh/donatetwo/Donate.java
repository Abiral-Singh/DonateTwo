package com.abiralsingh.donatetwo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Donate extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    String userId;
    String author = "anonymous";
    String tag, postTitle, postContent;
    EditText prodName;
    EditText prodDesc;
    EditText text_location;
    Button button_donate;
    ImageView spinner_icon;
    TextView text_date;
    TextView text_condition;
    Calendar c;
    DatePickerDialog dpd;
    Animation anim;

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
        anim = AnimationUtils.loadAnimation(this,R.anim.text_box_entry);

        spinner_icon = findViewById(R.id.spinner_icon);
        spinner_icon.startAnimation(anim);
        prodName = (EditText) findViewById(R.id.product_name_text);
        prodName.startAnimation(anim);
        prodDesc = (EditText) findViewById(R.id.product_des_text);
        prodDesc.startAnimation(anim);
        text_location = findViewById(R.id.text_location);
        text_location.startAnimation(anim);
        text_date = (TextView) findViewById(R.id.text_date);
        text_date.startAnimation(anim);
        button_donate=findViewById(R.id.button_donate);
        button_donate.startAnimation(anim);
        text_condition = findViewById(R.id.text_conditions);
        text_condition.startAnimation(anim);

        text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                dpd = new DatePickerDialog(Donate.this, R.style.MyTimePickerDialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                        text_date.setText(mDayOfMonth+"/"+mMonth+"/"+mYear);
                    }
                },day,month,year);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                dpd.show();
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_donate);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Donate");

        Spinner spinner = (Spinner) findViewById(R.id.donate_spinner);
        spinner.startAnimation(anim);
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
            if(!text_location.getText().toString().isEmpty() &&
                    !text_date.getText().toString().isEmpty()){

                postContent = postContent
                        +"\nLocation: "+text_location.getText().toString()
                        +"\n Available Till: "+text_date.getText().toString();
            }else {
                Toast.makeText(getApplicationContext(), "Enter Location and Date", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;

        } else {
            Toast.makeText(getApplicationContext(), "Post Title can't be empty", Toast.LENGTH_LONG).show();
        }

        return false;
    }
}
