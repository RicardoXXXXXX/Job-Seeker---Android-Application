package com.example.jobseeker;

import android.annotation.SuppressLint;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;

/***
 * This class solves the state when the user denys job sharing.
 * @author Lingpeng Xiao
 */
public class OffShareState implements ShareState{
    //Change state
    @SuppressLint("SetTextI18n")
    @Override
    public void changeState(DatabaseReference userStateRef, Button changeStateButton) {
        userStateRef.setValue("true");
        changeStateButton.setText("Turn off sharing");
    }

    @Override
    public String updateData(DatabaseReference mDatabase, String shortEmail, String jobId) {
        return "Cannot share job to this user";
    }
}
