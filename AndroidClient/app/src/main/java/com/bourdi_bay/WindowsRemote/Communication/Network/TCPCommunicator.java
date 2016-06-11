package com.bourdi_bay.WindowsRemote.Communication.Network;

import android.content.Context;
import android.util.Log;

import com.bourdi_bay.WindowsRemote.Communication.Communicator;
import com.bourdi_bay.WindowsRemote.Communication.CommunicatorException;
import com.bourdi_bay.WindowsRemote.Communication.MessageBroadcast;
import com.bourdi_bay.WindowsRemote.LoggerTags;
import com.bourdi_bay.WindowsRemote.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPCommunicator implements Communicator {

    private final String mServerIP;
    private final int mServerPort;
    private Socket mSocket = null;
    private OutputStream mOutputStream = null;

    public TCPCommunicator(String serverIP, int serverPort) {
        mServerIP = serverIP;
        mServerPort = serverPort;
    }

    private Socket createSocket() throws IOException {
        InetAddress serverAddr = InetAddress.getByName(mServerIP);
        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverAddr, mServerPort);
        socket.connect(inetSocketAddress, 3000);
        //socket = new Socket(serverAddr, mServerPort); // no timeout :(
        return socket;
    }

    @Override
    public void prepare(Context context) throws CommunicatorException {
        try {
            mSocket = createSocket();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommunicatorException(context.getString(R.string.tcp_communicator_socket_cannot_connect));
        }

        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommunicatorException(context.getString(R.string.tcp_communicator_socket_cannot_output));
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
        Log.d(LoggerTags.COMMUNICATION_TAG, "TCP Message sent !");
    }

    @Override
    public void finish() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
