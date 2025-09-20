package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccountPrivacyTest {

    private static final String TAG = "AccountPrivacyTest";
    private UserDatabaseHelper dbHelper;
    private static final String TEST_USERNAME = "testUser";
    private IdlingResource idlingResource;

    @Rule
    public ActivityScenarioRule<accountPrivacy> activityRule = new ActivityScenarioRule<>(
            new Intent(ApplicationProvider.getApplicationContext(), accountPrivacy.class)
                    .putExtra("username", TEST_USERNAME)
                    .putExtra("fullName", "Test User")
                    .putExtra("email", "test@example.com")
    );

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = UserDatabaseHelper.getInstance(context);

        // Ensure the test user exists in the database
        dbHelper.addUser(TEST_USERNAME, "password", "Test User", "test@example.com");
        // Reset settings before each test
        dbHelper.saveAccountPrivacySettings(TEST_USERNAME, false, false);

        // Register idling resource
        idlingResource = new ElapsedTimeIdlingResource(2000); // 2 seconds
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void tearDown() {
        // Unregister idling resource
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void testActivityLaunch() {
        onView(withId(R.id.accountPrivacyTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.accountPrivacyTextView)).check(matches(withText(R.string.accountHeading)));
    }


    @Test
    public void testSaveSettings() {
        // Toggle switches
        onView(withId(R.id.third_party_toggle)).perform(click());
        onView(withId(R.id.receive_mails_toggle)).perform(click());

        // Click save button
        onView(withId(R.id.savePreferences)).perform(click());

        // Wait for the idling resource (2 seconds)
        // This replaces the Thread.sleep() call

        onView(withId(R.id.savePreferences)).perform(click());

        // Verify UI state
        onView(withId(R.id.third_party_toggle)).check(matches(isChecked()));
        onView(withId(R.id.receive_mails_toggle)).check(matches(isChecked()));
    }

    @Test
    public void testBackNavigation() {
        onView(withId(R.id.backButton)).perform(click());
        // Note: This will finish the activity. You might want to use Intents to verify
        // that the correct intent is sent instead of checking for "Settings" text.
    }

    // Simple IdlingResource for waiting
    private static class ElapsedTimeIdlingResource implements IdlingResource {
        private final long startTime;
        private final long waitingTime;
        private ResourceCallback resourceCallback;

        ElapsedTimeIdlingResource(long waitingTime) {
            this.startTime = System.currentTimeMillis();
            this.waitingTime = waitingTime;
        }

        @Override
        public String getName() {
            return ElapsedTimeIdlingResource.class.getName() + ":" + waitingTime;
        }

        @Override
        public boolean isIdleNow() {
            long elapsed = System.currentTimeMillis() - startTime;
            boolean idle = (elapsed >= waitingTime);
            if (idle) {
                resourceCallback.onTransitionToIdle();
            }
            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }
    }
}