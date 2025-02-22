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
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.dataflow.helpers.Mappers.CountNode;
import com.fluxtion.runtime.dataflow.helpers.Predicates;
import com.fluxtion.runtime.dataflow.helpers.Predicates.AllUpdatedPredicate;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Objects;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PredicatesTest extends MultipleSepTargetInProcessTest {

    public PredicatesTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void hasChangedInt() {
        sep(c -> {
            DataFlow.subscribe(Integer.class)
                    .mapToInt(Integer::intValue)
                    .filter(Predicates.hasIntChanged())
                    .mapOnNotify(Mappers.newCountNode()).id("count")
//                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        CountNode countNode = getStreamed("count");

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(countNode.getCount(), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(countNode.getCount(), CoreMatchers.is(2));
    }

    @Test
    public void hasChangedIntWithMapCount() {
        sep(c -> {
            DataFlow.subscribe(Integer.class)
                    .mapToInt(Integer::intValue)
                    .filter(Predicates.hasIntChanged())
                    .map(Mappers.countInt()).id("count")
            ;

            DataFlow.subscribe(String.class)
                    .mapToInt(Mappers.count()).id("count_strings");
        });
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(getStreamed("count"), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(getStreamed("count"), CoreMatchers.is(2));

        assertThat(getStreamed("count_strings"), CoreMatchers.is(0));
        onEvent("test");
        onEvent("test");
        onEvent("test");
        assertThat(getStreamed("count_strings"), CoreMatchers.is(3));
    }

    @Test
    public void hasChangedDouble() {
        sep(c -> {
            DataFlow.subscribe(Integer.class)
                    .mapToDouble(Integer::doubleValue)
                    .filter(Predicates.hasDoubleChanged())
                    .mapOnNotify(Mappers.newCountNode()).id("count")
                    .mapToInt(CountNode::getCount)
            ;
        });
        CountNode countNode = getStreamed("count");

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(countNode.getCount(), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(countNode.getCount(), CoreMatchers.is(2));
    }


    @Test
    public void hasChangedLong() {
        sep(c -> {
            DataFlow.subscribe(Integer.class)
                    .mapToLong(Integer::longValue)
                    .filter(Predicates.hasLongChanged())
                    .mapOnNotify(Mappers.newCountNode()).id("count")
                    .mapToInt(CountNode::getCount)
            ;
        });
        CountNode countNode = getStreamed("count");

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(countNode.getCount(), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(countNode.getCount(), CoreMatchers.is(2));
    }


    @Test
    public void hasChangedObject() {
        sep(c -> {
            DataFlow.subscribe(Integer.class)
                    .map(Objects::toString)
                    .filter(Predicates.hasChangedFilter())
                    .mapToInt(Mappers.count())
                    .id("count");
        });

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(getStreamed("count"), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(getStreamed("count"), CoreMatchers.is(2));
    }

    @Test
    public void allUpdated() {
//        addAuditor();
        sep(c -> {
            LongFlowBuilder int1 = subscribe(BinaryMapTest.Data_1.class).mapToInt(BinaryMapTest.Data_1::getIntValue).box().mapToLong(Integer::longValue);
            LongFlowBuilder int2 = subscribe(BinaryMapTest.Data_2.class).mapToInt(BinaryMapTest.Data_2::getIntValue).box().mapToLong(Integer::longValue);
            int1.mapBiFunction(Mappers.DIVIDE_LONGS, int2).id("divide")
                    .updateTrigger(new AllUpdatedPredicate(StreamHelper.getSourcesAsList(int1, int2)));
        });
        onEvent(new BinaryMapTest.Data_1(100));
        assertThat(getStreamed("divide"), is(0L));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(4L));
    }

    @Test
    public void allUpdatedWithBuilder() {
        sep(c -> {
            LongFlowBuilder int1 = subscribe(BinaryMapTest.Data_1.class).mapToInt(BinaryMapTest.Data_1::getIntValue).box().mapToLong(Integer::longValue);
            LongFlowBuilder int2 = subscribe(BinaryMapTest.Data_2.class).mapToInt(BinaryMapTest.Data_2::getIntValue).box().mapToLong(Integer::longValue);
            int1.mapBiFunction(Mappers::divideLongs, int2).id("divide")
                    .updateTrigger(PredicateBuilder.allChanged(int1, int2));
        });
        onEvent(new BinaryMapTest.Data_1(100));
        assertThat(getStreamed("divide"), is(0L));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(4L));
    }

    @Test
    public void anyUpdatedWithBuilder() {
        sep(c -> {
            DataFlow.subscribe(String.class)
                    .publishTriggerOverride(
                            PredicateBuilder.anyTriggered(
                                    DataFlow.subscribeToSignal("signalA"),
                                    DataFlow.subscribeToSignal("signalB")))
                    .mapToInt(Mappers.count()).id("count_strings");
        });
        onEvent("test");
        onEvent("aa");
        publishSignal("signalA");
        publishSignal("signalB");
        publishSignal("signalC");

        assertThat(getStreamed("count_strings"), CoreMatchers.is(2));
    }

    @Test
    public void anyUpdatedWithHelper() {
        sep(c -> {
            DataFlow.subscribe(String.class)
                    .publishTriggerOverride(
                            DataFlow.subscribeToSignal("signalA"),
                            DataFlow.subscribeToSignal("signalB"))
                    .mapToInt(Mappers.count()).id("count_strings");
        });
        onEvent("test");
        onEvent("aa");
        publishSignal("signalA");
        publishSignal("signalB");
        publishSignal("signalC");

        assertThat(getStreamed("count_strings"), CoreMatchers.is(2));
    }

    @Test
    public void allUpdatedWithReset() {
        sep(c -> {
            //inputs
            IntFlowBuilder int1 = subscribe(BinaryMapTest.Data_1.class).mapToInt(BinaryMapTest.Data_1::getIntValue);
            IntFlowBuilder int2 = subscribe(BinaryMapTest.Data_2.class).mapToInt(BinaryMapTest.Data_2::getIntValue);
            int1.mapBiFunction(Mappers::divideInts, int2).id("divide")
                    .updateTrigger(
                            new AllUpdatedPredicate(
                                    StreamHelper.getSourcesAsList(int1, int2),
                                    StreamHelper.getSource(subscribe(String.class))));
        });
        onEvent(new BinaryMapTest.Data_1(100));
        assertThat(getStreamed("divide"), is(0));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(4));
        //reset the notify flag will need both inouts to update
        onEvent("reset");
        onEvent(new BinaryMapTest.Data_1(500));
        assertThat(getStreamed("divide"), is(4));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(20));
    }

    @Test
    public void allUpdatedWithResetBuilder() {
        sep(c -> {
            //inputs
            IntFlowBuilder int1 = subscribe(BinaryMapTest.Data_1.class).mapToInt(BinaryMapTest.Data_1::getIntValue);
            IntFlowBuilder int2 = subscribe(BinaryMapTest.Data_2.class).mapToInt(BinaryMapTest.Data_2::getIntValue);
            int1.mapBiFunction(Mappers::divideInts, int2).id("divide")
                    .updateTrigger(PredicateBuilder.allChangedWithReset(subscribe(String.class), int1, int2));
        });
        onEvent(new BinaryMapTest.Data_1(100));
        assertThat(getStreamed("divide"), is(0));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(4));
        //reset the update flag will need both inputs to update before starting a new calculation
        onEvent("reset");
        onEvent(new BinaryMapTest.Data_1(500));
        assertThat(getStreamed("divide"), is(4));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(20));
    }

}
