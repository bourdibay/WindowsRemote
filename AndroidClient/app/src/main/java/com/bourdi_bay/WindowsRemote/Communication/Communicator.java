package com.bourdi_bay.WindowsRemote.Communication;

import android.content.Context;

public interface Communicator {
    public void prepare(Context context) throws CommunicatorException;

    public void sendMessage(MessageBroadcast message);

    public void finish();
}
