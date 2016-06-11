package com.bourdi_bay.WindowsRemote.Communication.Network;


import android.content.Context;
import android.content.SharedPreferences;

import com.bourdi_bay.WindowsRemote.R;

/**
 * Created by bourdi_bay on 31/05/2016.
 */
public class TCPPreferences {
    public static String mLastError = "";

    public String mServerIP;
    public int mServerPort;

    public TCPPreferences(String serverIP, int serverPort) {
        mServerIP = serverIP;
        mServerPort = serverPort;
    }

    public static TCPPreferences createTCPPreferences(Context context, SharedPreferences sharedPreferences) {
        mLastError = "";

        final boolean useTCP = sharedPreferences.getBoolean(context.getString(R.string.pref_key_use_tcp), false);
        if (!useTCP) {
            mLastError = context.getString(R.string.tcp_preferences_tcp_not_activated);
            return null;
        }

        final String ipAddress = sharedPreferences.getString(context.getString(R.string.pref_key_tcp_ip), null);
        // TODO: for validation => https://stackoverflow.com/questions/3698034/validating-ip-in-android/11545229#11545229
        final String strPort = sharedPreferences.getString(context.getString(R.string.pref_key_tcp_port), "0");
        // TODO: validation int
        final int port = Integer.parseInt(strPort);

        final TCPPreferences preferences = new TCPPreferences(ipAddress, port);

        return preferences;
    }
}
