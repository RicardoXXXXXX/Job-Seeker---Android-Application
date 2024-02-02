package com.example.jobseeker;

import com.google.firebase.database.DatabaseReference;
/**
 * The `StoreChangeObserver` class represents an observer for changes in the job store.
 * It updates the Firebase Realtime Database when a job is saved or removed from a user's list.
 * @author Lingpeng Xiao
 */
public class StoreChangeObserver implements Observer{
    /**
     * Update the Firebase Realtime Database when a job is saved or removed.
     *
     * @param mDatabase  The Firebase Realtime Database reference.
     * @param shortEmail The shortened email of the user.
     * @param numberStr  The new number of subscribers for the job.
     * @param jobId      The ID of the job to be updated.
     */
    public void update(DatabaseReference mDatabase, String shortEmail, String numberStr, String jobId){
        mDatabase.child("jobs").child(jobId).setValue(numberStr);
        mDatabase.child("users").child(shortEmail).child("savedJobs").push().setValue(jobId);
    }
    @Override
    public void update() {

    }
}
