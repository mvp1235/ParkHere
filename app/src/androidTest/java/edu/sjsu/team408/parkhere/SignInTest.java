package edu.sjsu.team408.parkhere;

import android.content.Intent;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

/**
 * Created by MVP on 11/8/2017.
 */

@RunWith(AndroidJUnit4.class)
public class SignInTest {
    private String email, password;

    @Rule
    public ActivityTestRule<MainActivity> signInActivityActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    /**
     * An existing user with email huy123@gmail.com and password yolo123 has already existed in data base
     * Expected to sign in successfully and the profile page should load up all the user's profile information
     * Assuming that no current user login session is active
     */
    @Test
    public void signInSucessfully() {
        email = "huy123@gmail.com";
        password = "yolo123";

        //Click on profile fragment
        onView(withId(R.id.navigation_profile)).perform(click());
        //Click on sign in button (assuming no current login session exists)
        onView(withId(R.id.profileSignInBtn)).perform(click());

        onView(withId(R.id.signInEmail)).perform(typeText(email));
        onView(withId(R.id.signInPassword)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.signInBtn)).perform(click());


        //Allows enough time for the check method below to perform its action
        //Otherwise, the test activity just ends before it can access the email textview on the profile fragment
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        intended(hasComponent(MainActivity.class.getName()));
        onView(withId(R.id.userEmail)).check(matches(withText(email)));
    }

}
