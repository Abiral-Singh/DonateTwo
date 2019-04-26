package com.abiralsingh.donatetwo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Final_claim extends AppCompatActivity {

    String claimerName = "";
    String claimerRequest="";

    EditText cName;
    EditText cRequest;
    Button submitButton;
    Animation anim;
    TextView logo;

    String response="";
    int total=0;
    Intent i_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_claim);

        //Keyboard setting
        findViewById(R.id.relativeLayout_claim_page).setOnTouchListener(new View.OnTouchListener() {
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

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_final_claim);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Form");

        //setting up view
        logo=findViewById(R.id.final_claim_logo);
        cName = findViewById(R.id.claimer_name);
        cRequest = findViewById(R.id.claimer_request);
        submitButton = findViewById(R.id.button_submit);

        anim = AnimationUtils.loadAnimation(this,R.anim.text_box_entry);
        logo.startAnimation(anim);
        cName.startAnimation(anim);
        cRequest.startAnimation(anim);
        submitButton.startAnimation(anim);

        //default result of intent
        setResult(Activity.RESULT_CANCELED);

    }

    //make entry if successful and sent intent back as ok else not ok
    public void submitForm(View view){
        if(getFormResponse()){
            final DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Transaction");
            reference.child("total").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        total=Integer.valueOf(dataSnapshot.getValue().toString());
                        String temp=String.valueOf(total);
                        total=total+1;
                        //Log.i("msg",String.valueOf(total));
                        //fill data to firebase
                        reference.child("total").setValue(total);
                        reference.child(temp).setValue(response);
                        Toast.makeText(getApplicationContext(),"Request Submited",Toast.LENGTH_SHORT)
                                .show();

                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    boolean getFormResponse(){
        claimerName=cName.getText().toString();
        claimerRequest = cRequest.getText().toString();
        if(claimerName.isEmpty() || claimerRequest.isEmpty()){
            Toast.makeText(getApplicationContext(),"Fields can't be empty",Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        //get Owner Email
        i_start = getIntent();

        // "[[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\"]]"

        response = "[[\""+ i_start.getStringExtra("cMail")+"\",\""+
                claimerName+"\",\""+
                i_start.getStringExtra("pTitle").replace("\n"," ")+"\",\""+
                i_start.getStringExtra("pDesc").replace("\n"," ")+"\",\""+
                claimerRequest+"\",\""+
                i_start.getStringExtra("oMail")+"\"]]";
                Log.i("msg",response);
        return true;
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
}
