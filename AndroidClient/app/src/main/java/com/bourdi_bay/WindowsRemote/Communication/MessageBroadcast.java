package com.bourdi_bay.WindowsRemote.Communication;

public class MessageBroadcast {
    private String _message;

    MessageBroadcast(String message) {
        _message = message;
    }

    public String toString() {
        return _message;
    }
}
