package com.bourdi_bay.WindowsRemote.Settings;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.bourdi_bay.WindowsRemote.R;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment implements SettingsContract.View {

    private static final int REQUEST_ENABLE_BT = 2;

    private SettingsContract.UserActionListener mActionsListener;
    private SharedPreferences.OnSharedPreferenceChangeListener mListenerSharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        updateSummary(R.string.pref_key_tcp_port);
        updateSummary(R.string.pref_key_tcp_ip);
        updateSummary(R.string.pref_key_local_udp_port);
        updateSummary(R.string.pref_key_server_udp_port);

        mActionsListener = new SettingsPresenter(this);
        mListenerSharedPref = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(getString(R.string.pref_key_use_bluetooth))) {
                    final boolean isInUse = sharedPreferences.getBoolean(getString(R.string.pref_key_use_bluetooth), false);
                    if (isInUse) {
                        enableCheckedPreferences(false, R.string.pref_key_use_tcp, R.string.pref_key_use_udp);
                        mActionsListener.enableBluetooth();
                        mActionsListener.retrieveAllBluetoothDevices();
                    }
                } else if (key.equals(getString(R.string.pref_key_use_tcp))) {
                    final boolean isInUse = sharedPreferences.getBoolean(getString(R.string.pref_key_use_tcp), false);
                    if (isInUse) {
                        enableCheckedPreferences(false, R.string.pref_key_use_bluetooth, R.string.pref_key_use_udp);
                    }
                } else if (key.equals(getString(R.string.pref_key_use_udp))) {
                    final boolean isInUse = sharedPreferences.getBoolean(getString(R.string.pref_key_use_udp), false);
                    if (isInUse) {
                        enableCheckedPreferences(false, R.string.pref_key_use_tcp, R.string.pref_key_use_bluetooth);
                    }
                } else if (key.equals(getString(R.string.pref_key_list_bluetooth_devices))) {
                    // When the user clicks on the list of bluetooth devices, we update the list at this moment only.
                    displayCurrentBluetoothDeviceInSummary();
                } else if (key.equals(getString(R.string.pref_key_tcp_port))) {
                    updateSummary(R.string.pref_key_tcp_port);
                } else if (key.equals(getString(R.string.pref_key_tcp_ip))) {
                    updateSummary(R.string.pref_key_tcp_ip);
                } else if (key.equals(getString(R.string.pref_key_local_udp_port))) {
                    updateSummary(R.string.pref_key_local_udp_port);
                } else if (key.equals(getString(R.string.pref_key_server_udp_port))) {
                    updateSummary(R.string.pref_key_server_udp_port);
                }
            }
        };
    }

    private void enableCheckedPreferences(boolean state, int... prefKeys) {
        for (int key : prefKeys) {
            SwitchPreference switchPreference = (SwitchPreference) findPreference(getString(key));
            switchPreference.setChecked(state);
        }
    }

    private void updateSummary(int resourceId) {
        final Preference pref = findPreference(getString(resourceId));
        final String value = pref.getSharedPreferences().getString(getString(resourceId), "");
        pref.setSummary(value);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Update the list of devices available around when we click on the preference.
        final ListPreference listPrefs = (ListPreference) findPreference(getString(R.string.pref_key_list_bluetooth_devices));
        listPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO: Buggy behavior: the first time this takes too long, and as a result the list is not displayed.
                mActionsListener.retrieveAllBluetoothDevices();
                displayCurrentBluetoothDeviceInSummary();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(mListenerSharedPref);
        displayCurrentBluetoothDeviceInSummary();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(mListenerSharedPref);
    }

    @Override
    public void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // Warning: getActivity() is called because with PreferenceFragment
        // it seems intent cannot be verified during the mock tests otherwise.
        // Maybe try out PreferenceFragmentCompat from support-preferences-v7
        getActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void displayListOfBluetoothDevices(ArrayList<CharSequence> entries, ArrayList<CharSequence> entryValues) {
        final ListPreference listPrefs = (ListPreference) findPreference(getString(R.string.pref_key_list_bluetooth_devices));
        final CharSequence[] entriesSequence = new CharSequence[entries.size()];
        entries.toArray(entriesSequence);
        final CharSequence[] entryValuesSequence = new CharSequence[entryValues.size()];
        entryValues.toArray(entryValuesSequence);
        listPrefs.setEntries(entriesSequence);
        listPrefs.setEntryValues(entryValuesSequence);
        listPrefs.setPersistent(true);
    }

    private void displayCurrentBluetoothDeviceInSummary() {
        final ListPreference listPrefs = (ListPreference) findPreference(getString(R.string.pref_key_list_bluetooth_devices));
        CharSequence entry = listPrefs.getEntry();
        listPrefs.setSummary(entry);
    }
}
