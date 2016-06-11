package com.bourdi_bay.WindowsRemote.Communication;

public class MessageBroadcastFactory {

    public static MessageBroadcast createMoveMessage(MyDeltaPoint deltaPoint) {
        String msgBuilder = "{ \"type\" : MOVE" +
                ", \"from_x\" : " +
                deltaPoint.fromPoint.x +
                ", \"from_y\" : " +
                deltaPoint.fromPoint.y +
                ", \"to_x\" : " +
                deltaPoint.toPoint.x +
                ", \"to_y\" : " +
                deltaPoint.toPoint.y +
                "}";
        return new MessageBroadcast(msgBuilder);
    }

    public static MessageBroadcast createLeftClickDownMessage() {
        return new MessageBroadcast("{ \"type\" : LEFT_DOWN_CLICK }");
    }

    public static MessageBroadcast createRightClickDownMessage() {
        return new MessageBroadcast("{ \"type\" : RIGHT_DOWN_CLICK }");
    }

    public static MessageBroadcast createLeftClickUpMessage() {
        return new MessageBroadcast("{ \"type\" : LEFT_UP_CLICK }");
    }

    public static MessageBroadcast createRightClickUpMessage() {
        return new MessageBroadcast("{ \"type\" : RIGHT_UP_CLICK }");
    }

}
