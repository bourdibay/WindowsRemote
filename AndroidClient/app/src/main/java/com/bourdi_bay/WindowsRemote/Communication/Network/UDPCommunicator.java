package com.bourdi_bay.WindowsRemote.Communication.Network;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.bourdi_bay.WindowsRemote.Communication.Communicator;
import com.bourdi_bay.WindowsRemote.Communication.CommunicatorException;
import com.bourdi_bay.WindowsRemote.Communication.MessageBroadcast;
import com.bourdi_bay.WindowsRemote.LoggerTags;
import com.bourdi_bay.WindowsRemote.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPCommunicator implements Communicator {

    private DatagramSocket mClientSocket = null;
    private InetAddress mIPAddress = null;
    private int mLocalPort;
    private int mServerPort;

    public UDPCommunicator(int localPort, int serverPort) {
        mLocalPort = localPort;
        mServerPort = serverPort;
    }

    @Override
    public void prepare(Context context) throws CommunicatorException {
        if (mClientSocket == null || mClientSocket.isClosed()) {
            try {
                mClientSocket = new DatagramSocket(mLocalPort);
            } catch (SocketException e) {
                e.printStackTrace();
                throw new CommunicatorException(context.getString(R.string.udp_communicator_socket_cannot_create));
            }

            //TODO: put ip address in settings
            //mIPAddress = InetAddress.getByName("XX.XX.XX.XX");
            try {
                mIPAddress = getBroadcastAddress(context);
            } catch (CommunicatorException e) {
                mClientSocket.close();
                throw e;
            }
        }
    }

    InetAddress getBroadcastAddress(Context context) throws CommunicatorException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            throw new CommunicatorException(context.getString(R.string.udp_communicator_cannot_retrieve_dhcp));
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        try {
            return InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new CommunicatorException(context.getString(R.string.udp_communicator_cannot_retrieve_addr_from_dhcp));
        }
    }

    @Override
    public void sendMessage(MessageBroadcast message) {
        final String str = message.toString();
        final DatagramPacket packet = new DatagramPacket(str.getBytes(), str.getBytes().length,
                mIPAddress, mServerPort);
        try {
            mClientSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LoggerTags.COMMUNICATION_TAG, "UDP Message sent !");
    }

    @Override
    public void finish() {
        mClientSocket.close();
    }

}
