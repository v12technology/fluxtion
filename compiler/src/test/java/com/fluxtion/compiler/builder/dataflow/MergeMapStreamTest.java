/*
 * Copyright (c) 2025 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static com.fluxtion.compiler.builder.dataflow.MergeAndMapFlowBuilder.*;

public class MergeMapStreamTest extends MultipleSepTargetInProcessTest {
    public MergeMapStreamTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void mergeTestBasic() {
        sep(c -> DataFlow.mergeMap(
                        of(MyData::new)
                                .required(subscribe(String.class), MyData::setValue)
                )
                .push(new ResultsHolder()::setMyData));
        ResultsHolder resultsHolder = getField(ResultsHolder.NAME);
        onEvent("hello world");
        Assert.assertEquals("hello world", resultsHolder.getMyData().getValue());
    }

    @Test
    public void mergeTestBasicWithBuilder() {
        sep(c ->
                MergeAndMapFlowBuilder.of(MyData::new)
                        .required(subscribe(String.class), MyData::setValue)
                        .dataFlow()
                        .push(new ResultsHolder()::setMyData));
        ResultsHolder resultsHolder = getField(ResultsHolder.NAME);
        onEvent("hello world");
        Assert.assertEquals("hello world", resultsHolder.getMyData().getValue());
    }

    @Test
    @SuppressWarnings("all")
    public void mergeTestBasicWithHelper() {
        sep(c -> {
            MergeAndMapFlowBuilder
                    .merge(MyData::new, requiredMergeInput(subscribe(String.class), MyData::setValue))
                    .push(new ResultsHolder()::setMyData);
        });
        ResultsHolder resultsHolder = getField(ResultsHolder.NAME);
        onEvent("hello world");
        Assert.assertEquals("hello world", resultsHolder.getMyData().getValue());
    }

    @Test
    public void mergeTwoRequiredPropertiesTest() {
        sep(c -> DataFlow.mergeMap(
                        MergeAndMapFlowBuilder.of(MyData::new)
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
    @SuppressWarnings("all")
    public void mergeTwoRequired_Helper_PropertiesTest() {
        sep(c -> {

            MergeAndMapFlowBuilder.merge(MyData::new,
                            requiredMergeInput(subscribe(String.class), MyData::setValue),
                            requiredMergeInput(subscribe(Integer.class), MyData::setIntValue))
                    .push(new ResultsHolder()::setMyData);

        });
        onEvent("hello world");
        ResultsHolder resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertNull("no push expected", resultsHolder.getMyData());

        onEvent(1);
        resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertEquals("hello world", resultsHolder.getMyData().getValue());
    }

    @Test
    public void mergeTwoRequiredOneNonTriggeringPropertiesTest() {
        sep(c -> DataFlow.mergeMap(
                        MergeAndMapFlowBuilder.of(MyData::new)
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

    @Test
    @SuppressWarnings("all")
    public void mergeRequiredAndOptional_Helper_PropertiesTest() {
        sep(c -> {
            DataFlow.mergeMap(MyData::new,
                            requiredMergeInput(subscribe(String.class), MyData::setValue),
                            optionalMergeInput(subscribe(Integer.class), MyData::setIntValue))
                    .push(new ResultsHolder()::setMyData);

        });

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

    @Test
    @SuppressWarnings("all")
    public void mergeToKnownInstance_PropertiesTest() {
        sep(c -> {
            MyData myData = c.addNode(new MyData(), "myData");
            DataFlow.mergeMapToNode(myData,
                            requiredMergeInput(subscribe(String.class), MyData::setValue),
                            optionalMergeInput(subscribe(Integer.class), MyData::setIntValue))
                    .push(new ResultsHolder()::setMyData);

        });

        onEvent("hello world");
        MyData myData = getField("myData");
        Assert.assertNull("no push expected", myData.getValue());

        onEvent(1);
//        resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertNull("no push expected", myData.getValue());

        onEvent("hello world");
//        resultsHolder = getField(ResultsHolder.NAME);
        Assert.assertEquals("hello world", myData.getValue());
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
