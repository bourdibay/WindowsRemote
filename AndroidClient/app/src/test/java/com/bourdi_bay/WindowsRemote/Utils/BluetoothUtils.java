package com.bourdi_bay.WindowsRemote.Utils;

import android.bluetooth.BluetoothAdapter;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

public class BluetoothUtils {

    static public void setBluetoothNotSupported() {
        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(null);
    }

    static public BluetoothAdapter setBluetoothAvailable(boolean isEnabled) {
        BluetoothAdapter adapter = mock(BluetoothAdapter.class);
        when(adapter.isEnabled()).thenReturn(isEnabled);

        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(adapter);
        return adapter;
    }
}
