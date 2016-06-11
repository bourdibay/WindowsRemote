package com.bourdi_bay.WindowsRemote.Input;

import android.content.Context;
import android.content.SharedPreferences;

import com.bourdi_bay.WindowsRemote.Communication.Bluetooth.BluetoothPreferences;
import com.bourdi_bay.WindowsRemote.Communication.Network.TCPPreferences;
import com.bourdi_bay.WindowsRemote.Communication.Network.UDPPreferences;

public class InputPreferencesBuilder implements InputContract.PreferencesBuilder {

    final private InputContract.View mView;

    public InputPreferencesBuilder(InputContract.View view) {
        mView = view;
    }

    @Override
    public BluetoothPreferences buildBluetoothPreferences(Context context, SharedPreferences sharedPreferences) {
        BluetoothPreferences bluetoothPreferences = BluetoothPreferences.createBluetoothPreferences(context, sharedPreferences);
        if (bluetoothPreferences == null && !BluetoothPreferences.mLastError.isEmpty()) {
            mView.displayError(BluetoothPreferences.mLastError);
        }
        return bluetoothPreferences;
    }

    @Override
    public TCPPreferences buildTCPPreferences(Context context, SharedPreferences sharedPreferences) {
        TCPPreferences tcpPreferences = TCPPreferences.createTCPPreferences(context, sharedPreferences);
        if (tcpPreferences == null && !TCPPreferences.mLastError.isEmpty()) {
            mView.displayError(TCPPreferences.mLastError);
        }
        return tcpPreferences;
    }

    @Override
    public UDPPreferences buildUDPPreferences(Context context, SharedPreferences sharedPreferences) {
        UDPPreferences udpPreferences = UDPPreferences.createUDPPreferences(context, sharedPreferences);
        if (udpPreferences == null && !UDPPreferences.mLastError.isEmpty()) {
            mView.displayError(UDPPreferences.mLastError);
        }
        return udpPreferences;
    }

}
