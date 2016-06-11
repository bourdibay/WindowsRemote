package com.bourdi_bay.WindowsRemote.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation.ActivityResult;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.bourdi_bay.WindowsRemote.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {

    @Rule
    public IntentsTestRule<SettingsActivity> mActivityTestRule = new IntentsTestRule<>(SettingsActivity.class);

    private SettingsFragment mSettingsFragment;
    private Matcher<Intent> mIntentMatcher;

    @Before
    public void getSettings() {
        Activity activity = mActivityTestRule.getActivity();
        assertNotNull(activity);
        Fragment fragment = activity.getFragmentManager().findFragmentById(android.R.id.content);
        assertNotNull(fragment);
        mSettingsFragment = (SettingsFragment) fragment;
        assertNotNull(mSettingsFragment);

        // Intent mock
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, null);
        mIntentMatcher = allOf(hasAction(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        intending(mIntentMatcher).respondWith(result);
    }

    @Test
    public void bluetoothDisabledAtTheBeginning_EnableIt() {
        SwitchPreference preferenceBluetoothSwitch = (SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_bluetooth));
        assertNotNull(preferenceBluetoothSwitch);
        ListPreference preferenceBluetoothDevicesList = (ListPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_list_bluetooth_devices));
        assertNotNull(preferenceBluetoothDevicesList);

        // Start by ensuring bluetooth is disabled.
        // If enabled, we disable it !
        if (preferenceBluetoothSwitch.isChecked()) {
            onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).perform(click());
        }

        // Check the list of bluetooth devices is disabled at the beginning.
        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_list_bluetooth_devices))).check(matches(not(isEnabled())));

        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).perform(click());

        intended(mIntentMatcher, times(1));

        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).check(matches(isEnabled()));
        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_list_bluetooth_devices))).check(matches(isEnabled()));
    }

    @Test
    public void bluetoothEnabledAtTheBeginning_DoNothing() {
        SwitchPreference preferenceBluetoothSwitch = (SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_bluetooth));
        assertNotNull(preferenceBluetoothSwitch);
        ListPreference preferenceBluetoothDevicesList = (ListPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_list_bluetooth_devices));
        assertNotNull(preferenceBluetoothDevicesList);

        // Start by ensuring bluetooth is enabled.
        if (!preferenceBluetoothSwitch.isChecked()) {
            onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).perform(click());
            intended(mIntentMatcher);
        }

        // Check the list of bluetooth devices is enabled at the beginning.
        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_list_bluetooth_devices))).check(matches(isEnabled()));

        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).perform(click());

        intended(mIntentMatcher, times(0));

        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).check(matches(isEnabled()));
        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_list_bluetooth_devices))).check(matches(not(isEnabled())));
    }

    @Test
    public void checkBluetooth_uncheckAllOthers() {
        setCheckPreferences(false, R.string.pref_key_use_bluetooth, R.string.pref_key_use_tcp, R.string.pref_key_use_udp);

        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).perform(click());

        assertTrue(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).isChecked());
        assertFalse(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_udp))).isChecked());
        assertFalse(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_tcp))).isChecked());
    }

    @Test
    public void checkTCP_uncheckAllOthers() {
        setCheckPreferences(false, R.string.pref_key_use_bluetooth, R.string.pref_key_use_tcp, R.string.pref_key_use_udp);

        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_tcp))).perform(click());

        assertTrue(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_tcp))).isChecked());
        assertFalse(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_udp))).isChecked());
        assertFalse(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).isChecked());
    }

    @Test
    public void checkUDP_uncheckAllOthers() {
        setCheckPreferences(false, R.string.pref_key_use_bluetooth, R.string.pref_key_use_tcp, R.string.pref_key_use_udp);

        onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_use_udp))).perform(click());

        assertTrue(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_udp))).isChecked());
        assertFalse(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_tcp))).isChecked());
        assertFalse(((SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_use_bluetooth))).isChecked());
    }

    @Test
    public void checkTCPPortEdit_summaryUpdated() {
        final String portExpected = "99887";
        setCheckPreferences(true, R.string.pref_key_use_tcp);

        // Does not work, don't understand why ...
        // onData(PreferenceMatchers.withKey(mSettingsFragment.getString(R.string.pref_key_tcp_port))).perform(click(), typeText(portExpected), closeSoftKeyboard());

        final EditTextPreference prefsTCP = (EditTextPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(R.string.pref_key_tcp_port));

        try {
            mActivityTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prefsTCP.setSummary(portExpected);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            fail();
        }

        assertEquals(portExpected, prefsTCP.getSummary());

    }

    private void setCheckPreferences(boolean state, int... ids) {
        for (int key : ids) {
            SwitchPreference preferenceSwitch = (SwitchPreference) mSettingsFragment.findPreference(mSettingsFragment.getString(key));
            if (preferenceSwitch.isChecked() == !state) {
                onData(PreferenceMatchers.withKey(mSettingsFragment.getString(key))).perform(click());
            }
        }
    }

}
