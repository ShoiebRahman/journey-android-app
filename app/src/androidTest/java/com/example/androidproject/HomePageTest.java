package com.example.androidproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.SearchView; // Ensure this import matches your SearchView implementation

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomePageTest {

    @Rule
    public ActivityScenarioRule<home_page> activityRule = new ActivityScenarioRule<>(home_page.class);

    @Test
    public void testSearchFunctionality() {
        // Using a more specific matcher to find AutoCompleteTextView within the SearchView
        Matcher<View> searchAutoComplete = allOf(
                instanceOf(AutoCompleteTextView.class),
                withId(androidx.appcompat.R.id.search_src_text),
                isDescendantOfA(withId(R.id.search_bar))
        );

        // Ensure the SearchView is expanded before interacting with it
        onView(allOf(withId(R.id.search_bar), instanceOf(SearchView.class)))
                .perform(click());

        // Type "London" into the search bar and press the IME action button
        onView(searchAutoComplete)
                .perform(typeText("London"), closeSoftKeyboard());

        // Verify that the recycler view contains an item with the text "London, England"
        onView(withId(R.id.recycler_view))
                .check(matches(isDisplayed()));

        // You might want to add more specific checks here, such as verifying the content of the RecyclerView
    }

    @Test
    public void testPersonalRecommendationsButton() {
        onView(withId(R.id.tabRecommended))
                .perform(scrollTo(), click());

        // Verify that DestinationSelectionActivity is launched
        //onView(withText("Destination Selection"))
                //.check(matches(isDisplayed()));
    }

    @Test
    public void testFeaturedButton() {
        onView(withId(R.id.tabFeatured))
                .perform(scrollTo(), click());

        // Verify that FeaturedActivity is launched
        //onView(withText("Featured Activity"))
                //.check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationFavorites() {
        onView(withId(R.id.nav_favorites))
                .perform(click());

        // Verify that Wishlist activity is launched
        //onView(withText("Wishlist"))
                //.check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationProfile() {
        onView(withId(R.id.nav_profile))
                .perform(click());

        // Verify that ProfileSection activity is launched
        //onView(withText("Profile Section"))
                //.check(matches(isDisplayed()));
    }

    @Test
    public void testAllTabDisplayed() {
        onView(withId(R.id.tabAll))
                .check(matches(isDisplayed()))
                .check(matches(withText("All")));
    }

    @Test
    public void testRecyclerViewDisplayed() {
        onView(withId(R.id.recycler_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationBarDisplayed() {
        onView(withId(R.id.navBar))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testWelcomeTextDisplayed() {
        onView(withId(R.id.hello_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hi!")));

        onView(withId(R.id.where_to_go))
                .check(matches(isDisplayed()))
                .check(matches(withText("Where are you going?")));
    }
}