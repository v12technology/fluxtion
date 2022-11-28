package com.fluxtion.compiler.validation;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.builder.stream.EventFlow;
import com.fluxtion.extension.csvcompiler.annotations.CsvMarshaller;
import com.fluxtion.runtime.EventProcessor;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * An example demonstrating use of {@link RowValidatorDriver}
 */
public class ValidationTest extends BaseEventProcessorRowBasedTest {

    @Test
    public void parseAndFailCsvTest() {
        try {
            validate(generateSampleParser(), "doubledOutput", ParseValidationRow.class,
                    "intIn,answerString\n" +
                            "2,doubled:4\n" +
                            "5,This will fail\n" +
                            "2,doubled:4");
            fail("Expected exception was not thrown");
        } catch (Throwable e) {
            assertNotNull(e);
        }
    }

    private static EventProcessor generateSampleParser() {
        return Fluxtion.interpret(c -> EventFlow.subscribe(String.class)
                .mapToInt(Integer::parseInt)
                .map(i -> i * 2)
                .mapToObj(i -> "doubled:" + i)
                .sink("doubledOutput"));
    }

    @Test
    public void parseAndSucceedYamlTest() {
        List<ParseValidationRow> rows = new ArrayList<>();
        Yaml yaml = new Yaml();
        String yamlData = "!!com.fluxtion.compiler.validation.ValidationTest$ParseValidationRow {answerString: 'doubled:4', intIn: '2'}\n" +
                "---\n" +
                "!!com.fluxtion.compiler.validation.ValidationTest$ParseValidationRow {answerString: 'doubled:10', intIn: '5'}\n" +
                "---\n" +
                "!!com.fluxtion.compiler.validation.ValidationTest$ParseValidationRow {answerString: 'doubled:4', intIn: '2'}";
        yaml.loadAll(yamlData).forEach(i -> rows.add((ParseValidationRow) i));
        validate(generateSampleParser(), "doubledOutput", rows.stream());
    }

    @Test
    public void parseAndSucceedCsvTest() {
        validate(generateSampleParser(), "doubledOutput", ParseValidationRow.class,
                "intIn,answerString\n" +
                        "2,doubled:4\n" +
                        "5,doubled:10\n" +
                        "2,doubled:4");
    }

    @CsvMarshaller(trim = true)
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
