package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.ViewFlipper;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testViewFlipperDisplayed() {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.imageSlider)).check(matches(isDisplayed()));
    }

    @Test
    public void testGetStartedButtonDisplayed() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            ViewFlipper viewFlipper = activity.findViewById(R.id.imageSlider);
            activity.runOnUiThread(() -> {
                while (viewFlipper.getDisplayedChild() != 2) {
                    viewFlipper.showNext();
                }
            });
        });

        // Add a small delay to allow the UI to update
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now check for the button's visibility
        onView(withId(R.id.getStartedButton)).check(matches(isDisplayed()));
        onView(withId(R.id.getStartedButton)).check(matches(withText(R.string.getStarted)));
    }

    @Test
    public void testGetStartedButtonClick() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            ViewFlipper viewFlipper = activity.findViewById(R.id.imageSlider);
            activity.runOnUiThread(() -> {
                while (viewFlipper.getDisplayedChild() != 2) {
                    viewFlipper.showNext();
                }
            });
        });

        // Add a small delay to allow the UI to update
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the "Get Started" button
        onView(withId(R.id.getStartedButton)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testImageViewsDisplayed() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Check the first image
        onView(withId(R.id.imageView1)).check(matches(isDisplayed()));

        // Move to the second image
        scenario.onActivity(activity -> activity.runOnUiThread(() -> {
            ViewFlipper viewFlipper = activity.findViewById(R.id.imageSlider);
            viewFlipper.showNext();
        }));
        // Wait for the animation to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.imageView2)).check(matches(isDisplayed()));

        // Move to the third image
        scenario.onActivity(activity -> activity.runOnUiThread(() -> {
            ViewFlipper viewFlipper = activity.findViewById(R.id.imageSlider);
            viewFlipper.showNext();
        }));
        // Wait for the animation to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.imageView3)).check(matches(isDisplayed()));
    }
}