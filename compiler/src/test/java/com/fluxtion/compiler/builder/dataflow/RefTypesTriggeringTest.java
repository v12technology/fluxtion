package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.KeyedData;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntMaxFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import org.apache.commons.lang3.mutable.MutableInt;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RefTypesTriggeringTest extends MultipleSepTargetInProcessTest {
    public RefTypesTriggeringTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void resetTumblingMapTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> result.setValue(i));

        setTime(0);
        onEvent("20");
        onEvent("20");
        onEvent("20");
        tickDelta(100);
        Assert.assertEquals(60, result.intValue());

        onEvent("20");
        publishSignal("reset");
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());

        onEvent("40");
        tickDelta(100);
        Assert.assertEquals(40, result.intValue());
    }

    @Test
    public void additionalPublishTumblingMapTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> result.setValue(i));

        setTime(0);
        onEvent("20");
        onEvent("20");
        onEvent("20");
        tickDelta(100);
        Assert.assertEquals(60, result.intValue());

        result.setValue(0);
        onEvent("20");
        tickDelta(20);
        Assert.assertEquals(0, result.intValue());
        publishSignal("publish");
        Assert.assertEquals(60, result.intValue());

        tickDelta(120);
        Assert.assertEquals(20, result.intValue());
    }

    @Test
    public void overridePublishTumblingMapTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> result.setValue(i));

        setTime(0);
        onEvent("20");
        onEvent("20");
        onEvent("20");
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());

        onEvent("20");
        tickDelta(20);
        publishSignal("publish");
        Assert.assertEquals(60, result.intValue());
    }

    @Test
    public void updateTriggerumblingMapTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> result.setValue(i));

        setTime(0);
        onEvent("20");
        Assert.assertEquals(0, result.intValue());

        tickDelta(30);
        Assert.assertEquals(0, result.intValue());

        publishSignal("update");
        Assert.assertEquals(20, result.intValue());

        tickDelta(30);
        onEvent("20");
        onEvent("50");
        Assert.assertEquals(20, result.intValue());

        publishSignal("update");
        Assert.assertEquals(90, result.intValue());

        onEvent("50");
        result.setValue(0);
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());

        onEvent("50");
        publishSignal("update");
        Assert.assertEquals(50, result.intValue());
    }

    //SLIDING
    @Test
    public void slidingWindowNonDeductTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .slidingAggregate(IntMaxFlowFunction::new, 100, 4).id("max")
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
        );
        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);

        assertThat(getStreamed("max"), is(nullValue()));

        onEvent("90");
        tickDelta(100);
        assertThat(getStreamed("max"), is(nullValue()));

        onEvent("30");
        tickDelta(100);
        assertThat(getStreamed("max"), is(nullValue()));

        tickDelta(100);
        assertThat(getStreamed("max"), is(100));

        tickDelta(100);
        assertThat(getStreamed("max"), is(90));

        tickDelta(100);
        assertThat(getStreamed("max"), is(30));

        tickDelta(100);
        assertThat(getStreamed("max"), is(0));

        onEvent("70");
        onEvent("50");
        assertThat(getStreamed("max"), is(0));

        tickDelta(100);
        assertThat(getStreamed("max"), is(70));

        publishSignal("reset");
        tickDelta(100);
        assertThat(getStreamed("max"), is(0));
    }

    @Test
    public void additionalPublishSlidingWindowTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .slidingAggregate(IntMaxFlowFunction::new, 100, 4).id("max")
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> result.setValue(i));

        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());


        tickDelta(300);
        Assert.assertEquals(100, result.intValue());

        result.setValue(0);
        onEvent(150);
        Assert.assertEquals(0, result.intValue());

        publishSignal("publish");
        Assert.assertEquals(100, result.intValue());
    }

    @Test
    public void overridePublishSlidingWindowTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .slidingAggregate(IntMaxFlowFunction::new, 100, 4).id("max")
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> result.setValue(i));

        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());

        tickDelta(300);
        Assert.assertEquals(0, result.intValue());

        onEvent(150);
        Assert.assertEquals(0, result.intValue());

        publishSignal("update");
        Assert.assertEquals(100, result.intValue());
    }

    //GROUPBY TUMBLING
    @Test
    public void resetGroupByTumblingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupByTumbling(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100)
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .map(GroupBy::toMap)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(75);//100
        expected.put("A", 40);
        expected.put("B", 100);
        assertThat(results, is(expected));

        publishSignal("reset");
        expected.clear();
        assertThat(results, is(expected));
    }

    @Test
    public void publishGroupByTumblingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupByTumbling(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100)
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .map(GroupBy::toMap)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(75);//100
        expected.put("A", 40);
        expected.put("B", 100);
        assertThat(results, is(expected));

        expected.clear();
        assertThat(expected.values(), is(Matchers.empty()));

        publishSignal("publish");
        expected.put("A", 40);
        expected.put("B", 100);
        assertThat(results, is(expected));

        tickDelta(750);//100
        expected.clear();
        assertThat(results, is(expected));
    }

    @Test
    public void publishOverrideGroupByTumblingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupByTumbling(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100)
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .map(GroupBy::toMap)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(125);//100
        assertThat(results, is(expected));

        publishSignal("publish");
        expected.put("A", 40);
        expected.put("B", 100);
        assertThat(results, is(expected));
    }

    @Test
    public void updateTriggerGroupByTumblingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupByTumbling(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100)
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .map(GroupBy::toMap)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        publishSignal("update");
        expected.put("A", 40);
        expected.put("B", 100);
        assertThat(results, is(expected));

        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));
        tickDelta(500);
        onEvent(new KeyedData("A", 4));
        onEvent(new KeyedData("B", 10));

        assertThat(results, is(expected));

        publishSignal("update");
        expected.put("A", 4);
        expected.put("B", 10);
        assertThat(results, is(expected));
    }

    //GROUPBY SLIDING
    @Test
    public void resetTriggerGroupBySlidingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupBySliding(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100, 10)
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .map(GroupBy::toMap)
                .sink("map")
        );

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new KeyedData("A", 4000));

        tick(100);
        onEvent(new KeyedData("A", 40));

        tick(300);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tick(1000);
        expected.put("A", 4080);
        expected.put("B", 100);
        assertThat(results, is(expected));

        publishSignal("reset");
        expected.clear();
        assertThat(results, is(expected));
    }

    @Test
    public void publishTriggerGroupBySlidingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupBySliding(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100, 10)
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .map(GroupBy::toMap)
                .sink("map")
        );

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new KeyedData("A", 4000));

        tick(100);
        onEvent(new KeyedData("A", 40));

        tick(300);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tick(1000);
        expected.put("A", 4080);
        expected.put("B", 100);
        assertThat(results, is(expected));

        tick(1050);
        assertThat(results, is(expected));

        publishSignal("publish");
        expected.put("A", 4080);
        expected.put("B", 100);
        assertThat(results, is(expected));
    }

    @Test
    public void publishOverrideTriggerGroupBySlidingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupBySliding(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100, 10)
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .map(GroupBy::toMap)
                .sink("map")
        );

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new KeyedData("A", 4000));

        tick(100);
        onEvent(new KeyedData("A", 40));

        tick(300);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tick(1000);
        assertThat(results, is(expected));

        tick(1050);
        assertThat(results, is(expected));

        publishSignal("publish");
        expected.put("A", 4080);
        expected.put("B", 100);
        assertThat(results, is(expected));
    }

    @Test
    public void updateTriggerGroupBySlidingTest() {
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupBySliding(KeyedData::getId, KeyedData::getAmount, IntSumFlowFunction::new, 100, 10)
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .map(GroupBy::toMap)
                .sink("map")
        );

        addSink("map", (Map<String, Integer> in) -> {
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new KeyedData("A", 4000));

        tick(100);
        onEvent(new KeyedData("A", 40));

        tick(300);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tick(400);
        assertThat(results, is(expected));

//        tick(450);
//        onEvent(new KeyedData("B", 100));
//        assertThat(results, is(expected));
//
        publishSignal("update");
        expected.put("A", 4080);
        expected.put("B", 100);
        assertThat(results, is(expected));
    }
}
