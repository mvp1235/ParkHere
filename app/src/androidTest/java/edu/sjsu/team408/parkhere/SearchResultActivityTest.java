package edu.sjsu.team408.parkhere;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by robg on 11/9/17.
 */
@RunWith(AndroidJUnit4.class)
public class SearchResultActivityTest {
    private String dateStringToBeEntered;
    private String locationStringToBeTyped;

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new
            ActivityTestRule<>(MainActivity.class);
    @Before
    public void initStringInput() {
        dateStringToBeEntered = "11-10-2017";
        locationStringToBeTyped = "64 S 4th St, San Jose";
    }

    @Test
    public void clickSearchBeforeFillingLocationAndDate() {
        onView(withId(R.id.searchBtn))
                .perform(click());
    }
    @Test
    public void clickSearchWithFillingOnlyDateButNoLocation() {
        HomeFragment.setDate(dateStringToBeEntered);
        slowDown2Secs();
        onView(withId(R.id.searchBtn))
                .perform(click());
        slowDown2Secs();
    }
    @Test
    public void clickSearchWithFillingBothDateAndLocation() {
        slowDown2Secs();
        onView(withId(R.id.locationSearchTerm))
                .perform(typeText(locationStringToBeTyped));
        slowDown2Secs();
        closeSoftKeyboard();
        HomeFragment.setDate(dateStringToBeEntered);
        slowDown2Secs();
        onView(withId(R.id.searchBtn))
                .perform(click());
        slowDown2Secs();

    }
    @Test
    public void searchWithDifferentCriteriaAfter1stSearch() {}

    private void slowDown2Secs() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
