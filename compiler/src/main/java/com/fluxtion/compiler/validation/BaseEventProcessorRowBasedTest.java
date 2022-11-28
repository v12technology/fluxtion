package com.fluxtion.compiler.validation;

import com.fluxtion.extension.csvcompiler.RowMarshaller;
import com.fluxtion.runtime.EventProcessor;

import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BaseEventProcessorRowBasedTest {

    protected boolean useSyntheticTime = false;

    protected void useSyntheticTime() {
        useSyntheticTime = true;
    }

    protected void useWallClockTime() {
        useSyntheticTime = false;
    }

    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validate(
            EventProcessor processor,
            String sinkId,
            Class<S> validationRowClass,
            String data) {
        validate(processor, sinkId, validationRowClass, null, data);
    }

    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validate(
            EventProcessor processor,
            String sinkId,
            Class<S> validationRowClass,
            BiPredicate<O, O> validator,
            String data) {
        Stream<S> streamIn = RowMarshaller.load(validationRowClass).stream(data);
        new RowValidatorDriver<>(processor, sinkId, streamIn, validator)
                .useSyntheticTime(useSyntheticTime)
                .validate();
    }

    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validate(
            EventProcessor processor,
            String sinkId,
            Stream<S> streamIn) {
        validate(processor, sinkId, streamIn, null);
    }

    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validate(
            EventProcessor processor,
            String sinkId,
            Stream<S> streamIn,
            BiPredicate<O, O> validator) {
        new RowValidatorDriver<>(processor, sinkId, streamIn, validator)
                .useSyntheticTime(useSyntheticTime)
                .validate();
    }
}
