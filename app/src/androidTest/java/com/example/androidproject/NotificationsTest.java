package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationsTest {

    private static final String TEST_USERNAME = "testuser";
    private UserDatabaseHelper dbHelper;

    @Before
    public void setUp() {
        Intents.init();
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = UserDatabaseHelper.getInstance(context);
        // Add a test user to the database
        dbHelper.addUser("Test User", TEST_USERNAME, "test@example.com", "password123");
    }

    @After
    public void tearDown() {
        Intents.release();
        // Clean up the test user
        dbHelper.deleteUser("test@example.com");
    }

    @Test
    public void testNotificationSettingsDisplayed() {
        launchActivity();
        onView(withId(R.id.reminders_toggle)).check(matches(isDisplayed()));
        onView(withId(R.id.trending_places_toggle)).check(matches(isDisplayed()));
        onView(withId(R.id.feedback_toggle)).check(matches(isDisplayed()));
        onView(withId(R.id.support_toggle)).check(matches(isDisplayed()));
    }

    @Test
    public void testSaveNotificationPreferences() {
        // Launch the activity
        ActivityScenario<notifications> scenario = launchActivity();

        // Toggle the notification switches
        onView(withId(R.id.reminders_toggle)).perform(ViewActions.click());
        onView(withId(R.id.trending_places_toggle)).perform(ViewActions.click());
        onView(withId(R.id.feedback_toggle)).perform(ViewActions.click());
        onView(withId(R.id.support_toggle)).perform(ViewActions.click());

        // Click the save button
        onView(withId(R.id.saveNotifiationPref)).perform(ViewActions.click());


        //onView(withText("Notification settings saved")).inRoot(ToastMatcher.isToast()).check(matches(isDisplayed()));
        // Check if the toast message is displayed
        //onView(withText("Notification settings saved"))
                //.inRoot(new ToastMatcher())
                //.check(matches(isDisplayed()));

        // Close the scenario
        scenario.close();
    }

    @Test
    public void testBackButton() {
        ActivityScenario<notifications> scenario = launchActivity();
        onView(withId(R.id.backButton)).perform(click());
        intended(hasComponent(Setting.class.getName()));
        scenario.onActivity(activity -> assertTrue("Activity should be finishing", activity.isFinishing()));
        scenario.close();
    }

    @Test
    public void testLoadNotificationSettings() {
        // Set initial settings
        dbHelper.saveNotificationSettings(TEST_USERNAME, true, false, true, false);

        ActivityScenario<notifications> scenario = launchActivity();

        // Verify that the switches reflect the saved settings
        onView(withId(R.id.reminders_toggle)).check(matches(isChecked()));
        onView(withId(R.id.trending_places_toggle)).check(matches(not(isChecked())));
        onView(withId(R.id.feedback_toggle)).check(matches(isChecked()));
        onView(withId(R.id.support_toggle)).check(matches(not(isChecked())));

        // Close the scenario
        scenario.close();
    }

    private ActivityScenario<notifications> launchActivity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), notifications.class);
        intent.putExtra("username", TEST_USERNAME);
        return ActivityScenario.launch(intent);
    }
}
