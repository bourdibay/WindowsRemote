package com.bourdi_bay.WindowsRemote;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bourdi_bay.WindowsRemote.Communication.Bluetooth.BluetoothPreferences;
import com.bourdi_bay.WindowsRemote.Communication.CommunicationType;
import com.bourdi_bay.WindowsRemote.Communication.Network.TCPPreferences;
import com.bourdi_bay.WindowsRemote.Communication.Network.UDPPreferences;
import com.bourdi_bay.WindowsRemote.Input.InputContract;
import com.bourdi_bay.WindowsRemote.Input.InputPreferencesBuilder;
import com.bourdi_bay.WindowsRemote.Input.InputPresenter;

public class MainActivityFragment extends Fragment implements InputContract.View {

    public final InputContract.DesktopNavigationListener mInputPresenter;
    public final InputContract.PreferencesBuilder mPreferencesBuilder;

    public MainActivityFragment() {
        mInputPresenter = new InputPresenter(this);
        mPreferencesBuilder = new InputPreferencesBuilder(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        final View touchView = view.findViewById(R.id.touchView);
        final FloatingActionButton btLeftClick = (FloatingActionButton) view.findViewById(R.id.btLeftClick);
        final FloatingActionButton btRightClick = (FloatingActionButton) view.findViewById(R.id.btRightClick);

        assert btLeftClick != null;
        assert btRightClick != null;
        assert touchView != null;

        btRightClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mInputPresenter.onRightClickDown();
                        break;
                    case MotionEvent.ACTION_UP:
                        mInputPresenter.onRightClickUp();
                        break;
                }
                return true;
            }
        });
        btLeftClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mInputPresenter.onLeftClickDown();
                        return true;
                    case MotionEvent.ACTION_UP:
                        mInputPresenter.onLeftClickUp();
                        return true;
                }
                return false;
            }
        });

        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mInputPresenter.onMoveDown(motionEvent);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mInputPresenter.onMoveMove(motionEvent);
                        return true;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
    }

    private void updatePreferences() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final boolean useBluetooth = defaultSharedPreferences.getBoolean(getString(R.string.pref_key_use_bluetooth), false);
        final boolean useTCP = defaultSharedPreferences.getBoolean(getString(R.string.pref_key_use_tcp), false);
        final boolean useUDP = defaultSharedPreferences.getBoolean(getString(R.string.pref_key_use_udp), false);
        if (useBluetooth) {
            BluetoothPreferences bluetoothPreferences = mPreferencesBuilder.buildBluetoothPreferences(getContext(), defaultSharedPreferences);
            mInputPresenter.onPreferencesChanged(CommunicationType.BLUETOOTH, bluetoothPreferences);
            Log.d(LoggerTags.COMMUNICATION_TAG, "Interface set: Bluetooth");
        } else if (useTCP) {
            TCPPreferences tcpPreferences = mPreferencesBuilder.buildTCPPreferences(getContext(), defaultSharedPreferences);
            mInputPresenter.onPreferencesChanged(CommunicationType.TCP, tcpPreferences);
            Log.d(LoggerTags.COMMUNICATION_TAG, "Interface set: TCP");
        } else if (useUDP) {
            UDPPreferences udpPreferences = mPreferencesBuilder.buildUDPPreferences(getContext(), defaultSharedPreferences);
            mInputPresenter.onPreferencesChanged(CommunicationType.UDP, udpPreferences);
            Log.d(LoggerTags.COMMUNICATION_TAG, "Interface set: UDP");
        }
    }

    public void displayError(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInformation(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setStateConnected() {
        ConnectionStateListener activity = (ConnectionStateListener) getActivity();
        activity.onStateConnected();
    }

    @Override
    public void setStateOffline() {
        ConnectionStateListener activity = (ConnectionStateListener) getActivity();
        activity.onStateOffline();
    }

    @Override
    public void setStateError() {
        ConnectionStateListener activity = (ConnectionStateListener) getActivity();
        activity.onStateError();
    }

    @Override
    public void onDestroy() {
        mInputPresenter.onDestroy();
        super.onDestroy();
    }

    public interface ConnectionStateListener {
        void onStateConnected();

        void onStateOffline();

        void onStateError();
    }
}
