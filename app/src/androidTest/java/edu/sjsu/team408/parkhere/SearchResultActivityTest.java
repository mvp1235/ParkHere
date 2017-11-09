package edu.sjsu.team408.parkhere;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by robg on 11/9/17.
 */
@RunWith(AndroidJUnit4.class)
public class SearchResultActivityTest {
    private String dateStringToBeTyped;
    private String locationStringToBeTyped;

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new
            ActivityTestRule<>(MainActivity.class);
    @Before
    public void initStringInput() {
        dateStringToBeTyped = "11-10-2017";
        locationStringToBeTyped = "64 S 4th St, San Jose";
    }

    @Test
    public void clickSearchBeforeFillingLocationAndDate() {
        onView(withId(R.id.searchBtn))
                .perform(click());
        intended(hasComponent(SearchResultActivity.class.getName()));
    }
    @Test
    public void clickSearchWithFillingOnlyDateButNoLocation() {}
    @Test
    public void clickSearchWithFillingBothDateAndLocation() {}
    @Test
    public void searchWithDifferentCriteriaAfter1stSearch() {}

}
