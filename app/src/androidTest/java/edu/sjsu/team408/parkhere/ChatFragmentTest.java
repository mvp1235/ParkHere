package edu.sjsu.team408.parkhere;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
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
        onView(withId(R.id.input_message_editText))
                .perform(typeText("Hello"));
        closeSoftKeyboard();
        onView(withId(R.id.new_message_button))
                .perform(click());
        onView(withId(R.id.input_message_editText))
                .perform(typeText("I am good."));
        closeSoftKeyboard();
        onView(withId(R.id.new_message_button))
                .perform(click());
        slowDown1Secs();
    }

    @Test
    public void createANewChatTest() {
        slowDown1Secs();
        onView(withId(R.id.navigation_messaging))
                .perform(click());
        slowDown1Secs();
        onView(withId(R.id.new_message_button))
                .perform(click());
        slowDown1Secs();
        onView(withId(R.id.toAutoCompleteTextView))
                .perform(typeText("huy1"));
        closeSoftKeyboard();
        slowDown1Secs();
        onView(withId(R.id.input_message_editText))
                .perform(typeText("hello"));
        closeSoftKeyboard();
        onView(withId(R.id.send_message_button))
                .perform(click());
        slowDown1Secs();
        onView(withId(R.id.input_message_editText))
                .perform(typeText("how are"));
        closeSoftKeyboard();
        slowDown1Secs();
        onView(withId(R.id.send_message_button))
                .perform(click());
        slowDown1Secs();
        pressBack();
        slowDown1Secs();
    }

    // A function to delay the automated test in order to see it in action.
    private void slowDown1Secs() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}