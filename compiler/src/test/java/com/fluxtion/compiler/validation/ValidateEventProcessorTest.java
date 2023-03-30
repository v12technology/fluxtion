package com.fluxtion.compiler.validation;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.builder.stream.EventFlow;
import com.fluxtion.extension.csvcompiler.RowMarshaller;
import com.fluxtion.extension.csvcompiler.annotations.CsvMarshaller;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.stream.helpers.Mappers;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.util.function.Supplier;


/**
 * An example demonstrating use of {@link SinkValidatorDriver}
 */
public class ValidateEventProcessorTest extends BaseEventProcessorRowBasedTest {

    @Test
    public void parseAndSucceedCsvTest() {
        val stream = RowMarshaller.load(ParseValidationRow.class).stream("intIn,answerString\n" +
                "2,doubled:4\n" +
                "5,doubled:10\n" +
                "2,doubled:4");

        validateEventProcessor(
                generateSampleParser(),
                stream,
                ValidateEventProcessorTest::validateMyDoublingEventProcessor);
    }

    @Test
    public void parseAndSucceedCsvSampleOfExpectedTest() {
        val stream = RowMarshaller.load(ParseValidationRow.class).stream("intIn,answerString\n" +
                "20\n" +
                "5\n" +
                "25,cumSum:50");

        validateEventProcessor(
                generateSampleParser(),
                stream,
                ValidateEventProcessorTest::validateMyDoublingEventProcessor);
    }

    @SneakyThrows
    private static boolean validateMyDoublingEventProcessor(EventProcessor processor, String expexcted) {
        return processor.getNodeById("results").toString().equalsIgnoreCase(expexcted);
    }

    @SneakyThrows
    private static boolean validateMyCumSumEventProcessor(EventProcessor processor, String expexcted) {
        return processor.getNodeById("results").toString().equalsIgnoreCase(expexcted);
    }

    private static EventProcessor generateSampleParser() {
        return Fluxtion.interpret(c -> EventFlow.subscribe(String.class)
                .mapToInt(Integer::parseInt)
                .map(i -> i * 2)
                .mapToObj(i -> "doubled:" + i).id("results"));
    }

    private static EventProcessor generateSampleSumParser() {
        return Fluxtion.interpret(c -> EventFlow.subscribe(String.class)
                .mapToInt(Integer::parseInt)
                .map(Mappers.cumSumInt())
                .mapToObj(i -> "cumSum:" + i).id("results"));
    }

    @CsvMarshaller(trim = true, acceptPartials = true)
    public static class ParseValidationRow implements Supplier<TestRowValidationRecord<String>> {
        private String intIn;
        private String answerString;

        public String getIntIn() {
            return intIn;
        }

        public void setIntIn(String intIn) {
            this.intIn = intIn;
        }

        public String getAnswerString() {
            return answerString;
        }

        public void setAnswerString(String answerString) {
            this.answerString = answerString;
        }

        @Override
        public TestRowValidationRecord get() {
            return new TestRowValidationRecord(-1, intIn, answerString);
        }
    }

}
