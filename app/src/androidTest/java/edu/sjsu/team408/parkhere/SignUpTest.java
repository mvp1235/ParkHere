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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.not;

/**
 * Created by MVP on 11/9/2017.
 */
@RunWith(AndroidJUnit4.class)
public class SignUpTest {
    private String email, password;

    @Rule
    public ActivityTestRule<MainActivity> signUnActivityActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    /*
     * The email address "huy2@gmail.com" has not yet been registered for an account yet.
     * Expected to sign up successfully and the profile page should load up all the user's profile information
     * Assuming that no current user login session is active, and that no account associated with the email provided yet
     */
    @Test
    public void signUpSuccessfully() {
        email = "huy2@gmail.com";
        password = "yolo123";

        //Click on profile fragment
        onView(withId(R.id.navigation_profile)).perform(click());
        //Click on sign in button (assuming no current login session exists)
        onView(withId(R.id.profileSignUpBtn)).perform(click());

        onView(withId(R.id.signUpEmail)).perform(typeText(email));
        onView(withId(R.id.signUpPassword)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.signUpBtn)).perform(click());

        //Allows enough time for the check method below to perform its action
        //Otherwise, the test activity just ends before it can access the email textview on the profile fragment
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.userEmail)).check(matches(withText(email)));
    }

    /*
     * The email address "huy123@gmail.com" has already exist in the user database
     * Expected to sign up unsuccessfully and the user will be asked to provide another email address.
     * Assuming that no current user login session is active, and that the email address provided by user has already exist in the user dataabase
     */
    @Test
    public void signUpFailEmailExist() {
        email = "huy123@gmail.com";
        password = "yolo123";

        //Click on profile fragment
        onView(withId(R.id.navigation_profile)).perform(click());
        //Click on sign in button (assuming no current login session exists)
        onView(withId(R.id.profileSignUpBtn)).perform(click());

        onView(withId(R.id.signUpEmail)).perform(typeText(email));
        onView(withId(R.id.signUpPassword)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.signUpBtn)).perform(click());

        //Allows enough time for the check method below to perform its action
        //Otherwise, the test activity just ends before it can access the email textview on the profile fragment
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.signUpBtn)).check(matches(isDisplayed()));
    }
}
