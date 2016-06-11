package com.bourdi_bay.WindowsRemote.Input;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bourdi_bay.WindowsRemote.Communication.Bluetooth.BluetoothCommunicator;
import com.bourdi_bay.WindowsRemote.Communication.Bluetooth.BluetoothPreferences;
import com.bourdi_bay.WindowsRemote.Communication.CommunicationType;
import com.bourdi_bay.WindowsRemote.Communication.Communicator;
import com.bourdi_bay.WindowsRemote.Communication.CommunicatorException;
import com.bourdi_bay.WindowsRemote.Communication.Network.TCPCommunicator;
import com.bourdi_bay.WindowsRemote.Communication.Network.TCPPreferences;
import com.bourdi_bay.WindowsRemote.LoggerTags;

public class InputHandlerSelector {

    private InputHandler mCurrentHandler = null;
    private AsyncTask<Void, Void, Boolean> mPrepareTask = null;

    public Handler createNewHandler(final @NonNull Context context, final @NonNull Looper looper, final @Nullable InputContract.CommunicationStateListener communicationStateListener,
                                    final CommunicationType communicationType, @Nullable Object specificParameters) {

        cleanupPreviousRunningHandler(communicationStateListener);

        switch (communicationType) {
            case BLUETOOTH: {
                BluetoothPreferences prefs = (BluetoothPreferences) specificParameters;
                if (prefs != null) {
                    final Communicator bluetoothCommunicator = new BluetoothCommunicator(prefs.mBluetoothDevice);
                    mCurrentHandler = new InputHandler(looper, bluetoothCommunicator);
                } else {
                    if (communicationStateListener != null) {
                        communicationStateListener.onConnectionError("Cannot initialize handler for Bluetooth, preferences are invalid");
                    }
                    Log.w(LoggerTags.COMMUNICATION_TAG, "Bluetooth specific parameters are null");
                }
                break;
            }
            case TCP: {
                TCPPreferences prefs = (TCPPreferences) specificParameters;
                if (prefs != null) {
                    final Communicator tcpCommunicator = new TCPCommunicator(prefs.mServerIP, prefs.mServerPort);
                    mCurrentHandler = new InputHandler(looper, tcpCommunicator);
                } else {
                    if (communicationStateListener != null) {
                        communicationStateListener.onConnectionError("Cannot initialize handler for TCP, preferences are invalid");
                    }
                    Log.w(LoggerTags.COMMUNICATION_TAG, "TCP specific parameters are null");
                }
                break;
            }

            /*
            case UDP: {
                DatagramPreferences prefs = (DatagramPreferences) specificParameters;
                if (prefs != null) {
                    mCurrentHandler = new DatagramPacketsSenderRunner(mAppContext, prefs.mLocalPort, prefs.mServerPort);
                } else {
                    Log.w(LoggerTags.COMMUNICATION_TAG, "Datagram specific parameters are null");
                }
                break;
            }
            */
            case NOTHING:
                break;
        }

        if (mCurrentHandler != null) {
            mPrepareTask = new AsyncTask<Void, Void, Boolean>() {

                private String mErrorMsg = null;

                @Override
                protected Boolean doInBackground(Void... params) {
                    Thread.currentThread().setName("CommunicatorAsyncTask");
                    try {
                        mCurrentHandler.prepare(context);
                        mErrorMsg = null;
                    } catch (CommunicatorException e) {
                        e.printStackTrace();
                        mErrorMsg = e.getMessage();
                        return false;
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean isSuccess) {
                    if (isSuccess) {
                        if (communicationStateListener != null) {
                            communicationStateListener.onConnectionEnabled();
                        }
                    } else {
                        if (mErrorMsg != null && communicationStateListener != null) {
                            communicationStateListener.onConnectionError(mErrorMsg);
                        }
                    }
                }
            };
            mPrepareTask.execute();
        }

        return mCurrentHandler;
    }

    private void cleanupPreviousRunningHandler(InputContract.CommunicationStateListener communicationStateListener) {
        if (mCurrentHandler != null) {
            mCurrentHandler.finish();
            mCurrentHandler.removeCallbacksAndMessages(null);
            mCurrentHandler = null;
            if (communicationStateListener != null) {
                communicationStateListener.onConnectionClosed();
            }
        }
        if (mPrepareTask != null) {
            mPrepareTask.cancel(true);
            mPrepareTask = null;
        }
    }

}

