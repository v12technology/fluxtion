package com.fluxtion.compiler.validation;

import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.stream.aggregate.MutableNumber;
import com.fluxtion.runtime.time.ClockStrategy;

import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RowValidatorDriver<IN, EXPECTED> {
    private final EventProcessor eventProcessor;
    private final String sinkName;
    private final Stream<? extends Supplier<TestRowValidationRecord<EXPECTED>>> validationStream;
    private final BiPredicate<EXPECTED, EXPECTED> validator;
    private boolean useSyntheticTime;
    private boolean stopOnFirstFailure = true;
    private EXPECTED actualOutput;
    private MutableNumber syntheticTime = new MutableNumber();
    private LongAdder rowCount = new LongAdder();

    public RowValidatorDriver(
            EventProcessor eventProcessor,
            String sinkName,
            Stream<? extends Supplier<TestRowValidationRecord<EXPECTED>>> validationStream,
            BiPredicate<EXPECTED, EXPECTED> validator) {
        this(eventProcessor, sinkName, validationStream, validator, false);
    }

    public RowValidatorDriver(
            EventProcessor eventProcessor,
            String sinkName,
            Stream<? extends Supplier<TestRowValidationRecord<EXPECTED>>> validationStream,
            BiPredicate<EXPECTED, EXPECTED> validator,
            boolean useSyntheticTime) {
        this.eventProcessor = eventProcessor;
        this.sinkName = sinkName;
        this.validationStream = validationStream;
        this.validator = validator;
        this.useSyntheticTime = useSyntheticTime;
        syntheticTime.setLongValue(0);
        rowCount.reset();
    }

    public boolean useSyntheticTime() {
        return useSyntheticTime;
    }

    public RowValidatorDriver<IN, EXPECTED> useSyntheticTime(boolean useSyntheticTime) {
        this.useSyntheticTime = useSyntheticTime;
        return this;
    }

    public boolean stopOnFirstFailure() {
        return stopOnFirstFailure;
    }

    public RowValidatorDriver<IN, EXPECTED> stopOnFirstFailure(boolean stopOnFirstFailure) {
        this.stopOnFirstFailure = stopOnFirstFailure;
        return this;
    }

    public void validate() {
        eventProcessor.init();
        eventProcessor.addSink(sinkName, (EXPECTED e) -> actualOutput = e);
        if (useSyntheticTime) {
            eventProcessor.onEvent(ClockStrategy.registerClockEvent(syntheticTime::longValue));
        }
        validationStream.map(Supplier::get).forEach(this::validateRow);
    }

    private void validateRow(TestRowValidationRecord<EXPECTED> row) {
        if (useSyntheticTime) {
            syntheticTime.setLongValue(row.timeMillis());
        }
        eventProcessor.onEvent(row.inputEvent());
        EXPECTED expectedOutput = row.expected();
        if (expectedOutput != null && validator == null) {
            if (!objectsAreEqual(actualOutput, expectedOutput)) {
                throw new AssertionError("validation error on row:" + rowCount.longValue()
                        + " objects not equal ["
                        + expectedOutput
                        + " ==> " + actualOutput
                        + "]"
                );
            }
        } else if (expectedOutput != null && validator.test(expectedOutput, actualOutput)) {
            throw new AssertionError("validation error on row:" + rowCount.longValue()
                    + " objects failed vaildation["
                    + expectedOutput
                    + "  ==> " + actualOutput
                    + "]"
            );
        }
        rowCount.increment();
        actualOutput = null;
    }

    static boolean objectsAreEqual(Object obj1, Object obj2) {
        if (obj1 == null) {
            return (obj2 == null);
        }
        return obj1.equals(obj2);
    }
}
