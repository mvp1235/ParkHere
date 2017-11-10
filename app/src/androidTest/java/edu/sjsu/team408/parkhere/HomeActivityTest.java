package edu.sjsu.team408.parkhere;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
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


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.navigation_profile)).check(matches(isDisplayed()));
    }

    @Test
    public void ListingButtonTest() {
        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressStreetNumber)).perform(click());

        onView(withId(R.id.listingAddressStreetNumber)).check(matches(isDisplayed()));
    }


    @Test
    public void listingCorrectStreetAddressInputTest(){
        String streetAdress = "2770 Bristol Dr";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressStreetNumber)).perform(click(), clearText(), replaceText(streetAdress));

        onView(withId(R.id.listingAddressStreetNumber)).check(matches(withText(streetAdress)));

    }


    @Test
    public void listingCorrectCityInputTest() {
        String city = "San Jose";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressCity)).perform(click(), clearText(), replaceText(city));

        onView(withId(R.id.listingAddressCity)).check(matches(withText(city)));


    }

    @Test
    public void listingCorrectlyStateInputTest() {
        String state = "CA";

        onView(withId(R.id.navigation_profile)).perform(click());

        onView(withId(R.id.newListingBtn)).perform(click());

        onView(withId(R.id.listingAddressState)).perform(click(), clearText(), replaceText(state));

        onView(withId(R.id.listingAddressState)).check(matches(withText(state)));
    }
}
