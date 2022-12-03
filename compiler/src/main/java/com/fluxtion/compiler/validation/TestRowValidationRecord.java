package com.fluxtion.compiler.validation;

/**
 * Drives a row for testing, contains input object event, expected output and time of event
 *
 * @param timeMillis sets the test, if < 0 then used current time
 * @param inputEvent The input event to the system
 * @param expected   output from the event processor to match, if null no match is carried out
 */
public class TestRowValidationRecord<EXPECTED> {
    private final long timeMillis;
    private final Object inputEvent;
    private final EXPECTED expected;

    public TestRowValidationRecord(long timeMillis, Object inputEvent, EXPECTED expected) {
        this.timeMillis = timeMillis;
        this.inputEvent = inputEvent;
        this.expected = expected;
    }

    public long timeMillis() {
        return timeMillis;
    }

    public Object inputEvent() {
        return inputEvent;
    }

    public EXPECTED expected() {
        return expected;
    }
}
