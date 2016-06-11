package com.bourdi_bay.WindowsRemote.Communication.Network;

import android.content.Context;
import android.content.SharedPreferences;

import com.bourdi_bay.WindowsRemote.R;

public class UDPPreferences {
    public static String mLastError;
    public int mLocalPort;
    public int mServerPort;

    public UDPPreferences(int localPort, int serverPort) {
        mLocalPort = localPort;
        mServerPort = serverPort;
    }

    public static UDPPreferences createUDPPreferences(Context context, SharedPreferences sharedPreferences) {
        mLastError = "";

        final boolean useUDP = sharedPreferences.getBoolean(context.getString(R.string.pref_key_use_udp), false);
        if (!useUDP) {
            mLastError = context.getString(R.string.udp_preferences_udp_not_activated);
            return null;
        }

        final String strLocalPort = sharedPreferences.getString(context.getString(R.string.pref_key_local_udp_port), "0");
        final String strServerPort = sharedPreferences.getString(context.getString(R.string.pref_key_server_udp_port), "0");
        final int localPort = Integer.parseInt(strLocalPort);
        final int serverPort = Integer.parseInt(strServerPort);

        final UDPPreferences preferences = new UDPPreferences(localPort, serverPort);
        return preferences;
    }

}
