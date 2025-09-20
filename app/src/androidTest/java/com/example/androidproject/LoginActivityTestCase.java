package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Root;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTestCase {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityRule = new ActivityTestRule<>(LoginActivity.class);

    private UserDatabaseHelper dbHelper;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        dbHelper = UserDatabaseHelper.getInstance(context);
        dbHelper.addUser("TestUser", "testuser", "test@example.com", "Password123");

        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
        dbHelper.close();
    }

    @Test
    public void testSuccessfulLogin() {
        onView(withId(R.id.usernameEdit)).perform(typeText("testuser"));
        onView(withId(R.id.passwordEdit)).perform(typeText("Password123"));
        onView(withId(R.id.loginButton)).perform(click());
        //matches(not(withText("Login Successful")));

    }

    @Test
    public void testFailedLogin() {
        onView(withId(R.id.usernameEdit)).perform(typeText("wronguser"));
        onView(withId(R.id.passwordEdit)).perform(typeText("wrongpassword"));
        onView(withId(R.id.loginButton)).perform(click());
        //matches(not(withText("Invalid username or password")));

    }

    @Test
    public void testNavigateToSignUp() {
        onView(withId(R.id.newUser)).perform(click());

        intended(hasComponent(SignUpActivity.class.getName()));
    }

    // Add this helper class for Toast matching
    public class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                return windowToken == appToken;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }
    }
}