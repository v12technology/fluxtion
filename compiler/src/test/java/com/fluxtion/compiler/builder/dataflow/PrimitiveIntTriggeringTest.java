package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntMaxFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrimitiveIntTriggeringTest extends MultipleSepTargetInProcessTest {

    public PrimitiveIntTriggeringTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    //MAPPING TESTS
    @Test
    public void resetMapTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .map(Mappers.cumSumInt())
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(70, result.intValue());

        publishSignal("reset");
        Assert.assertEquals(0, result.intValue());

        publishIntSignal("in", 90);
        publishIntSignal("in", 50);
        Assert.assertEquals(140, result.intValue());
    }

    @Test
    public void additionalPublishMapTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .map(Mappers.cumSumInt())
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(70, result.intValue());

        result.setValue(0);
        publishSignal("publish");
        Assert.assertEquals(70, result.intValue());
    }

    @Test
    public void overridePublishMapTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .map(Mappers.cumSumInt())
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(0, result.intValue());

        publishSignal("publish");
        Assert.assertEquals(70, result.intValue());
    }

    @Test
    public void updateMapOnTriggerTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .map(Mappers.cumSumInt())
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(0, result.intValue());

        publishSignal("update");
        publishSignal("update");
        publishSignal("update");
        Assert.assertEquals(150, result.intValue());
    }

    //AGGREGATE TESTS
    @Test
    public void resetAggregateTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .aggregate(Aggregates.intSumFactory())
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(70, result.intValue());

        publishSignal("reset");
        Assert.assertEquals(0, result.intValue());

        publishIntSignal("in", 90);
        publishIntSignal("in", 50);
        Assert.assertEquals(140, result.intValue());
    }

    @Test
    public void additionalPublishAggregateTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .aggregate(Aggregates.intSumFactory())
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(70, result.intValue());

        result.setValue(0);
        publishSignal("publish");
        Assert.assertEquals(70, result.intValue());
    }

    @Test
    public void overridePublishAggregateTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .aggregate(Aggregates.intSumFactory())
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(0, result.intValue());

        publishSignal("publish");
        Assert.assertEquals(70, result.intValue());
    }

    @Test
    public void updateAggregateOnTriggerTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .aggregate(Aggregates.intSumFactory())
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(0, result.intValue());

        publishSignal("update");
        publishSignal("update");
        publishSignal("update");
        Assert.assertEquals(150, result.intValue());
    }

    //TUMBLING
    @Test
    public void resetTumblingMapTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        setTime(0);
        publishIntSignal("in", 20);
        publishIntSignal("in", 20);
        publishIntSignal("in", 20);
        tickDelta(100);
        Assert.assertEquals(60, result.intValue());

        publishIntSignal("in", 20);
        publishSignal("reset");
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());

        publishIntSignal("in", 40);
        tickDelta(100);
        Assert.assertEquals(40, result.intValue());
    }

    @Test
    public void additionalPublishTumblingMapTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        setTime(0);
        publishIntSignal("in", 20);
        publishIntSignal("in", 20);
        publishIntSignal("in", 20);
        tickDelta(100);
        Assert.assertEquals(60, result.intValue());

        result.setValue(0);
        publishIntSignal("in", 20);
        tickDelta(20);
        Assert.assertEquals(0, result.intValue());
        publishSignal("publish");
        Assert.assertEquals(60, result.intValue());

        tickDelta(120);
        Assert.assertEquals(20, result.intValue());
    }

    @Test
    public void overridePublishTumblingMapTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        setTime(0);
        publishIntSignal("in", 20);
        publishIntSignal("in", 20);
        publishIntSignal("in", 20);
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());

        publishIntSignal("in", 20);
        tickDelta(20);
        publishSignal("publish");
        Assert.assertEquals(60, result.intValue());
    }

    @Test
    public void updateTriggerTumblingMapTest() {
        sep(c -> DataFlow.subscribeToIntSignal("in")
                .tumblingAggregate(IntSumFlowFunction::new, 100).id("sum")
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

        setTime(0);
        publishIntSignal("in", 20);
        Assert.assertEquals(0, result.intValue());

        tickDelta(30);
        Assert.assertEquals(0, result.intValue());

        publishSignal("update");
        Assert.assertEquals(20, result.intValue());

        tickDelta(30);
        publishIntSignal("in", 20);
        publishIntSignal("in", 50);
        Assert.assertEquals(20, result.intValue());

        publishSignal("update");
        Assert.assertEquals(90, result.intValue());

        publishIntSignal("in", 50);
        result.setValue(0);
        tickDelta(100);
        Assert.assertEquals(0, result.intValue());

        publishIntSignal("in", 50);
        publishSignal("update");
        Assert.assertEquals(50, result.intValue());
    }

    //SLIDING
    @Test
    public void slidingWindowNonDeductTest() {
        sep(c -> subscribe(String.class)
                .mapToInt(Mappers::parseInt)
                .slidingAggregate(IntMaxFlowFunction::new, 100, 4).id("max")
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
        );
        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);

        assertThat(getStreamed("max"), is(0));

        onEvent("90");
        tickDelta(100);
        assertThat(getStreamed("max"), is(0));

        onEvent("30");
        tickDelta(100);
        assertThat(getStreamed("max"), is(0));

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
                .mapToInt(Mappers::parseInt)
                .slidingAggregate(IntMaxFlowFunction::new, 100, 4).id("max")
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

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
                .mapToInt(Mappers::parseInt)
                .slidingAggregate(IntMaxFlowFunction::new, 100, 4).id("max")
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addIntSink("out", result::setValue);

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
}
