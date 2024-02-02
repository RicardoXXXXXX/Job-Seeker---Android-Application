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

/**
 * This class defines UI tests for the Register activity using Espresso framework.
 */
@RunWith(AndroidJUnit4.class)
public class RegisterUITest {

    @Rule
    public ActivityScenarioRule<Register> activityScenario = new ActivityScenarioRule<>(Register.class);

    /**
     * Test to check the title of the Register activity.
     */
    @Test
    public void testTitle() {
        onView(withId(R.id.title)).check(matches(withText("Register")));
    }

    /**
     * Test to check the text of the "Register" button and "Login Now" link.
     * Note: Replace with actual text if different.
     */
    @Test
    public void testButtonAndTextViewContent() {
        onView(withId(R.id.btn_register)).check(matches(withText("Register")));
        onView(withId(R.id.loginLink)).check(matches(withText("Login Now")));
    }

    /**
     * Test for a successful registration scenario.
     * @Note Change the email after running the test.
     */
    @Test
    public void testSuccessfulRegister() {
        onView(withId(R.id.email)).perform(typeText("test5@test.com"));
        onView(withId(R.id.password)).perform(typeText("123456"));
        onView(withId(R.id.btn_register)).perform(click());
    }

    /**
     * Test to check the hint text for the email input field.
     */
    @Test
    public void testEmailHint() {
        onView(withId(R.id.email)).check(matches(withHint("Email")));
    }

    /**
     * Test to check the hint text for the password input field.
     */
    @Test
    public void testPasswordHint() {
        onView(withId(R.id.password)).check(matches(withHint("Password")));
    }
}
