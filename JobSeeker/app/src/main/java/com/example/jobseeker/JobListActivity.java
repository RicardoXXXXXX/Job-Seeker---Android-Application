package com.example.jobseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * The `JobListActivity` class represents the activity where users can view a list of jobs based on the content type (saved or received).
 * Users can click on a job to view its details and have the option to clear all jobs from the list.
 * @author Lingpeng Xiao
 */
public class JobListActivity extends AppCompatActivity {
    String contentType, userEmail, shortEmail;
    TextView titleTextView;
    FirebaseAuth auth;
    FirebaseUser user;
    ListView listView;
    ArrayList<String> jobList = new ArrayList<String>();
    ArrayList<String> jobIdList = new ArrayList<String>();
    DatabaseReference mDatabase, savedJobIdRef;
    ObjectMapper objectMapper = new ObjectMapper();
    Button clearAllBtn,backBtn;
    private List<JsonNode> currentDisplayedJob = new ArrayList<JsonNode>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        contentType = getIntent().getExtras().getString("contentType");
        listView = findViewById(R.id.jobListView);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userEmail = user.getEmail();
        shortEmail = userEmail.replaceAll("\\.", "");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        savedJobIdRef = mDatabase.child("users").child(shortEmail).child(contentType);
        clearAllBtn = findViewById(R.id.btn_clearList);
        backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the "backBtn" (back button) is clicked.
             * @param v
             */
            @Override
            public void onClick(View v) {
                finish();  // This will close the current activity (JobDetailsActivity)
            }
        });

        // Instantiate ArrayAdapter
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, jobList);
        listView.setAdapter(adapter);
        // If the extra is not null, change the TextView's content
        if (contentType != null) {
            titleTextView = findViewById(R.id.titleTextView);
            // Use the correct ID for your TextView
            if(Objects.equals(contentType, "savedJobs")){
                titleTextView.setText("My saved jobs");
            } else if (Objects.equals(contentType, "receivedJobs")) {
                titleTextView.setText("My received jobs");
            }

        }
        //Load the data from the realtimeDB
        savedJobIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                jobIdList.clear();
                String json = loadJSONFromFile();
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    String jobId = jobSnapshot.getValue(String.class);
                    int index = Integer.parseInt(jobId);
                    String jobTitle = "";
                    String companyName = "";
                    String workType = "";
                    try {
                        // Parse the existing JSON
                        ObjectNode existingJsonNode = objectMapper.readValue(json, ObjectNode.class);
                        // Get the title, company name, work type from existing JSON
                        ArrayNode existingJobListings = (ArrayNode) existingJsonNode.get("jobListings");
                        jobTitle = String.valueOf(existingJobListings.get(index-1).get("JobTitle"));
                        jobTitle = jobTitle.replace("\"", "");
                        companyName = String.valueOf(existingJobListings.get(index-1).get("CompanyName"));
                        companyName = companyName.replace("\"", "");
                        workType = String.valueOf(existingJobListings.get(index-1).get("WorkType"));
                        workType = workType.replace("\"", "");
                        currentDisplayedJob.add(existingJobListings.get(index-1));
                    }catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error JSON data");
                    }
                    //Add these value to the jobIdList and jobList
                    jobList.add(jobTitle + "\n" + companyName + "\n" + workType);
                    jobIdList.add(jobId);
                }
                // At this point, jobsList contains the id of all saved jobs for the specific user
                // Notify the adapter that the underlying data has changed
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                Log.e("Firebase", "Error reading saved jobs", databaseError.toException());
            }
        });

        // click the job item and leads to a detailed page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // Assuming your JSON data contains an array of job objects
                    JsonNode selectedJob = currentDisplayedJob.get(position);
                    System.out.println("check: " + selectedJob);

                    // Ensure selectedJob is not null
                    if (selectedJob == null) {
                        Toast.makeText(JobListActivity.this, "Error fetching job details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(JobListActivity.this, JobDetailsActivity.class);
                    intent.putExtra("jobData", selectedJob.toString());
                    startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(JobListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        //Clear all jobs in the list
        clearAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseSubscribeNum();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public String loadJSONFromFile() {
        String json;
        try {
            File file = new File(getFilesDir(), "newJson.json");
            if (file.exists()) {
                int size = (int) file.length();
                byte[] buffer = new byte[size];
                FileInputStream fis = new FileInputStream(file);
                fis.read(buffer);
                fis.close();
                json = new String(buffer, "UTF-8");
            } else {
                System.out.println("No Files From Path");
                json = "{}";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e("JSONLoadError", "Error loading JSON from file!", ex);
            return null;
        }
        return json;
    }

    /***
     * This function decreases the number of subscribers.
     */
    public void decreaseSubscribeNum(){
        if (Objects.equals(contentType, "savedJobs")) {
            for (String jId : jobIdList) {
                mDatabase.child("jobs").child(jId).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        String currentCountStr = mutableData.getValue(String.class);

                        // Check for null immediately after fetching value
                        if (currentCountStr == null) {
                            return Transaction.success(mutableData);
                        }

                        int currentCountInt = Integer.parseInt(currentCountStr);
                        mutableData.setValue(String.valueOf(currentCountInt - 1));

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (databaseError != null) {
                            Log.e("Firebase", "Error in transaction", databaseError.toException());
                        }
                    }
                });
            }
        }
        mDatabase.child("users").child(shortEmail).child(contentType).setValue("");
        // Ensure you're clearing jobIdList and jobList.
        jobIdList.clear();
        jobList.clear();
    }
}