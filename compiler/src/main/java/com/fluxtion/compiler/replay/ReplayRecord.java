package com.fluxtion.compiler.replay;

public class ReplayRecord {
    private long wallClockTime;
    private Object event;

    public long getWallClockTime() {
        return wallClockTime;
    }

    public void setWallClockTime(long wallClockTime) {
        this.wallClockTime = wallClockTime;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }
}
