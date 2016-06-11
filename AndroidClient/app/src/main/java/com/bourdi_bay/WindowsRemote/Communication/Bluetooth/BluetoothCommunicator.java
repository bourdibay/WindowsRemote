package com.bourdi_bay.WindowsRemote.Communication.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.bourdi_bay.WindowsRemote.Communication.Communicator;
import com.bourdi_bay.WindowsRemote.Communication.CommunicatorException;
import com.bourdi_bay.WindowsRemote.Communication.MessageBroadcast;
import com.bourdi_bay.WindowsRemote.LoggerTags;
import com.bourdi_bay.WindowsRemote.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothCommunicator implements Communicator {

    private final BluetoothDevice mDeviceToConnect;
    private BluetoothSocket mSocket = null;
    private OutputStream mOutputStream;

    public BluetoothCommunicator(BluetoothDevice device) {
        mDeviceToConnect = device;
    }

    @Override
    public void prepare(Context context) throws CommunicatorException {
        if (mDeviceToConnect.getBondState() != BluetoothDevice.BOND_BONDED) {
            throw new CommunicatorException(context.getString(R.string.bluetooth_communicator_device_not_bonded));
        }

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            mSocket = mDeviceToConnect.createRfcommSocketToServiceRecord(UUID.fromString(context.getString(R.string.UUID_value)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommunicatorException(context.getString(R.string.bluetooth_communicator_cannot_create_connection));
        }

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
            connectException.printStackTrace();
            throw new CommunicatorException(context.getString(R.string.bluetooth_communicator_cannot_connect));
        }

        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommunicatorException(context.getString(R.string.bluetooth_communicator_cannot_output_stream));
        }
    }

    @Override
    public void sendMessage(MessageBroadcast message) {
        final String str = message.toString();
        final byte[] bytes = str.getBytes();
        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LoggerTags.COMMUNICATION_TAG, "Bluetooth Message sent !");
    }

    @Override
    public void finish() {
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
