package com.bourdi_bay.WindowsRemote.Settings;

import java.util.ArrayList;

public interface SettingsContract {

    interface View {
        void enableBluetooth();

        void displayListOfBluetoothDevices(ArrayList<CharSequence> entries, ArrayList<CharSequence> entryValues);
    }

    interface UserActionListener {
        void enableBluetooth();

        void retrieveAllBluetoothDevices();
    }
}
