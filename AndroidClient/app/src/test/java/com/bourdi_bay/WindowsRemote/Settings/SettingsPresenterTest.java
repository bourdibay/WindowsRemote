package com.bourdi_bay.WindowsRemote.Settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.bourdi_bay.WindowsRemote.Utils.BluetoothUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class}) // Prepare the static classes for mocking
public class SettingsPresenterTest {

    private SettingsPresenter mSettingsPresenter;

    @Mock
    private SettingsContract.View mSettingsView;
    @Captor
    private ArgumentCaptor<ArrayList<CharSequence>> captor;

    @Before
    public void setupSettingsPresenter() {
        MockitoAnnotations.initMocks(this);
        mSettingsPresenter = new SettingsPresenter(mSettingsView);
    }

    ///////////////////////////////////////////////
    // Bluetooth preferences
    ///////////////////////////////////////////////

    @Test
    public void enableBluetooth_bluetoothIsDisabled() {
        BluetoothUtils.setBluetoothAvailable(false);
        mSettingsPresenter.enableBluetooth();
        verify(mSettingsView).enableBluetooth();
    }

    @Test
    public void enableBluetooth_bluetoothIsAlreadyEnabled() {
        BluetoothUtils.setBluetoothAvailable(true);
        mSettingsPresenter.enableBluetooth();
        verify(mSettingsView, never()).enableBluetooth();
    }

    @Test
    public void enableBluetooth_bluetoothNotAvailable() {
        BluetoothUtils.setBluetoothNotSupported();
        mSettingsPresenter.enableBluetooth();
        verify(mSettingsView, never()).enableBluetooth();
    }

    @Test
    public void getBluetoothDevices_showListDevices() {
        final BluetoothAdapter adapter = BluetoothUtils.setBluetoothAvailable(true);
        final int nbDevices = 2;
        final Set<BluetoothDevice> devices = new HashSet<>();
        final ArrayList<CharSequence> entries = new ArrayList<>(nbDevices);
        final ArrayList<CharSequence> entryValues = new ArrayList<>(nbDevices);
        for (int i = 0; i < nbDevices; ++i) {
            final BluetoothDevice device = mock(BluetoothDevice.class);
            final String addr = "address_" + i;
            final String name = "name_" + i;
            when(device.getAddress()).thenReturn(addr);
            when(device.getName()).thenReturn(name);
            devices.add(device);
            entries.add(name);
            entryValues.add(addr);
        }
        when(adapter.getBondedDevices()).thenReturn(devices);

        mSettingsPresenter.retrieveAllBluetoothDevices();

        verify(mSettingsView).displayListOfBluetoothDevices(
                argThat(new EqualityArgumentMatcher<>(entries)),
                argThat(new EqualityArgumentMatcher<>(entryValues))
        );
    }

    @Test
    public void getBluetoothDevices_bluetoothNotAvailable() {
        BluetoothUtils.setBluetoothNotSupported();
        mSettingsPresenter.retrieveAllBluetoothDevices();
        verify(mSettingsView, never()).displayListOfBluetoothDevices(captor.capture(), captor.capture());
    }

}

class EqualityArgumentMatcher<T> extends ArgumentMatcher<T> {
    T thisObject;

    public EqualityArgumentMatcher(T left) {
        this.thisObject = left;
    }

    @Override
    public boolean matches(Object argument) {
        AbstractCollection left = (AbstractCollection) thisObject;
        AbstractCollection right = (AbstractCollection) argument;
        return left.size() == right.size() && left.containsAll(right);
    }
}
