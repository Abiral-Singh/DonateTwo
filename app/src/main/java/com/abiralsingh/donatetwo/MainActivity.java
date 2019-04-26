package com.abiralsingh.donatetwo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Animation text_box_anim;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    ProgressBar progressBar;
    public static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Animation Load
        text_box_anim = AnimationUtils.loadAnimation(this,R.anim.text_box_entry);

        //start animation
        TextView text_logo_anim = findViewById(R.id.logo);
        EditText email_text_anim = (EditText) findViewById(R.id.text_email);
        EditText password_text_anim = (EditText) findViewById(R.id.text_password);
        Button logIn_buton_anim = findViewById(R.id.button_logIn);
        Button signIn_buton_anim = findViewById(R.id.button_signIn);

        Animation delay_anim = AnimationUtils.loadAnimation(this,R.anim.text_box_entry);
        delay_anim.setStartOffset(300);
        text_logo_anim.startAnimation(text_box_anim);
        email_text_anim.startAnimation(delay_anim);
        Animation delay_anim2 = AnimationUtils.loadAnimation(this,R.anim.text_box_entry);
        delay_anim2.setStartOffset(500);
        password_text_anim.startAnimation(delay_anim2);
        Animation delay_anim3 = AnimationUtils.loadAnimation(this,R.anim.text_box_entry);
        delay_anim3.setStartOffset(600);
        logIn_buton_anim.startAnimation(delay_anim3);
        signIn_buton_anim.startAnimation(delay_anim3);

        //Keyboard setting
        findViewById(R.id.relativeLayout_logIn).setOnTouchListener(new View.OnTouchListener() {
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
        /**
         * bypassing login
         */
        /*Intent i = new Intent(this,main_page.class);
        finish();
        startActivity(i);*/
        //bypass code end
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.i("message", "inOnAuthStateListener");
                if (user != null) {
                    //signed in
                    Log.i("message", "signed in");
                    Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, main_page.class);
                    finish();
                    startActivity(i);
                } else {
                    //signed out
                    Toast.makeText(getApplicationContext(), "Not Signed In", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
        progressBar.setVisibility(View.GONE);
    }

    public void logIn(View view) {
        progressBar.setVisibility(View.VISIBLE);
        //check if user is authentic give access else show error msg
        EditText email_text = (EditText) findViewById(R.id.text_email);
        EditText password_text = (EditText) findViewById(R.id.text_password);
        String email, password;
        email = email_text.getText().toString().toLowerCase().trim();
        password = password_text.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fields can't be left empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.getException() != null)
                    Log.i("in log in", task.getException().getMessage());
                if (task.isSuccessful()) {
                    //sign in succesful
                    Log.i("LogIn", "Succesful");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                } else {
                    Log.i("Sign in", "signInWithEmail:failure");
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.i("Button", "Pressed Log in");

    }

    public void signUp(View view) {
        //take user to sign up activity
        Log.i("Button", "Pressed Sign Up");
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                                )
                        ).build(),
                RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**intent data
         * IdpResponse{mUser=User{mProviderId='password', mEmail='8@gmail.com',
         * mPhoneNumber='null', mName='Mr 8', mPhotoUri=null}, mToken='null',
         * mSecret='null',
         * mIsNewUser='true', mException=null, mPendingCredential=null}
         */

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Log.i("msg", user.getDisplayName());
                    String t_uid =user.getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference().child("Users").child(t_uid);
                    reference.child("email").setValue(user.getEmail());
                    reference.child("uid").setValue(t_uid);
                    reference.child("name").setValue(user.getDisplayName());
                }
            }
        }
    }
}
