package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleMaxFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleSumFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrimitiveDoubleTriggeringTest extends MultipleSepTargetInProcessTest {

    public PrimitiveDoubleTriggeringTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    //MAPPING TESTS
    @Test
    public void resetMapTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .map(Mappers.cumSumDouble())
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(70, result.doubleValue(), 0.0001);

        publishSignal("reset");
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishDoubleSignal("in", 90d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(140, result.doubleValue(), 0.0001);
    }

    @Test
    public void additionalPublishMapTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .map(Mappers.cumSumDouble())
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(70, result.doubleValue(), 0.0001);

        result.setValue(0);
        publishSignal("publish");
        Assert.assertEquals(70, result.doubleValue(), 0.0001);
    }

    @Test
    public void overridePublishMapTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .map(Mappers.cumSumDouble())
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishSignal("publish");
        Assert.assertEquals(70, result.doubleValue(), 0.0001);
    }

    @Test
    public void updateMapOnTriggerTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .map(Mappers.cumSumDouble())
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishSignal("update");
        publishSignal("update");
        publishSignal("update");
        Assert.assertEquals(150, result.doubleValue(), 0.0001);
    }

    //AGGREGATE TESTS
    @Test
    public void resetAggregateTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .aggregate(Aggregates.doubleSumFactory())
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(70, result.doubleValue(), 0.0001);

        publishSignal("reset");
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishDoubleSignal("in", 90d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(140, result.doubleValue(), 0.0001);
    }

    @Test
    public void additionalPublishAggregateTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .aggregate(Aggregates.doubleSumFactory())
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(70, result.doubleValue(), 0.0001);

        result.setValue(0);
        publishSignal("publish");
        Assert.assertEquals(70, result.doubleValue(), 0.0001);
    }

    @Test
    public void overridePublishAggregateTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .aggregate(Aggregates.doubleSumFactory())
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishSignal("publish");
        Assert.assertEquals(70, result.doubleValue(), 0.0001);
    }

    @Test
    public void updateAggregateOnTriggerTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .aggregate(Aggregates.doubleSumFactory())
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishSignal("update");
        publishSignal("update");
        publishSignal("update");
        Assert.assertEquals(150, result.doubleValue(), 0.0001);
    }

    //TUMBLING
    @Test
    public void resetTumblingMapTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .tumblingAggregate(DoubleSumFlowFunction::new, 100).id("sum")
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        setTime(0);
        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 20d);
        tickDelta(100);
        Assert.assertEquals(60, result.doubleValue(), 0.0001);

        publishDoubleSignal("in", 20d);
        publishSignal("reset");
        tickDelta(100);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishDoubleSignal("in", 40d);
        tickDelta(100);
        Assert.assertEquals(40, result.doubleValue(), 0.0001);
    }

    @Test
    public void additionalPublishTumblingMapTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .tumblingAggregate(DoubleSumFlowFunction::new, 100).id("sum")
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        setTime(0);
        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 20d);
        tickDelta(100);
        Assert.assertEquals(60, result.doubleValue(), 0.0001);

        result.setValue(0);
        publishDoubleSignal("in", 20d);
        tickDelta(20);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);
        publishSignal("publish");
        Assert.assertEquals(60, result.doubleValue(), 0.0001);

        tickDelta(120);
        Assert.assertEquals(20, result.doubleValue(), 0.0001);
    }

    @Test
    public void overridePublishTumblingMapTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .tumblingAggregate(DoubleSumFlowFunction::new, 100).id("sum")
                .publishTriggerOverride(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        setTime(0);
        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 20d);
        tickDelta(100);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishDoubleSignal("in", 20d);
        tickDelta(20);
        publishSignal("publish");
        Assert.assertEquals(60, result.doubleValue(), 0.0001);
    }

    @Test
    public void updateTriggerTumblingMapTest() {
        sep(c -> DataFlow.subscribeToDoubleSignal("in")
                .tumblingAggregate(DoubleSumFlowFunction::new, 100).id("sum")
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        setTime(0);
        publishDoubleSignal("in", 20d);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        tickDelta(30);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishSignal("update");
        Assert.assertEquals(20, result.doubleValue(), 0.0001);

        tickDelta(30);
        publishDoubleSignal("in", 20d);
        publishDoubleSignal("in", 50d);
        Assert.assertEquals(20, result.doubleValue(), 0.0001);

        publishSignal("update");
        Assert.assertEquals(90, result.doubleValue(), 0.0001);

        publishDoubleSignal("in", 50d);
        result.setValue(0);
        tickDelta(100);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishDoubleSignal("in", 50d);
        publishSignal("update");
        Assert.assertEquals(50, result.doubleValue(), 0.0001);
    }

    //SLIDING
    @Test
    public void slidingWindowNonDeductTest() {
        sep(c -> subscribe(String.class)
                .mapToDouble(Mappers::parseDouble)
                .slidingAggregate(DoubleMaxFlowFunction::new, 100, 4).id("max")
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
        );
        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);

        assertThat(getStreamed("max"), is(Double.NaN));

        onEvent("90");
        tickDelta(100);
        assertThat(getStreamed("max"), is(Double.NaN));

        onEvent("30");
        tickDelta(100);
        assertThat(getStreamed("max"), is(Double.NaN));

        tickDelta(100);
        assertThat(getStreamed("max"), is(100d));

        tickDelta(100);
        assertThat(getStreamed("max"), is(90d));

        tickDelta(100);
        assertThat(getStreamed("max"), is(30d));

        tickDelta(100);
        assertThat(getStreamed("max"), is(Double.NaN));

        onEvent("70");
        onEvent("50");
        assertThat(getStreamed("max"), is(Double.NaN));

        tickDelta(100);
        assertThat(getStreamed("max"), is(70d));

        publishSignal("reset");
        tickDelta(100);
        assertThat(getStreamed("max"), is(Double.NaN));
    }

    @Test
    public void additionalPublishSlidingWindowTest() {
        sep(c -> subscribe(String.class)
                .mapToDouble(Mappers::parseDouble)
                .slidingAggregate(DoubleMaxFlowFunction::new, 100, 4).id("max")
                .publishTrigger(DataFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);


        tickDelta(300);
        Assert.assertEquals(100, result.doubleValue(), 0.0001);

        result.setValue(0);
        onEvent(150d);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishSignal("publish");
        Assert.assertEquals(100, result.doubleValue(), 0.0001);
    }

    @Test
    public void overridePublishSlidingWindowTest() {
        sep(c -> subscribe(String.class)
                .mapToDouble(Mappers::parseDouble)
                .slidingAggregate(DoubleMaxFlowFunction::new, 100, 4).id("max")
                .updateTrigger(DataFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableDouble result = new MutableDouble();
        addDoubleSink("out", result::setValue);

        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        tickDelta(300);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        onEvent(150d);
        Assert.assertEquals(0, result.doubleValue(), 0.0001);

        publishSignal("update");
        Assert.assertEquals(100, result.doubleValue(), 0.0001);
    }
}
