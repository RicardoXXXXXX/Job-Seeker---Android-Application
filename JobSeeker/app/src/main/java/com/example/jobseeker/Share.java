package com.example.jobseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Activity for sharing job information
 * Check input email is valid
 * Check whether user allows to share
 * @author Jialin Wang
 * @author Lingpeng Xiao
 */
public class Share extends AppCompatActivity {
    OffShareState offShareState = new OffShareState();
    OnShareState onShareState = new OnShareState();
    TextInputEditText editShareEmail, editShareJobID;
    Button btn_share;
    DatabaseReference mDatabase;
    FirebaseUser user;
    String userEmail, jobId;
    DatabaseReference saveJobIdRef;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState The saved instance state.
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        btn_share = findViewById(R.id.btn_share);
        editShareEmail = findViewById(R.id.email);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobID");
        TextView back = findViewById(R.id.goBack);
        String userShortEmail =  userEmail.replaceAll("\\.", "");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the back button is clicked.
             * @param view The view that was clicked.
             * @return void
             */
            @Override
            public void onClick(View view) {
                String email;
                email = editShareEmail.getText().toString();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Share.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                String shortEmail = email.replaceAll("\\.", "");

                DatabaseReference usersRef = mDatabase.child("users");
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * Called when the data is changed in the Firebase database.
                     * @param dataSnapshot The current data at the location.
                     * @return void
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String isShareJobAllowed = dataSnapshot.child(shortEmail).child("isShareJobAllowed").getValue(String.class);
                        //Check if this receiver is valid.
                        if(!dataSnapshot.hasChild(shortEmail)){
                            Toast.makeText(Share.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                        }else {
                            //Check if this receiver is the user-self.
                            if (Objects.equals(shortEmail, userShortEmail)) {
                                Toast.makeText(Share.this, "Can only share with other users", Toast.LENGTH_SHORT).show();
                            } else {
                                //Check if this receiver allows sharing.
                                if(Objects.equals(isShareJobAllowed,"true")) {
                                    String msg = onShareState.updateData(mDatabase, shortEmail, jobId);
                                    Toast.makeText(Share.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    String msg = offShareState.updateData(mDatabase, shortEmail, jobId);
                                    Toast.makeText(Share.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    /**
                     * Called when the data retrieval is canceled.
                     * @param databaseError A description of the error that occurred.
                     * @return void
                     */
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle potential errors here
                        Log.e("Firebase", "Error reading saved jobs", databaseError.toException());
                    }
                });

            }
        });



    }
}

