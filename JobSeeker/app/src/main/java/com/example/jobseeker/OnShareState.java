package com.example.jobseeker;

import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

/***
 * This class solves the state when the user allows job sharing.
 * @author Lingpeng Xiao
 */
public class OnShareState implements ShareState{
    //Change state
    @SuppressLint("SetTextI18n")
    @Override
    public void changeState(DatabaseReference userStateRef, Button changeStateButton) {
        userStateRef.setValue("false");
        changeStateButton.setText("Turn on sharing");
    }

    @Override
    public String updateData(DatabaseReference mDatabase, String shortEmail, String jobId) {
        mDatabase.child("users").child(shortEmail).child("receivedJobs").push().setValue(jobId);
        return "Job shared";
    }
}
