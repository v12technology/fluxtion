package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.NamedNode;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class MergeMapStreamTest extends MultipleSepTargetInProcessTest {
    public MergeMapStreamTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void mergeTestBasic() {
        sep(c -> EventFlow.mergeMap(
                        MergeMapStreamBuilder.of(MyData::new)
                                .required(subscribe(String.class), MyData::setValue)
                )
                .push(new ResultsHolder()::setMyData));
        ResultsHolder resultsHolder = getField(ResultsHolder.NAME);
        onEvent("hello world");
        Assert.assertEquals("hello world", resultsHolder.getMyData().getValue());
    }

    @Test
    public void mergeTwoRequiredPropertiesTest() {
        sep(c -> EventFlow.mergeMap(
                        MergeMapStreamBuilder.of(MyData::new)
                                .required(subscribe(String.class), MyData::setValue)
                                .required(subscribe(Integer.class), MyData::setIntValue)
                )
                .push(new ResultsHolder()::setMyData));
        onEvent("hello world");
        ResultsHolder resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertNull("no push expected", resultsHolder.getMyData());

        onEvent(1);
        resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertEquals("hello world", resultsHolder.getMyData().getValue());
    }

    @Test
    public void mergeTwoRequiredOneNonTriggeringPropertiesTest() {
        sep(c -> EventFlow.mergeMap(
                        MergeMapStreamBuilder.of(MyData::new)
                                .required(subscribe(String.class), MyData::setValue)
                                .requiredNoTrigger(subscribe(Integer.class), MyData::setIntValue)
                )
                .push(new ResultsHolder()::setMyData));
        onEvent("hello world");
        ResultsHolder resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertNull("no push expected", resultsHolder.getMyData());

        onEvent(1);
        resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertNull("no push expected", resultsHolder.getMyData());

        onEvent("hello world");
        resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertEquals("hello world", resultsHolder.getMyData().getValue());
    }

    @Data
    public static class MyData {
        private String value;
        private int intValue;
    }

    @Data
    public static class ResultsHolder implements NamedNode {

        public static final String NAME = "resultsHolderNode";

        MyData myData;

        @Override
        public String getName() {
            return NAME;
        }
    }
}
