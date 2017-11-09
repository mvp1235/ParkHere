package edu.sjsu.team408.parkhere;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by DuocNguyen on 11/9/17.
 */

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> homeActivityTestActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);



    /**
     *
     */
    @Test
    public void transitionBetweenTabsTest() {

        onView(withId(R.id.navigation_home)).perform(click());

        onView(withId(R.id.navigation_history)).perform(click());

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.navigation_profile)).check(matches(isDisplayed()));
    }

    @Test
    public void listingCorrectStreetAddressInputTest(){
        String streetAdress = "2770 Bristol Dr";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressStreetNumber)).perform(typeText(streetAdress));

        closeSoftKeyboard();

        onView(withId(R.id.listingAddressStreetNumber)).check(matches(withText(streetAdress)));

    }

    @Test
    public void listingCorrectPriceInputTest(){
        String price = "100";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingPrice)).perform(typeText(price));

        closeSoftKeyboard();

        onView(withId(R.id.listingPrice)).check(matches(withText(price)));

    }

    @Test
    public void listingCorrectCityInputTest() {
        String city = "San Jose";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressCity)).perform(typeText(city));

        closeSoftKeyboard();

        onView(withId(R.id.listingAddressCity)).check(matches(withText(city)));


    }

    @Test
    public void listingCorrectlyStateInputTest() {
        String state = "CA";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressState)).perform(typeText(state));

        closeSoftKeyboard();

        onView(withId(R.id.listingAddressState)).check(matches(withText(state)));
    }

    @Test
    public void listingCorrectZipCodeInputTest() {
        String zipcode = "95127";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressZipCode)).perform(typeText(zipcode));

        closeSoftKeyboard();

        onView(withId(R.id.listingAddressZipCode)).check(matches(withText(zipcode)));
    }
}
