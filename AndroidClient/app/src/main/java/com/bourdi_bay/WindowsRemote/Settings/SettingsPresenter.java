package com.bourdi_bay.WindowsRemote.Settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bourdi_bay.WindowsRemote.LoggerTags;

import java.util.ArrayList;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SettingsPresenter implements SettingsContract.UserActionListener {

    private final SettingsContract.View mSettingsView;

    public SettingsPresenter(@NonNull SettingsContract.View settingsView) {
        mSettingsView = checkNotNull(settingsView);
    }

    @Override
    public void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e(LoggerTags.SETTINGS_TAG, "Cannot get BluetoothAdapter");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            mSettingsView.enableBluetooth();
        }
    }

    @Override
    public void retrieveAllBluetoothDevices() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(LoggerTags.SETTINGS_TAG, "Cannot get BluetoothAdapter");
            return;
        }
        final Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if (bondedDevices != null) {
            ArrayList<CharSequence> entries = new ArrayList<>(bondedDevices.size());
            ArrayList<CharSequence> entryValues = new ArrayList<>(bondedDevices.size());

            for (BluetoothDevice device : bondedDevices) {
                entries.add(device.getName());
                entryValues.add(device.getAddress());
            }

            mSettingsView.displayListOfBluetoothDevices(entries, entryValues);
        }
    }

}
