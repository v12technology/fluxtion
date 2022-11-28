package com.fluxtion.compiler.validation;

import com.fluxtion.extension.csvcompiler.RowMarshaller;
import com.fluxtion.runtime.EventProcessor;

import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A base class users can extend to drive validation tests against an {@link EventProcessor} from a stream of
 * {@link TestRowValidationRecord}'s
 *
 * <pre>
 *
 * &#64;Test
 * public void parseAndFailCsvTest() {
 *     try {
 *         validateSinkOutput(generateSampleParser(), "doubledOutput", ParseValidationRow.class,
 *                 "intIn,answerString\n" +
 *                         "2,doubled:4\n" +
 *                         "5,This will fail\n" +
 *                         "2,doubled:4");
 *         fail("Expected exception was not thrown");
 *     } catch (Throwable e) {
 *         assertNotNull(e);
 *     }
 * }
 *
 * private static EventProcessor generateSampleParser() {
 *     return Fluxtion.interpret(c -> EventFlow.subscribe(String.class)
 *             .mapToInt(Integer::parseInt)
 *             .map(i -> i * 2)
 *             .mapToObj(i -> "doubled:" + i)
 *             .sink("doubledOutput"));
 * }
 *
 * &#64;CsvMarshaller(trim = true, acceptPartials = true)
 * public static class ParseValidationRow implements Supplier &lt;TestRowValidationRecord&lt;String&gt;&gt; {
 *     private String intIn;
 *     private String answerString;
 *
 *     public String getIntIn() {
 *         return intIn;
 *     }
 *
 *     public void setIntIn(String intIn) {
 *         this.intIn = intIn;
 *     }
 *
 *     public String getAnswerString() {
 *         return answerString;
 *     }
 *
 *     public void setAnswerString(String answerString) {
 *         this.answerString = answerString;
 *     }
 *
 *     public TestRowValidationRecord get() {
 *         return new TestRowValidationRecord(-1, intIn, answerString);
 *     }
 * }
 * </pre>
 */
public class BaseEventProcessorRowBasedTest {

    protected boolean useSyntheticTime = false;

    protected void useSyntheticTime() {
        useSyntheticTime = true;
    }

    protected void useWallClockTime() {
        useSyntheticTime = false;
    }

    /**
     * Validates using a {@link com.fluxtion.extension.csvcompiler.annotations.CsvMarshaller} annotated java bean as
     * the source of {@link TestRowValidationRecord} stream. The validation is an equality test
     *
     * @param processor          The {@link EventProcessor} under test
     * @param sinkId             The id of sink in the test EventProcessor that results are published to
     * @param validationRowClass The class that is annotated with a {@link com.fluxtion.extension.csvcompiler.annotations.CsvMarshaller} annotation
     * @param data               String data driving the test
     * @param <O>                Expected result data type
     * @param <S>                supplier of TestRowValidationRecord
     */
    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validateSinkOutput(
            EventProcessor processor,
            String sinkId,
            Class<S> validationRowClass,
            String data) {
        validateSinkOutput(processor, sinkId, validationRowClass, null, data);
    }

    /**
     * Validates using a {@link com.fluxtion.extension.csvcompiler.annotations.CsvMarshaller} annotated java bean as
     * the source of {@link TestRowValidationRecord} stream. The validation is a user supplied {@link BiPredicate}
     *
     * @param processor          The {@link EventProcessor} under test
     * @param sinkId             The id of sink in the test EventProcessor that results are published to
     * @param validationRowClass The class that is annotated with a {@link com.fluxtion.extension.csvcompiler.annotations.CsvMarshaller} annotation
     * @param validator          validation predicate
     * @param data               String data driving the test
     * @param <O>                Expected result data type
     * @param <S>                supplier of TestRowValidationRecord
     */
    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validateSinkOutput(
            EventProcessor processor,
            String sinkId,
            Class<S> validationRowClass,
            BiPredicate<O, O> validator,
            String data) {
        Stream<S> streamIn = RowMarshaller.load(validationRowClass).stream(data);
        new SinkValidatorDriver<>(processor, sinkId, streamIn, validator)
                .useSyntheticTime(useSyntheticTime)
                .validate();
    }

    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validateSinkOutput(
            EventProcessor processor,
            String sinkId,
            Stream<S> streamIn) {
        validateSinkOutput(processor, sinkId, streamIn, null);
    }

    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validateSinkOutput(
            EventProcessor processor,
            String sinkId,
            Stream<S> streamIn,
            BiPredicate<O, O> validator) {
        new SinkValidatorDriver<>(processor, sinkId, streamIn, validator)
                .useSyntheticTime(useSyntheticTime)
                .validate();
    }

    /**
     * Validates an {@link EventProcessor} against a {@link Stream<TestRowValidationRecord>} source using a user supplied
     * BiPredicate.
     *
     * @param processor The {@link EventProcessor} under test
     * @param streamIn  The stream of {@link TestRowValidationRecord} records to validate with
     * @param validator The user supplied validation test
     * @param <O>       Expected result data type
     * @param <S>       supplier of TestRowValidationRecord
     */
    protected <O, S extends Supplier<TestRowValidationRecord<O>>> void validateEventProcessor(
            EventProcessor processor,
            Stream<S> streamIn,
            BiPredicate<EventProcessor, O> validator) {
        new EventProcessorValidatorDriver<>(processor, streamIn, validator)
                .useSyntheticTime(useSyntheticTime)
                .validate();
    }
}
