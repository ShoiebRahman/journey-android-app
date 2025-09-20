package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingsTest {

    private Context context;
    private SharedPreferences themePreferences;
    private UserDatabaseHelper dbHelper;

    @Rule
    public ActivityScenarioRule<Setting> activityRule = new ActivityScenarioRule<>(Setting.class);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        themePreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        dbHelper = new UserDatabaseHelper(context);
        Intents.init();

        // Prepare intent with mock data
        Intent intent = new Intent(context, Setting.class);
        intent.putExtra("username", "testUser");
        intent.putExtra("fullName", "Test User");
        intent.putExtra("email", "test@example.com");

        // Launch the activity with the prepared intent
        ActivityScenario.launch(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testSettingsActivityLaunch() {
        onView(withId(R.id.settingsHeading)).check(matches(isDisplayed()));
        onView(withId(R.id.settingsHeading)).check(matches(withText(R.string.settings)));
    }

    @Test
    public void testAccountPrivacyNavigation() {
        onView(withId(R.id.accountPrivacyLayout)).perform(click());
        // Verify navigation to AccountPrivacy activity
        onView(withId(R.id.accountPrivacyTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void testNotificationNavigation() {
        onView(withId(R.id.notificationLayout)).perform(click());
        // Verify navigation to Notifications activity
        onView(withId(R.id.notificationHeading)).check(matches(isDisplayed()));
    }

    @Test
    public void testLanguageChangeDialog() {
        onView(withId(R.id.languageLayout)).perform(click());
        // Verify that the language change dialog is displayed
        onView(withText(R.string.changeLanguageDialog)).check(matches(isDisplayed()));
    }

    @Test
    public void testThemeChange() {
        onView(withId(R.id.themeLayout)).perform(click());
        // Verify that the theme change dialog is displayed
        onView(withText(R.string.chooseTheme)).check(matches(isDisplayed()));

        // Select dark theme
        onView(withText("Dark")).perform(click());

        // Verify that the theme preference has been updated
        int savedTheme = themePreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, savedTheme);
    }

    @Test
    public void testBackButtonNavigation() {
        onView(withId(R.id.backButton)).perform(click());
        // Verify navigation to ProfileSection activity
        onView(withId(R.id.textView)).check(matches(withText(R.string.profile)));
    }

    @Test
    public void testDeleteAccountDialog() {
        onView(withId(R.id.deleteLayout)).perform(click());
        // Verify that the delete account dialog is displayed
        onView(withText(R.string.deleteMessage)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteAccount() {
        onView(withId(R.id.deleteLayout)).perform(click());
        // Confirm deletion
        onView(withText(android.R.string.yes)).perform(click());
        // Verify navigation to LoginActivity
        onView(withId(R.id.login)).check(matches(isDisplayed()));
    }
}