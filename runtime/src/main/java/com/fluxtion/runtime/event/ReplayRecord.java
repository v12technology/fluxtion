package com.fluxtion.runtime.event;

/**
 * Holds an event with a wall clock time, ready to be replayed into an {@link com.fluxtion.runtime.EventProcessor}.
 * <p>
 * See YamlReplayRunner in Fluxtion compiler for an example of replaying ReplayRecords into an {@link com.fluxtion.runtime.EventProcessor}
 * <p>
 * See YamlReplayRecordWriter in Fluxtion compiler for an example of recording ReplayRecords from an {@link com.fluxtion.runtime.EventProcessor}
 */
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
