package com.bourdi_bay.WindowsRemote.Input;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.bourdi_bay.WindowsRemote.Communication.Bluetooth.BluetoothPreferences;
import com.bourdi_bay.WindowsRemote.Communication.CommunicationType;
import com.bourdi_bay.WindowsRemote.Communication.Network.TCPPreferences;
import com.bourdi_bay.WindowsRemote.Communication.Network.UDPPreferences;

public interface InputContract {

    interface DesktopNavigationListener {
        void onMoveDown(MotionEvent motionEvent);

        void onMoveMove(MotionEvent motionEvent);

        void onRightClickDown();

        void onRightClickUp();

        void onLeftClickDown();

        void onLeftClickUp();

        void onPreferencesChanged(CommunicationType type, Object preferences);

        void onDestroy();
    }

    interface PreferencesBuilder {
        BluetoothPreferences buildBluetoothPreferences(Context context, SharedPreferences sharedPreferences);

        TCPPreferences buildTCPPreferences(Context context, SharedPreferences sharedPreferences);

        UDPPreferences buildUDPPreferences(Context context, SharedPreferences sharedPreferences);
    }

    interface CommunicationStateListener {
        void onConnectionEnabled();

        void onConnectionClosed();

        void onConnectionError(String msgError);
    }

    interface View {
        Context getContext();

        void displayError(String msg);

        void displayInformation(String msg);

        void setStateConnected();

        void setStateOffline();

        void setStateError();
    }
}
