package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupActivityTestcase {

    @Rule
    public ActivityTestRule<SignUpActivity> signUpActivityRule = new ActivityTestRule<>(SignUpActivity.class);

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void testSignUp() {
        // Sign up with new credentials
        String fullname = "Test2";
        String email = "test2@example.com";
        String username = "Test2";
        String password = "Pass123456";

        // Perform sign up
        onView(withId(R.id.fullNameEdit)).perform(typeText(fullname));
        onView(withId(R.id.usernameEdit)).perform(typeText(username));
        onView(withId(R.id.emailIDEdit)).perform(typeText(email));
        onView(withId(R.id.passwordEdit)).perform(typeText("Pass123456"));
        onView(withId(R.id.confirmPasswordEdit)).perform(typeText(password));
        onView(withId(R.id.signUpButton)).perform(click());

        // Verify sign up success
        //matches(not(withText("Sign up Successful")));

        //onView(withText("Sign up Successful")).inRoot(new ToastMatcher()).check(matches(withText("Sign up Successful")));
    }
}
