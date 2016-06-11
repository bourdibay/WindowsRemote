package com.bourdi_bay.WindowsRemote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import com.bourdi_bay.WindowsRemote.Communication.Bluetooth.BluetoothPreferences;
import com.bourdi_bay.WindowsRemote.Input.InputContract;
import com.bourdi_bay.WindowsRemote.Input.InputPreferencesBuilder;
import com.bourdi_bay.WindowsRemote.Utils.BluetoothUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class, SharedPreferences.class, Context.class})
public class InputPreferencesBuilderTest {

    static final String bluetooth_not_enabled = "not enabled";
    static final String bluetooth_not_supported = "not supported";
    static final String bluetooth_not_activated = "not activated";
    static final String no_device_to_connect = "no connect";

    private InputPreferencesBuilder mInputPreferencesBuilder;

    @Mock
    private InputContract.View mInputView;
    @Mock
    private SharedPreferences mSharedPreferences;
    @Mock
    private Context mContext;

    @Before
    public void setupSettingsPresenter() {
        MockitoAnnotations.initMocks(this);
        mInputPreferencesBuilder = new InputPreferencesBuilder(mInputView);

        when(mContext.getString(R.string.bluetooth_preferences_bluetooth_not_enabled)).thenReturn(bluetooth_not_enabled);
        when(mContext.getString(R.string.bluetooth_preferences_bluetooth_not_activated)).thenReturn(bluetooth_not_activated);
        when(mContext.getString(R.string.bluetooth_preferences_bluetooth_not_supported)).thenReturn(bluetooth_not_supported);
        when(mContext.getString(R.string.bluetooth_preferences_no_device_to_connect)).thenReturn(no_device_to_connect);

        when(mSharedPreferences.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(mSharedPreferences.getString(anyString(), anyString())).thenReturn("Something");
    }

    @Test
    public void buildBluetoothPreferences_validPreferences() {
        BluetoothAdapter bluetoothAdapter = BluetoothUtils.setBluetoothAvailable(true);

        BluetoothDevice bluetoothDevice = mock(BluetoothDevice.class);
        when(bluetoothAdapter.getRemoteDevice(anyString())).thenReturn(bluetoothDevice);

        BluetoothPreferences bluetoothPreferences = mInputPreferencesBuilder.buildBluetoothPreferences(mContext, mSharedPreferences);
        assertNotNull(bluetoothPreferences);

        assertEquals(bluetoothDevice, bluetoothPreferences.mBluetoothDevice);
        assertTrue(BluetoothPreferences.mLastError.isEmpty());
    }

    @Test
    public void buildBluetoothPreferences_nullPreferences_notEnabled() {
        BluetoothUtils.setBluetoothAvailable(false);

        BluetoothPreferences bluetoothPreferences = mInputPreferencesBuilder.buildBluetoothPreferences(mContext, mSharedPreferences);

        assertNull(bluetoothPreferences);
        assertEquals(bluetooth_not_enabled, BluetoothPreferences.mLastError);
    }

    @Test
    public void buildBluetoothPreferences_nullPreferences_notSupported() {
        BluetoothUtils.setBluetoothNotSupported();

        BluetoothPreferences bluetoothPreferences = mInputPreferencesBuilder.buildBluetoothPreferences(mContext, mSharedPreferences);

        assertNull(bluetoothPreferences);
        assertEquals(bluetooth_not_supported, BluetoothPreferences.mLastError);
    }

    @Test
    public void buildBluetoothPreferences_nullPreferences_notActivated() {
        when(mSharedPreferences.getBoolean(anyString(), anyBoolean())).thenReturn(false);

        BluetoothPreferences bluetoothPreferences = mInputPreferencesBuilder.buildBluetoothPreferences(mContext, mSharedPreferences);

        assertNull(bluetoothPreferences);
        assertEquals(bluetooth_not_activated, BluetoothPreferences.mLastError);
    }

    @Test
    public void buildBluetoothPreferences_nullPreferences_noDevicesToConnect() {
        BluetoothUtils.setBluetoothAvailable(true);
        when(mSharedPreferences.getString(anyString(), anyString())).thenReturn(null);

        BluetoothPreferences bluetoothPreferences = mInputPreferencesBuilder.buildBluetoothPreferences(mContext, mSharedPreferences);

        assertNull(bluetoothPreferences);
        assertEquals(no_device_to_connect, BluetoothPreferences.mLastError);
    }
}
