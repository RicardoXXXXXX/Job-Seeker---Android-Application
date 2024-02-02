package com.example.jobseeker;

import android.widget.Button;

import com.google.firebase.database.DatabaseReference;

/***
 * This interface implements the state design pattern.
 * The state is "if the user allows other users to share jobs with the user self".
 * @author Lingpeng Xiao
 */
public interface ShareState {
    void changeState(DatabaseReference userStateRef, Button changeStateButton);

    String updateData(DatabaseReference mDatabase, String shortEmail, String jobId);
}
