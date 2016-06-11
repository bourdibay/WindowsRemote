package com.bourdi_bay.WindowsRemote.Input;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.bourdi_bay.WindowsRemote.Communication.CommunicationType;
import com.bourdi_bay.WindowsRemote.Communication.MessageBroadcast;
import com.bourdi_bay.WindowsRemote.Communication.MessageBroadcastFactory;
import com.bourdi_bay.WindowsRemote.Communication.MyDeltaPoint;
import com.bourdi_bay.WindowsRemote.Communication.MyPoint;
import com.bourdi_bay.WindowsRemote.LoggerTags;
import com.bourdi_bay.WindowsRemote.R;

class CommunicationListenerImpl implements InputContract.CommunicationStateListener {

    private final InputContract.View mView;

    public CommunicationListenerImpl(InputContract.View view) {
        mView = view;
    }

    @Override
    public void onConnectionEnabled() {
        mView.setStateConnected();
        mView.displayInformation(mView.getContext().getString(R.string.info_connection_established));
    }

    @Override
    public void onConnectionClosed() {
        mView.setStateOffline();
        mView.displayInformation(mView.getContext().getString(R.string.info_connection_closed));
    }

    @Override
    public void onConnectionError(String msgError) {
        mView.setStateError();
        mView.displayError(msgError);
    }
}

public class InputPresenter implements InputContract.DesktopNavigationListener {

    public final HandlerThread mHandlerThread;
    private final InputContract.View mView;
    private final InputContract.CommunicationStateListener mCommunicationListener;
    public Handler mHandler;
    private InputHandlerSelector mInputHandlerSelector = new InputHandlerSelector();
    private MyPoint mFromPoint = null;

    public InputPresenter(InputContract.View view) {
        mView = view;
        mCommunicationListener = new CommunicationListenerImpl(mView);
        mHandlerThread = new HandlerThread("InputPresenter_HandlerThread");
        mHandlerThread.start();
    }

    public void onPreferencesChanged(CommunicationType type, Object preferences) {
        while (mHandlerThread.getLooper() == null) {
            Thread.yield();
        }
        mHandler = mInputHandlerSelector.createNewHandler(mView.getContext(), mHandlerThread.getLooper(), mCommunicationListener, type, preferences);
    }

    @Override
    public void onMoveDown(MotionEvent motionEvent) {
        Log.d(LoggerTags.INPUT_TAG, "InputPresenter::onMoveDown");
        mFromPoint = new MyPoint(((int) motionEvent.getX()), (int) motionEvent.getY());
    }

    @Override
    public void onMoveMove(MotionEvent motionEvent) {
        Log.d(LoggerTags.INPUT_TAG, "InputPresenter::onMoveMove");
        MyPoint toPoint = new MyPoint((int) motionEvent.getX(), (int) motionEvent.getY());
        if (toPoint != mFromPoint) {
            MyDeltaPoint deltaPoint = new MyDeltaPoint(mFromPoint, toPoint);
            MessageBroadcast message = MessageBroadcastFactory.createMoveMessage(deltaPoint);
            pushMessage(message);
        }
        mFromPoint = new MyPoint(toPoint);
    }

    @Override
    public void onRightClickDown() {
        Log.d(LoggerTags.INPUT_TAG, "InputPresenter::onRightClickDown");
        pushMessage(MessageBroadcastFactory.createRightClickDownMessage());
    }

    @Override
    public void onRightClickUp() {
        Log.d(LoggerTags.INPUT_TAG, "InputPresenter::onRightClickUp");
        pushMessage(MessageBroadcastFactory.createRightClickUpMessage());
    }

    @Override
    public void onLeftClickDown() {
        Log.d(LoggerTags.INPUT_TAG, "InputPresenter::onLeftClickDown");
        pushMessage(MessageBroadcastFactory.createLeftClickDownMessage());
    }

    @Override
    public void onLeftClickUp() {
        Log.d(LoggerTags.INPUT_TAG, "InputPresenter::onLeftClickUp");
        pushMessage(MessageBroadcastFactory.createLeftClickUpMessage());
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mInputHandlerSelector.createNewHandler(mView.getContext(), mHandlerThread.getLooper(), mCommunicationListener, CommunicationType.NOTHING, null);
            mHandlerThread.quit();
            mHandler = null;
        }
    }

    private void pushMessage(MessageBroadcast messageBroadcast) {
        if (messageBroadcast != null && mHandler != null) {
            Message msg = mHandler.obtainMessage(1, messageBroadcast);
            mHandler.sendMessage(msg);
        }
    }
}
