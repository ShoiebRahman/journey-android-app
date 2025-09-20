package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileSectionTest {

    private static final String TEST_FULL_NAME = "Test User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testuser";
    private static final long TEST_USER_ID = 1L;

    @Before
    public void setUp() {
        Intents.init();
        // Set up test SharedPreferences
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fullName", TEST_FULL_NAME);
        editor.putString("email", TEST_EMAIL);
        editor.putLong("userId", TEST_USER_ID);
        editor.apply();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testProfileInfoDisplayed() {
        launchActivity();
        onView(withId(R.id.fullName)).check(matches(withText(TEST_FULL_NAME)));
        onView(withId(R.id.emailID)).check(matches(withText(TEST_EMAIL)));
    }

    @Test
    public void testEditProfileButton() {
        launchActivity();
        onView(withId(R.id.editProfile)).perform(click());
        intended(hasComponent(EditProfile.class.getName()));
    }

    @Test
    public void testSettingsButton() {
        launchActivity();
        onView(withId(R.id.settingsLayout)).perform(click());
        intended(hasComponent(Setting.class.getName()));
    }

    @Test
    public void testItineraryButton() {
        launchActivity();
        onView(withId(R.id.itineraryLayout)).perform(click());
        intended(hasComponent(ItineraryActivity.class.getName()));
    }

    @Test
    public void testChangePasswordButton() {
        launchActivity();
        onView(withId(R.id.changePasswordLayout)).perform(click());
        intended(hasComponent(changePassword.class.getName()));
    }

    @Test
    public void testHelpButton() {
        launchActivity();
        onView(withId(R.id.helpLayout)).perform(click());
        intended(hasComponent(Help.class.getName()));
    }

    @Test
    public void testLogoutButton() {
        launchActivity();
        onView(withId(R.id.logoutLayout)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));

        // Verify SharedPreferences are cleared
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        assertTrue(sharedPreferences.getAll().isEmpty());
    }


    private ActivityScenario<ProfileSection> launchActivity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileSection.class);
        intent.putExtra("fullName", TEST_FULL_NAME);
        intent.putExtra("email", TEST_EMAIL);
        intent.putExtra("username", TEST_USERNAME);
        intent.putExtra("userId", TEST_USER_ID);
        return ActivityScenario.launch(intent);
    }
}