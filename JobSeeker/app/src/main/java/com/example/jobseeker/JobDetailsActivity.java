package com.example.jobseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * JobDetailsActivity represents the detailed view of a specific job.
 * Users can view job details, subscribe to a job, and share it.
 */
public class JobDetailsActivity extends AppCompatActivity {
    Button subscribe, shareJobs;

    DatabaseReference mDatabase;
    DatabaseReference savedJobIdRef;
    ArrayList<String> savedJobs = new ArrayList<String>();
    FirebaseAuth auth;
    FirebaseUser user;
    String jobId, subNum;
    IncreaseNumObserver increaseNumObserver = new IncreaseNumObserver();
    StoreChangeObserver storeChangeObserver = new StoreChangeObserver();


    /**
     * The entry point for the JobDetailsActivity where initialization is done.
     * @author Jiaqi Zhuang, lingPeng Xiao, Jialin Wang
     * @param savedInstanceState a bundle containing the most recent data for this activity
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        subscribe = findViewById(R.id.subscribeButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        shareJobs = findViewById(R.id.shareJobsButton);

        Intent intent = getIntent();
        String jobDataStr = intent.getStringExtra("jobData");
        try {
            assert jobDataStr != null;
            JSONObject jobData = new JSONObject(jobDataStr);

            TextView jobTitle = findViewById(R.id.jobTitle);
            jobTitle.setText(jobData.getString("JobTitle"));

            TextView companyName = findViewById(R.id.companyName);
            String companyHtml = "<b>Company:</b><br>" + jobData.getString("CompanyName");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                companyName.setText(Html.fromHtml(companyHtml, Html.FROM_HTML_MODE_COMPACT));
            }

            TextView category = findViewById(R.id.category);
            String categoryHtml = "<b>Category:</b><br>" + jobData.getString("Category");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                category.setText(Html.fromHtml(categoryHtml, Html.FROM_HTML_MODE_COMPACT));
            }

            TextView description = findViewById((R.id.description));
            String descriptionHtml = "<b>Description:</b><br>" + jobData.getString("Description");
            description.setText(Html.fromHtml(descriptionHtml, Html.FROM_HTML_MODE_COMPACT));

            TextView requirements = findViewById(R.id.requirements);
            String[] requirementsItems = parseJSONArray(jobData.getString("Requirements"));
            String requirementsHtml = "<b>Requirements:</b><br>" + String.join("<br>", requirementsItems);
            requirements.setText(Html.fromHtml(requirementsHtml, Html.FROM_HTML_MODE_COMPACT));


            TextView responsibility = findViewById(R.id.responsibility);
            String responsibilityHtml = "<b>Responsibility:</b><br>" + jobData.getString("Responsibility");
            responsibility.setText(Html.fromHtml(responsibilityHtml, Html.FROM_HTML_MODE_COMPACT));

            TextView salary = findViewById(R.id.salary);
            String salaryHtml = "<b>Salary Range:</b><br>" + jobData.getString("SalaryRange");
            salary.setText(Html.fromHtml(salaryHtml, Html.FROM_HTML_MODE_COMPACT));

            TextView benefits = findViewById(R.id.benefits);
            String[] benefitsItems = parseJSONArray(jobData.getString("Benefits"));
            String benefitsHtml = "<b>Benefits:</b><br>" + String.join("<br>", benefitsItems);
            benefits.setText(Html.fromHtml(benefitsHtml, Html.FROM_HTML_MODE_COMPACT));

            TextView postingDate = findViewById(R.id.postingDate);
            String postingDateHtml = "<b>Posting Date:</b><br>" + jobData.getString("PostingDate");
            postingDate.setText(Html.fromHtml(postingDateHtml, Html.FROM_HTML_MODE_COMPACT));

            TextView workType = findViewById(R.id.workType);
            String workTypeHtml = "<b>Work Type:</b><br>" + jobData.getString("WorkType");
            workType.setText(Html.fromHtml(workTypeHtml, Html.FROM_HTML_MODE_COMPACT));

            TextView location = findViewById(R.id.location);
            String[] locationItems = parseJSONArray(jobData.getString("Location"));
            String locationHtml = "<b>Location:</b> " + String.join(", ", locationItems);
            location.setText(Html.fromHtml(locationHtml, Html.FROM_HTML_MODE_COMPACT));

            jobId = jobData.getString("id");


            //Get the number of subscribers of this job
            mDatabase.child("jobs").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /**
                     * @author Jialin Wang
                     * This method is called when data at the specified location changes.
                     * @param dataSnapshot The current data at the location.
                     * @retun void
                     */
                    // Assuming the subscriber number is stored under a key named "subscribers"
                    subNum = dataSnapshot.child(jobId).getValue(String.class);
                    TextView subscribeNum = findViewById(R.id.subscribeNum);
                    String subscribeNumHtml = "<b>Number of subscriber:</b><br>" + subNum;
                    subscribeNum.setText(Html.fromHtml(subscribeNumHtml, Html.FROM_HTML_MODE_COMPACT));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    /**
                     * This method is called if the data retrieval is canceled or fails.
                     * @param databaseError The error information.
                     * @return void
                     */
                    // Handle potential errors here
                    Log.e("Firebase", "Error reading subscribers", databaseError.toException());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        shareJobs.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the "shareJobs" button is clicked.
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                /**
                 * This code navigates to the Share activity and passes the "jobID" as an extra.
                 */
                Intent intent = new Intent(getApplicationContext(), Share.class);
                intent.putExtra("jobID", jobId);
                startActivity(intent);
            }
        });
        // user click the return button and return to the previous page
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // This will close the current activity (JobDetailsActivity)
            }
        });

        //Shorten user email
        String userEmail = user.getEmail();
        String shortEmail = userEmail.replaceAll("\\.", "");
        savedJobIdRef = mDatabase.child("users").child(shortEmail).child("savedJobs");

        //Add subscribed jobs
        subscribe.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the "subscribe" button is clicked.
             * @param view
             * @return
             */
            @Override
            public void onClick(View view) {
                // Fetch the saved jobs from Firebase
                savedJobIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * This method is called when data at the specified location changes.
                     * @param dataSnapshot The current data at the location
                     * @return
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Clear the list to ensure no duplicates if this method is called multiple times
                        savedJobs.clear();
                        for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                            String jobId = jobSnapshot.getValue(String.class);
                            savedJobs.add(jobId);
                        }

                        // Now, perform the duplicate check
                        boolean isSaved = false;
                        for(String id : savedJobs) {
                            if(Objects.equals(id, jobId)) {
                                Toast.makeText(JobDetailsActivity.this, "This job has already been saved!", Toast.LENGTH_SHORT).show();
                                isSaved = true;
                                break;
                            }
                        }

                        // If not saved, save it.
                        if (!isSaved){
                            increaseSubscribeNum(shortEmail);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        /**
                         * This method is called if the data retrieval is canceled or fails.
                         * @param databaseError The error information.
                         * @return
                         */
                        // Handle potential errors here
                        Log.e("Firebase", "Error reading saved jobs", databaseError.toException());
                    }
                });
            }
        });


    }

    /**
     * Parses a JSON array string into an array of strings.
     * @author Jiaqi Zhuang
     * @param jsonArrayString the JSON array as a string
     * @return an array of strings or an empty array if there's an error
     */
    private String[] parseJSONArray(String jsonArrayString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            String[] items = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                items[i] = jsonArray.getString(i);
            }
            return items;
        } catch (JSONException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    /***
     * @Usage the function increases the number of subscribers of the specific job
     * @author Lingpeng Xiao
     * @param shortEmail the value of user emails after removing dots
     */
    private void increaseSubscribeNum(String shortEmail){
        //Convert String to int
        int subNumInt = Integer.parseInt(subNum);
        //Increase the number of subscribers
        String numberStr = increaseNumObserver.update(subNumInt);;
        TextView subscribeNum = findViewById(R.id.subscribeNum);
        String subscribeNumHtml = "<b>Number of subscriber:</b><br>" + numberStr;
        subscribeNum.setText(Html.fromHtml(subscribeNumHtml, Html.FROM_HTML_MODE_COMPACT));
        // Write a message to the database
        storeChangeObserver.update(mDatabase, shortEmail, numberStr, jobId);
        Toast.makeText(JobDetailsActivity.this, "Successfully saved!", Toast.LENGTH_SHORT).show();
    }

}
