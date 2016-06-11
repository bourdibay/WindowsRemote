package com.bourdi_bay.WindowsRemote.Input;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bourdi_bay.WindowsRemote.Communication.Communicator;
import com.bourdi_bay.WindowsRemote.Communication.CommunicatorException;
import com.bourdi_bay.WindowsRemote.Communication.MessageBroadcast;
import com.bourdi_bay.WindowsRemote.LoggerTags;

import java.util.concurrent.atomic.AtomicBoolean;

public class InputHandler extends Handler {

    private final Communicator mCommunicator;
    private AtomicBoolean mIsPrepared = new AtomicBoolean(false);

    public InputHandler(Looper looper, @NonNull Communicator communicator) {
        super(looper);
        mCommunicator = communicator;
    }

    @Override
    public void handleMessage(Message msg) {
        if (!mIsPrepared.get()) {
            return;
        }
        MessageBroadcast m = (MessageBroadcast) msg.obj;
        Log.d(LoggerTags.INPUT_TAG, "Message handled:" + m.toString());
        mCommunicator.sendMessage(m);
    }

    public void prepare(Context context) throws CommunicatorException {
        try {
            mCommunicator.prepare(context);
            mIsPrepared.set(true);
        } catch (CommunicatorException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void finish() {
        mCommunicator.finish();
    }
}



