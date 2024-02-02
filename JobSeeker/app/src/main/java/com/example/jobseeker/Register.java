package com.example.jobseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**
 * The `Register` class represents the registration activity in the JobSeeker application.
 * Users can create a new account by providing their email and password.
 * @author Lingpeng Xiao
 */
public class Register extends AppCompatActivity {
    //Init components
    TextInputEditText editTextEmail, editTextPassword;

    TextView textViewLogin;
    Button btnRegister;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference usersRef = mDatabase.child("users");
    /**
     * Check if the user is currently signed in. If yes, redirect to the main activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //When user already sign in, directly go to the main activity.
        if(currentUser != null){
            //Jump to the Main page
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            //End current activity
            finish();
        }
    }
    /**
     * Initialize the registration activity and set up UI components and click listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Hook components
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_register);
        textViewLogin = findViewById(R.id.loginLink);
        mAuth = FirebaseAuth.getInstance();
        ProgressBar progressBarReg = findViewById(R.id.progressBarReg);

        //Set up button onClick function
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get input
                progressBarReg.setVisibility(View.VISIBLE);
                String email, password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                //Check if email or password is empty, if so, show error msg, turn off this page.
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Input is ok, create user with input email and password.
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //After task completing, remove progress bar.
                                progressBarReg.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    String shortEmail = email.replaceAll("\\.", "");
                                    usersRef.child(shortEmail).child("isShareJobAllowed").setValue("true");
                                    // If user is added successfully, display a message to the user.
                                    Toast.makeText(Register.this, "New user Added.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If user adding fails, display a message to the user.
                                    Toast.makeText(Register.this, "New user generation failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //Set up textView onClick function
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Jump to the Login page
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                //End current activity
                finish();
            }
        });
    }
}