package com.example.jobseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
/**
 * The `Login` class represents the login activity in the JobSeeker application.
 * Users can enter their email and password to log in and access the app's content.
 * @author Lingpeng Xiao
 */
public class Login extends AppCompatActivity {
    //Init components
    TextInputEditText editTextEmail, editTextPassword;
    Button btnLogin;
    TextView textViewReg;
    FirebaseAuth mAuth;
    ProgressBar progressBarLog;
    //Check if user is currently signed in

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
     * Initialize the login activity and set up UI components and click listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hook components
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnLogin = findViewById(R.id.btn_login);
        progressBarLog = findViewById(R.id.progressBarLg);
        textViewReg = findViewById(R.id.regLink);


        //Set up button onClick function
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get input
                progressBarLog.setVisibility(View.VISIBLE);
                String email, password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                //Check if email or password is empty, if so, show error msg, turn off this page.
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Input is ok, sign in user with input email and password.
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Display process bar
                                progressBarLog.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, show the signed-in user's information
                                    Toast.makeText(getApplicationContext(), "Login Successfully.", Toast.LENGTH_SHORT).show();
                                    //Now access to the content of app
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    //End current activity
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Login failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //Set up textView onClick function
        textViewReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Jump to the Login page
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                //End current activity
                finish();
            }
        });
    }
}