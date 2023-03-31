package com.fluxtion.compiler.validation;

import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.stream.MutableNumber;
import com.fluxtion.runtime.time.ClockStrategy;

import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Validates an {@link EventProcessor} with a supplied {@link BiPredicate}. Events are dispatched into the processor
 * from a {@link Stream<TestRowValidationRecord>} source.
 *
 * @param <EXPECTED> The output type of the sink
 */
public class EventProcessorValidatorDriver<EXPECTED> {
    private final EventProcessor eventProcessor;
    private final Stream<? extends Supplier<TestRowValidationRecord<EXPECTED>>> validationStream;
    private final BiPredicate<EventProcessor, EXPECTED> sinkValidator;
    private boolean useSyntheticTime;
    private boolean stopOnFirstFailure = true;
    private EXPECTED actualOutput;
    private final MutableNumber syntheticTime = new MutableNumber();
    private final LongAdder rowCount = new LongAdder();

    public EventProcessorValidatorDriver(
            EventProcessor eventProcessor,
            Stream<? extends Supplier<TestRowValidationRecord<EXPECTED>>> validationStream,
            BiPredicate<EventProcessor, EXPECTED> sinkValidator) {
        this(eventProcessor, validationStream, sinkValidator, false);
    }

    public EventProcessorValidatorDriver(
            EventProcessor eventProcessor,
            Stream<? extends Supplier<TestRowValidationRecord<EXPECTED>>> validationStream,
            BiPredicate<EventProcessor, EXPECTED> sinkValidator,
            boolean useSyntheticTime) {
        this.eventProcessor = eventProcessor;
        this.validationStream = validationStream;
        this.sinkValidator = sinkValidator;
        this.useSyntheticTime = useSyntheticTime;
        syntheticTime.setLongValue(0);
        rowCount.reset();
    }

    public boolean useSyntheticTime() {
        return useSyntheticTime;
    }

    public EventProcessorValidatorDriver<EXPECTED> useSyntheticTime(boolean useSyntheticTime) {
        this.useSyntheticTime = useSyntheticTime;
        return this;
    }

    public boolean stopOnFirstFailure() {
        return stopOnFirstFailure;
    }

    public EventProcessorValidatorDriver<EXPECTED> stopOnFirstFailure(boolean stopOnFirstFailure) {
        this.stopOnFirstFailure = stopOnFirstFailure;
        return this;
    }

    public void validate() {
        eventProcessor.init();
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
        if (expectedOutput != null && sinkValidator.test(eventProcessor, expectedOutput)) {
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
