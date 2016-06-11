package com.bourdi_bay.WindowsRemote.Communication.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bourdi_bay.WindowsRemote.LoggerTags;
import com.bourdi_bay.WindowsRemote.R;

public class BluetoothPreferences {
    public static String mLastError = "";
    public BluetoothDevice mBluetoothDevice;

    public BluetoothPreferences(BluetoothDevice device) {
        mBluetoothDevice = device;
    }

    public static BluetoothPreferences createBluetoothPreferences(Context context, SharedPreferences sharedPreferences) {
        mLastError = "";

        final boolean useBluetooth = sharedPreferences.getBoolean(context.getString(R.string.pref_key_use_bluetooth), false);
        if (!useBluetooth) {
            mLastError = context.getString(R.string.bluetooth_preferences_bluetooth_not_activated);
            return null;
        }

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            mLastError = context.getString(R.string.bluetooth_preferences_bluetooth_not_supported);
            return null;
        }
        if (!bluetoothAdapter.isEnabled()) {
            mLastError = context.getString(R.string.bluetooth_preferences_bluetooth_not_enabled);
            return null;
        }

        final String bluetoothDeviceToConnect = sharedPreferences.getString(context.getString(R.string.pref_key_list_bluetooth_devices), null);
        if (bluetoothDeviceToConnect == null) {
            Log.e(LoggerTags.COMMUNICATION_TAG, "Cannot get the bluetooth device to connect in the shared preferences");
            mLastError = context.getString(R.string.bluetooth_preferences_no_device_to_connect);
            return null;
        }

        try {
            final BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(bluetoothDeviceToConnect);
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();
            final BluetoothPreferences preferences = new BluetoothPreferences(remoteDevice);
            Log.d(LoggerTags.COMMUNICATION_TAG, "Remote device used: " + remoteDevice);
            return preferences;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
