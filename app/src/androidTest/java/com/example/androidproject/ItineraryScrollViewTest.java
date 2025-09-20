package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.Matchers.allOf;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import static java.util.EnumSet.allOf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ItineraryScrollViewTest {

    private UserDatabaseHelper dbHelper;
    private long testUserId;
    private long testItineraryId;

    @Before
    public void setUp() {
        Intents.init();
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = UserDatabaseHelper.getInstance(context);

        // Create a test user and add an itinerary
        testUserId = dbHelper.addUser("Test User", "testuser", "test@example.com", "password123");
        testItineraryId = dbHelper.addItinerary(testUserId, "Test Itinerary", "2023-01-01");

        // Set up SharedPreferences with test user ID
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", testUserId);
        editor.apply();
    }

    @After
    public void tearDown() {
        Intents.release();
        // Clean up test data
        dbHelper.deleteUser("test@example.com");
    }

    @Test
    public void testItineraryDetailsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItineraryScrollView.class);
        intent.putExtra("itineraryId", testItineraryId);
        intent.putExtra("userId", testUserId);
        ActivityScenario.launch(intent);

        onView(withId(R.id.titleTextView)).check(matches(withText("Test Itinerary")));
        onView(withId(R.id.dateTextView)).check(matches(withText("2023-01-01")));
    }

    @Test
    public void testAddTimelineButtonDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItineraryScrollView.class);
        intent.putExtra("itineraryId", testItineraryId);
        intent.putExtra("userId", testUserId);
        ActivityScenario.launch(intent);

        onView(withId(R.id.fabAddTimeline)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddTimelineButtonClick() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItineraryScrollView.class);
        intent.putExtra("itineraryId", testItineraryId);
        intent.putExtra("userId", testUserId);
        ActivityScenario.launch(intent);

        onView(withId(R.id.fabAddTimeline)).perform(click());
        onView(withId(R.id.timelinePeriodEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.notesEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));

        // Enter some text
        onView(withId(R.id.timelinePeriodEditText)).perform(typeText("Day 1"));
        onView(withId(R.id.notesEditText)).perform(typeText("Visit the museum"));

        // Click save
        onView(withId(R.id.saveButton)).perform(click());

        // Add a delay
        try {
            Thread.sleep(1000); // Wait for 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Scroll to the newly added item
        onView(withId(R.id.timelineContainer)).perform(ViewActions.scrollTo());

        // Verify that the new timeline item is added
        onView(allOf(withText(containsString("Day 1")), isDescendantOfA(withId(R.id.timelineContainer))))
                .check(matches(isDisplayed()));
        onView(allOf(withText(containsString("Visit the museum")), isDescendantOfA(withId(R.id.timelineContainer))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonClick() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItineraryScrollView.class);
        intent.putExtra("itineraryId", testItineraryId);
        intent.putExtra("userId", testUserId);
        ActivityScenario.launch(intent);

        onView(withId(R.id.backButton)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(ItineraryActivity.class.getName()));
    }
}