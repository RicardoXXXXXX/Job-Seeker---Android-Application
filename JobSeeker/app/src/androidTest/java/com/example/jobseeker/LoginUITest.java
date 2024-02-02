package com.example.jobseeker;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

// This class defines UI tests for the Login activity using Espresso framework
@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    // Define a rule for launching the Login activity before each test
    @Rule
    public ActivityScenarioRule<Login> activityScenario = new ActivityScenarioRule<>(Login.class);

    // Test to check button and text view contents
    @Test
    public void testButtonAndTextViewContent() {
        onView(withId(R.id.btn_login)).check(matches(withText("Login")));
        onView(withId(R.id.regLink)).check(matches(withText("Register Now")));
    }

    // Test to check the title of the Login activity
    @Test
    public void testTitle() {
        onView(withId(R.id.title)).check(matches(withText("Login")));
    }

    // Test for a successful login scenario
    @Test
    public void testSuccessfulLogin() {
        onView(withId(R.id.email)).perform(typeText("test1@test.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.btn_login)).perform(click());
    }

    // Test to check the hint text for the email input field
    @Test
    public void testEmailHint() {
        onView(withId(R.id.email)).check(matches(withHint("Email")));
    }

    // Test to check the hint text for the password input field
    @Test
    public void testPasswordHint() {
        onView(withId(R.id.password)).check(matches(withHint("Password")));
    }
}
