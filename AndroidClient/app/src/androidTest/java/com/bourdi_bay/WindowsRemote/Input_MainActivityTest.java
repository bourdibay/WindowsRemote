package com.bourdi_bay.WindowsRemote;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bourdi_bay.WindowsRemote.Input.InputPresenter;
import com.bourdi_bay.WindowsRemote.Utils.LowLevelActions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class Input_MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivityFragment mMainFragment;

    @Before
    public void setUp() {
        Activity activity = mActivityTestRule.getActivity();
        assertNotNull(activity);
        Fragment fragment = activity.getFragmentManager().findFragmentById(R.id.main_fragment_placeholder);
        assertNotNull(fragment);
        mMainFragment = (MainActivityFragment) fragment;
        assertNotNull(mMainFragment);
    }

    @Test
    public void clickRight_pushMessageToMessageQueue() {
        CountDownLatch counter = defineMessageCounter(2);
        onView(withId(R.id.btRightClick)).perform(click());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(1);
        onView(withId(R.id.btRightClick)).perform(LowLevelActions.pressAndHold());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(1);
        onView(withId(R.id.btRightClick)).perform(LowLevelActions.release());
        waitForAllMessagesProcessed(counter);
    }

    @Test
    public void clickLeft_pushMessageToMessageQueue() {
        CountDownLatch counter = defineMessageCounter(2);
        onView(withId(R.id.btLeftClick)).perform(click());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(1);
        onView(withId(R.id.btLeftClick)).perform(LowLevelActions.pressAndHold());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(1);
        onView(withId(R.id.btLeftClick)).perform(LowLevelActions.release());
        waitForAllMessagesProcessed(counter);
    }

    @Test
    public void touchView_DownAndRelease_noMessagePushed() {
        CountDownLatch counter = defineMessageCounter(0);
        onView(withId(R.id.touchView)).perform(LowLevelActions.pressAndHold());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(0); // Do not handle release, not useful.
        onView(withId(R.id.touchView)).perform(LowLevelActions.release());
        waitForAllMessagesProcessed(counter);
    }

    @Test
    public void touchView_DownAndMove_pushMessageToMessageQueue() {
        CountDownLatch counter = defineMessageCounter(0);
        onView(withId(R.id.touchView)).perform(LowLevelActions.pressAndHold());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(1);
        onView(withId(R.id.touchView)).perform(LowLevelActions.move());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(2);
        onView(withId(R.id.touchView)).perform(LowLevelActions.move());
        onView(withId(R.id.touchView)).perform(LowLevelActions.move());
        waitForAllMessagesProcessed(counter);

        counter = defineMessageCounter(0); // Do not handle release, not useful.
        onView(withId(R.id.touchView)).perform(LowLevelActions.release());
        waitForAllMessagesProcessed(counter);
    }

    private CountDownLatch defineMessageCounter(int nbExpectedMessages) {
        InputPresenter inputPresenter = (InputPresenter) mMainFragment.mInputPresenter;
        final CountDownLatch counter = new CountDownLatch(nbExpectedMessages);
        inputPresenter.mHandler = new Handler(inputPresenter.mHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                counter.countDown();
            }
        };
        return counter;
    }

    private void waitForAllMessagesProcessed(CountDownLatch counter) {
        try {
            boolean success = counter.await(5, TimeUnit.SECONDS); // wait for 5 sec
            assertTrue(success);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        LowLevelActions.tearDown();
    }
}
