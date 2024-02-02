package com.example.jobseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * MainActivity serves as the main entry point for the JobSeeker application.
 * It provides functionalities to view, search, and interact with job listings.
 * Additionally, users can change their job sharing status and view saved or received job listings.
 * @author Jiaqi Zhuang, Lingpeng Xiao, Yunkai Xu, jialin Wang
 */
public class MainActivity extends AppCompatActivity {
    OffShareState offShareState = new OffShareState();
    OnShareState onShareState = new OnShareState();
    FirebaseAuth auth;
    Button btnLogout, btnShowSavedJobs, btnShowReceivedJobs, changeStateButton;
    String isShareAllowed, userEmail, shortEmail;
    FirebaseUser user;
    DatabaseReference mDatabase, userStateRef, receivedJobIdRef;
    TextView textView;
    ArrayList<String> receivedIdList = new ArrayList<>();
    List<JSONObject> currentDisplayedJobs = new ArrayList<>();
    private final AVLTree avlTree = AVLTree.getInstance();


    /**
     * Initializes the activity, sets up the interface elements, and binds necessary actions.
     * @param savedInstanceState contains any saved data from a previous instance of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readLocalAndMerge();
        initializeAttributes();
        checkNumberOfReceivedJobs();
        setSharingStateListener();
        setupJobListView();
        setSearchFunctionality();
        setJobItemClickListener();
        checkUserStatus();
        setButtonActions();

    }

    /**
     * Reads a local CSV file, converts it to JSON, and merges it with an existing JSON file.
     * The resulting JSON data is written to a new JSON file.
     */
    private void readLocalAndMerge() {
        // convert csv file to json file, and merge with the other json file
        InputStream csvInputStream = getResources().openRawResource(R.raw.csv_data);
        List<Map<String, String>> csvData = Converter.readCSV(csvInputStream);

        String jsonData = Converter.convertToJSON(csvData);
        String existingJsonData = Converter.loadExistingJSON(this);

        Context context = this;
        Converter.mergeJSON(existingJsonData, jsonData, context);
    }

    /**
     * Initializes various attributes and UI elements.
     */
    private void initializeAttributes() {
        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.welcome_msg);
        btnLogout = findViewById(R.id.btn_logout);
        btnShowSavedJobs = findViewById(R.id.showSavedJobButton);
        btnShowReceivedJobs = findViewById(R.id.btn_receivedJobs);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        changeStateButton = findViewById(R.id.btn_changeState);
        user = auth.getCurrentUser();
        assert user != null;
        userEmail = user.getEmail();
        assert userEmail != null;
        shortEmail = userEmail.replaceAll("\\.", "");
        userStateRef = mDatabase.child("users").child(shortEmail).child("isShareJobAllowed");
        receivedJobIdRef = mDatabase.child("users").child(shortEmail).child("receivedJobs");
    }


    /**
     * Checks and notifies if the user has received more than one job.
     * @author Lingpeng Xiao, Jiaqi Zhuang
     */
    private void checkNumberOfReceivedJobs() {
        receivedJobIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                receivedIdList.clear();
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    String jobId = jobSnapshot.getValue(String.class);
                    receivedIdList.add(jobId);
                }
                if (receivedIdList.size() >= 2) {
                    Toast.makeText(MainActivity.this, "You have received more than 1 jobs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading saved jobs", databaseError.toException());
            }
        });
    }


    /**
     * Sets a listener for sharing state and updates the label of the share button.
     * @author Lingpeng Xiao, Jiaqi Zhuang
     */
    private void setSharingStateListener() {
        userStateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isShareAllowed = dataSnapshot.getValue(String.class);
                updateShareButtonLabel();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error reading saved jobs", databaseError.toException());
            }
        });

        changeStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleShareState();
            }
        });
    }


    /**
     * Updates the label of the share button based on the sharing status.
     */
    @SuppressLint("SetTextI18n")
    private void updateShareButtonLabel() {
        if ("true".equals(isShareAllowed)) {
            changeStateButton.setText("TURN OFF SHARING");
        } else if ("false".equals(isShareAllowed)) {
            changeStateButton.setText("TURN ON SHARING");
        }
    }


    /**
     * Toggles the job sharing state and updates the button label.
     */
    private void toggleShareState() {
        if ("true".equals(isShareAllowed)) {
            onShareState.changeState(userStateRef, changeStateButton);
            isShareAllowed = "false";
        } else if ("false".equals(isShareAllowed)) {
            offShareState.changeState(userStateRef, changeStateButton);
            isShareAllowed = "true";
        }
    }


    /**
     * Populates the job list view using JSON data.
     */
    private void setupJobListView() {
        ListView listView = findViewById(R.id.JobList);

        ArrayList<String> items = new ArrayList<>();


        String json = loadJSONFromFile();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jobListings = jsonObject.getJSONArray("jobListings");
            for (int i = 0; i < jobListings.length(); i++) {
                JSONObject job = jobListings.getJSONObject(i);
                currentDisplayedJobs.add(job);

                String jobTitle = job.getString("JobTitle");
                String companyName = job.getString("CompanyName");
                String workType = job.getString("WorkType");
                items.add(jobTitle + "\n" + companyName + "\n" + workType);

                JSONArray locations = job.getJSONArray("Location");
                for (int j = 0; j < locations.length(); j++) {
                    String loc = locations.getString(j);
                    avlTree.insert(loc, job);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemsAdapter);
    }


    /**
     * Sets up search functionality to filter jobs based on user input.
     */
    private void setSearchFunctionality() {
        EditText searchBar = findViewById(R.id.searchBar);
        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = searchBar.getText().toString();
                try {
                    Tokenizer tokenizer = new Tokenizer(input);
                    List<String> tokens = tokenizer.tokenize();

                    Parser parser = new Parser(tokens);
                    Map<String, String> parsedData = parser.parse();

                    List<JSONObject> jobResults = avlTree.search(parsedData.get("location"));
                    if (jobResults != null) {
                        ArrayList<ArrayList<String>> jobs = new ArrayList<>();
                        ArrayList<String> resultTitles = new ArrayList<>();
                        String str = parsedData.get("keyword").toLowerCase();
                        String[] words = str.split(" ");

                        currentDisplayedJobs.clear();
                        for (JSONObject job : jobResults) {
                            ArrayList<String> jobData = new ArrayList<>();
                            jobData.add(job.getString("JobTitle"));
                            jobData.add(job.getString("CompanyName"));
                            jobData.add(job.getString("WorkType"));
                            jobs.add(jobData);

                            for (String word : words) {
                                if (job.getString("JobTitle").toLowerCase().contains(word)) {
                                    resultTitles.add(jobData.get(0) + "\n" + jobData.get(1) + "\n" + jobData.get(2));
                                    currentDisplayedJobs.add(job);
                                    break;
                                }
                            }
                        }

                        ArrayAdapter<String> resultsAdapter =
                                new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, resultTitles);
                        ListView listView = findViewById(R.id.JobList);
                        listView.setAdapter(resultsAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, "No job listings found for the given location", Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalArgumentException | JSONException e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    /**
     * Sets up click listener for job items, leading to detailed job view.
     */
    private void setJobItemClickListener() {
        ListView listView = findViewById(R.id.JobList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject selectedJob = currentDisplayedJobs.get(position);

                    if (selectedJob == null) {
                        Toast.makeText(MainActivity.this, "Error fetching job details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(MainActivity.this, JobDetailsActivity.class);
                    intent.putExtra("jobData", selectedJob.toString());
                    startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Checks if the user is authenticated; if not, navigates to the login page.
     * @author Lingpeng Xiao, Jiaqi Zhuang
     */
    private void checkUserStatus() {
        String name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        if (user == null) {
            navigateToLogin();
        } else {
            textView.setText("Hello " + name + " :)");
        }
    }


    /**
     * Redirects the user to the login page.
     * @author Lingpeng Xiao, Jiaqi Zhuang
     */
    private void navigateToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }


    /**
     * Sets up button actions, such as logout and viewing saved/received jobs.
     * @author Lingpeng Xiao, Jiaqi Zhuang
     */
    private void setButtonActions() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                navigateToLogin();
            }
        });
        btnShowSavedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JobListActivity.class);
                String requiredContent = "savedJobs";
                intent.putExtra("contentType", requiredContent);
                startActivity(intent);
            }
        });
        btnShowReceivedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JobListActivity.class);
                String requiredContent = "receivedJobs";
                intent.putExtra("contentType", requiredContent);
                startActivity(intent);
            }
        });
    }


    /**
     * Loads JSON data from the internal storage file.
     *
     * @return The JSON data string.
     * @author Jiaqi Zhuang, Yunkai Xu
     */
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
}