package com.bourdi_bay.WindowsRemote.Utils;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.espresso.action.Press;
import android.view.MotionEvent;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

// From https://stackoverflow.com/questions/32010927/android-espresso-make-assertion-while-button-is-kept-pressed
public class LowLevelActions {
    static MotionEvent sMotionEventDownHeldView = null;

    public static LowLevelActions.PressAndHoldAction pressAndHold() {
        return new LowLevelActions.PressAndHoldAction();
    }

    public static LowLevelActions.ReleaseAction release() {
        return new LowLevelActions.ReleaseAction();
    }

    public static LowLevelActions.MoveAction move() {
        return new LowLevelActions.MoveAction();
    }

    public static void tearDown() {
        sMotionEventDownHeldView = null;
    }

    static class PressAndHoldAction implements ViewAction {
        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(90); // Like GeneralClickAction
        }

        @Override
        public String getDescription() {
            return "Press and hold action";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            if (sMotionEventDownHeldView != null) {
                throw new AssertionError("Only one view can be held at a time");
            }

            float[] precision = Press.FINGER.describePrecision();
            float[] coords = GeneralLocation.CENTER.calculateCoordinates(view);
            sMotionEventDownHeldView = MotionEvents.sendDown(uiController, coords, precision).down;
        }
    }

    static class ReleaseAction implements ViewAction {
        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(90);  // Like GeneralClickAction
        }

        @Override
        public String getDescription() {
            return "Release action";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            if (sMotionEventDownHeldView == null) {
                throw new AssertionError("Before calling release(), you must call pressAndHold() on a view");
            }

            float[] coords = GeneralLocation.CENTER.calculateCoordinates(view);
            MotionEvents.sendUp(uiController, sMotionEventDownHeldView, coords);
        }
    }

    static class MoveAction implements ViewAction {

        static float[] mPreviousCoord = null;
        static DirectionMove mPreviousDirection = DirectionMove.LEFT;

        @Override
        public Matcher<View> getConstraints() {
            return isDisplayingAtLeast(90);  // Like GeneralClickAction
        }

        @Override
        public String getDescription() {
            return "Move action";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            if (sMotionEventDownHeldView == null) {
                throw new AssertionError("Before calling release(), you must call pressAndHold() on a view");
            }

            if (mPreviousCoord == null) {
                mPreviousCoord = GeneralLocation.CENTER.calculateCoordinates(view);
            }
            if (mPreviousDirection == DirectionMove.LEFT) {
                mPreviousCoord[0]++;
                mPreviousDirection = DirectionMove.RIGHT;
            } else {
                mPreviousCoord[0]--;
                mPreviousDirection = DirectionMove.LEFT;
            }

            MotionEvents.sendMovement(uiController, sMotionEventDownHeldView, mPreviousCoord);
        }

        enum DirectionMove {
            LEFT,
            RIGHT
        }
    }

}
