package com.bourdi_bay.WindowsRemote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.bourdi_bay.WindowsRemote.Communication.Bluetooth.BluetoothCommunicator;
import com.bourdi_bay.WindowsRemote.Communication.CommunicatorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UUID.class, Context.class})
public class BluetoothCommunicatorTest {

    private final String device_not_bonded = "no device bonded";
    private final String cannot_create_connection = "no create connection";
    private final String cannot_connect = "cannot connect";
    private final String cannot_output_stream = "cannot output stream";
    private final String uuid_fake = "B62C4E8D-62CC-404b-BBBF-BF3E3BBB1374";
    private BluetoothCommunicator mBluetoothCommunicator;

    @Mock
    private BluetoothDevice mBluetoothDevice;
    @Mock
    private Context mContext;

    @Before
    public void setupSettingsPresenter() {
        MockitoAnnotations.initMocks(this);

        when(mContext.getString(R.string.UUID_value)).thenReturn(uuid_fake);

        when(mContext.getString(R.string.bluetooth_communicator_device_not_bonded)).thenReturn(device_not_bonded);
        when(mContext.getString(R.string.bluetooth_communicator_cannot_create_connection)).thenReturn(cannot_create_connection);
        when(mContext.getString(R.string.bluetooth_communicator_cannot_connect)).thenReturn(cannot_connect);
        when(mContext.getString(R.string.bluetooth_communicator_cannot_output_stream)).thenReturn(cannot_output_stream);

        mBluetoothCommunicator = new BluetoothCommunicator(mBluetoothDevice);
    }

    @Test
    public void prepare_failBondedDevices() {
        when(mBluetoothDevice.getBondState()).thenReturn(BluetoothDevice.BOND_NONE);
        try {
            mBluetoothCommunicator.prepare(mContext);
            fail("Should have thrown exception");
        } catch (CommunicatorException e) {
            assertEquals(device_not_bonded, e.getMessage());
        }
    }

    @Test
    public void prepare_cannotCreateConnection() {
        when(mBluetoothDevice.getBondState()).thenReturn(BluetoothDevice.BOND_BONDED);
        try {
            when(mBluetoothDevice.createRfcommSocketToServiceRecord(any(UUID.class))).thenThrow(new IOException());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        try {
            mBluetoothCommunicator.prepare(mContext);
            fail("Should have thrown exception");
        } catch (CommunicatorException e) {
            assertEquals(cannot_create_connection, e.getMessage());
        }
    }

    @Test
    public void prepare_cannotConnect() {
        when(mBluetoothDevice.getBondState()).thenReturn(BluetoothDevice.BOND_BONDED);
        BluetoothSocket socket = mock(BluetoothSocket.class);
        try {
            when(mBluetoothDevice.createRfcommSocketToServiceRecord(any(UUID.class))).thenReturn(socket);
            doThrow(new IOException()).when(socket).connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mBluetoothCommunicator.prepare(mContext);
            fail("Should have thrown exception");
        } catch (CommunicatorException e) {
            assertEquals(cannot_connect, e.getMessage());
        }
    }

    @Test
    public void prepare_cannotOutputStream() {
        when(mBluetoothDevice.getBondState()).thenReturn(BluetoothDevice.BOND_BONDED);
        BluetoothSocket socket = mock(BluetoothSocket.class);
        try {
            when(mBluetoothDevice.createRfcommSocketToServiceRecord(any(UUID.class))).thenReturn(socket);
            doNothing().when(socket).connect();
            when(socket.getOutputStream()).thenThrow(new IOException());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mBluetoothCommunicator.prepare(mContext);
            fail("Should have thrown exception");
        } catch (CommunicatorException e) {
            assertEquals(cannot_output_stream, e.getMessage());
        }
    }

    @Test
    public void prepare_everythingOK() {
        when(mBluetoothDevice.getBondState()).thenReturn(BluetoothDevice.BOND_BONDED);
        BluetoothSocket socket = mock(BluetoothSocket.class);
        try {
            when(mBluetoothDevice.createRfcommSocketToServiceRecord(any(UUID.class))).thenReturn(socket);
            doNothing().when(socket).connect();
            OutputStream out = mock(OutputStream.class);
            when(socket.getOutputStream()).thenReturn(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mBluetoothCommunicator.prepare(mContext);
        } catch (CommunicatorException e) {
            e.printStackTrace();
            fail("Should NOT have thrown exception");
        }
    }
}
