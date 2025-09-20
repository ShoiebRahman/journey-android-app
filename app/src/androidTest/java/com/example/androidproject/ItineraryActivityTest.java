package com.example.androidproject;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ItineraryActivityTest {

    private UserDatabaseHelper dbHelper;
    private long testUserId;

    @Before
    public void setUp() {
        Intents.init();
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = UserDatabaseHelper.getInstance(context);

        // Create a test user and add some itineraries
        testUserId = dbHelper.addUser("Test User", "testuser", "test@example.com", "password123");
        dbHelper.addItinerary(testUserId, "Test Itinerary 1", "2023-01-01");
        dbHelper.addItinerary(testUserId, "Test Itinerary 2", "2023-02-01");

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
    public void testItineraryListDisplayed() {
        ActivityScenario.launch(ItineraryActivity.class);
        onView(withId(R.id.itineraryListView)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddButtonDisplayed() {
        ActivityScenario.launch(ItineraryActivity.class);
        onView(withId(R.id.addButton)).check(matches(isDisplayed()));
        onView(withId(R.id.addButton)).check(matches(withText(R.string.add_button_text)));
    }

    @Test
    public void testAddButtonClick() {
        ActivityScenario.launch(ItineraryActivity.class);
        onView(withId(R.id.addButton)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(AddItineraryActivity.class.getName()));
    }

    @Test
    public void testBackButtonClick() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItineraryActivity.class);
        intent.putExtra("fullName", "Test User");
        intent.putExtra("email", "test@example.com");
        intent.putExtra("username", "testuser");
        intent.putExtra("userId", testUserId);
        ActivityScenario.launch(intent);

        onView(withId(R.id.backButton)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(ProfileSection.class.getName()));
    }

    @Test
    public void testItineraryItemClick() {
        ActivityScenario<ItineraryActivity> scenario = ActivityScenario.launch(ItineraryActivity.class);

        scenario.onActivity(activity -> {
            ListView listView = activity.findViewById(R.id.itineraryListView);
            // Ensure the ListView has items
            assertThat(listView.getAdapter().getCount(), greaterThan(0));

            // Perform click on the first item
            activity.runOnUiThread(() -> {
                listView.performItemClick(
                        listView.getChildAt(0),
                        0,
                        listView.getAdapter().getItemId(0)
                );
            });
        });

        // Check if the correct intent was launched
        Intents.intended(IntentMatchers.hasComponent(ItineraryScrollView.class.getName()));
    }

    @Test
    public void testDeleteItineraryItem() {
        ActivityScenario<ItineraryActivity> scenario = ActivityScenario.launch(ItineraryActivity.class);

        // Wait for the ListView to be displayed
        onView(withId(R.id.itineraryListView)).check(matches(isDisplayed()));

        // Get the initial count of items
        final int[] initialCount = new int[1];
        scenario.onActivity(activity -> {
            ListView listView = activity.findViewById(R.id.itineraryListView);
            initialCount[0] = listView.getAdapter().getCount();
        });

        // Click on the more options icon of the first item
        onData(anything())
                .inAdapterView(withId(R.id.itineraryListView))
                .atPosition(0)
                .onChildView(withId(R.id.menuButton))
                .perform(click());

        // Click on the delete option in the popup menu
        onView(withText(R.string.menu_delete)).perform(click());

        // Verify that the item count has decreased
        scenario.onActivity(activity -> {
            ListView listView = activity.findViewById(R.id.itineraryListView);
            int newCount = listView.getAdapter().getCount();
            assertThat(newCount, lessThan(initialCount[0]));
        });
    }
}