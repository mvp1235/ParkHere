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
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by robg on 11/20/17.
 */

@RunWith(AndroidJUnit4.class)
public class ChatFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new
            ActivityTestRule<>(MainActivity.class);

    @Test
    public void typeMessageClickSend() {
        onView(withId(R.id.navigation_messaging))
                .perform(click());
        onView(withId(R.id.msg_type))
                .perform(typeText("Hello"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_chat_send))
                .perform(click());
        onView(withId(R.id.msg_type))
                .perform(typeText("I am good."));
        closeSoftKeyboard();
        onView(withId(R.id.btn_chat_send))
                .perform(click());
        slowDown2Secs();
    }

    // A function to delay the automated test in order to see it in action.
    private void slowDown2Secs() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}