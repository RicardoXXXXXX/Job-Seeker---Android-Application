package com.example.jobseeker;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * This class defines UI tests for the MainActivity using Espresso framework.
 * Important Note: These tests assume that the user is already logged into the app,
 * as they involve checking elements on the main screen.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Test to check the text of the Logout button.
     */
    @Test
    public void checkLogoutButtonsText() {
        // Check logout button text
        onView(withId(R.id.btn_logout)).check(matches(withText("Logout")));
    }

    /**
     * Test to check the text of the "Show Saved Jobs" button.
     */
    @Test
    public void checkSaveJobButtonsText(){
        // Check show saved jobs button text
        onView(withId(R.id.showSavedJobButton)).check(matches(withText("Saved Jobs")));
    }

    /**
     * Test to check the text of the "Show Received Jobs" button.
     */
    @Test
    public void checkReceivedJobButtonsText(){
        // Check show received jobs button text
        onView(withId(R.id.btn_receivedJobs)).check(matches(withText("Received Jobs")));
    }
}
