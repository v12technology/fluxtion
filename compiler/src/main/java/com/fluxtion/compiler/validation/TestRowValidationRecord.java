package com.fluxtion.compiler.validation;

/**
 * Drives a row for testing, contains input object event, expected output and time of event
 * <pre>
 *
 * timeMillis    sets the test, if < 0 then used current time
 * inputEvent    The input event to the system
 * expected      output from the event processor to match, if null no match is carried out
 * nullExpected  expect a null to published
 * </pre>
 */
public class TestRowValidationRecord<EXPECTED> {
    private final long timeMillis;
    private final Object inputEvent;
    private final EXPECTED expected;
    private final boolean nullExpected;

    public TestRowValidationRecord(long timeMillis, Object inputEvent, EXPECTED expected, boolean nullExpected) {
        this.timeMillis = timeMillis;
        this.inputEvent = inputEvent;
        this.expected = expected;
        this.nullExpected = nullExpected;
    }

    public TestRowValidationRecord(long timeMillis, Object inputEvent, EXPECTED expected) {
        this(timeMillis, inputEvent, expected, false);
    }

    public TestRowValidationRecord(Object inputEvent, EXPECTED expected) {
        this(-1, inputEvent, expected);
    }

    public static <E> TestRowValidationRecord<E> nullExpected(Object inputEvent) {
        return new TestRowValidationRecord<>(-1, inputEvent, null, true);
    }

    public static <E> TestRowValidationRecord<E> nullExpected(long timeMillis, Object inputEvent) {
        return new TestRowValidationRecord<>(timeMillis, inputEvent, null, true);
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

    public boolean expectNull() {
        return nullExpected;
    }
}
