package com.bourdi_bay.WindowsRemote.Settings;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.bourdi_bay.WindowsRemote.MainActivity;
import com.bourdi_bay.WindowsRemote.R;
import com.bourdi_bay.WindowsRemote.Utils.ActivityFinisher;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.VerificationModes.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationMainActivityToSettingsTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void fromMainActivityToSettings() {
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));

        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());

        intended(hasComponent(SettingsActivity.class.getName()), times(1));

        onView(withText(R.string.title_activity_settings)).check(matches(isDisplayed()));
    }

    @Test
    public void fromMainActivityToSettings_pressBack() {
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()), times(1));
        onView(withText(R.string.title_activity_settings)).check(matches(isDisplayed()));

        Espresso.pressBack();

        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
    }

    @Test
    public void fromMainActivityToSettings_goUpToolBar() {
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()), times(1));
        onView(withText(R.string.title_activity_settings)).check(matches(isDisplayed()));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        // Cleanup opened activities, because they are not clean up automatically
        // and this lead to a lot of failures for the next tests...
        ActivityFinisher.finishOpenActivities();
    }

}
