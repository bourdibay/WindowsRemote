package com.bourdi_bay.WindowsRemote.Communication;

public class MyDeltaPoint {
    public final MyPoint fromPoint;
    public final MyPoint toPoint;

    public MyDeltaPoint(MyPoint fromPoint, MyPoint toPoint) {
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
    }
}
