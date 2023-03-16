package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongMax;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongSum;
import com.fluxtion.runtime.stream.helpers.Aggregates;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.apache.commons.lang3.mutable.MutableLong;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrimitiveLongTriggeringTest extends MultipleSepTargetInProcessTest {

    public PrimitiveLongTriggeringTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    //MAPPING TESTS
    @Test
    public void resetMapTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .map(Mappers.cumSumLong())
                .resetTrigger(EventFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(70, result.longValue());

        publishSignal("reset");
        Assert.assertEquals(0, result.longValue());

        publishLongSignal("in", 90L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(140, result.longValue());
    }

    @Test
    public void additionalPublishMapTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .map(Mappers.cumSumLong())
                .publishTrigger(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(70, result.longValue());

        result.setValue(0);
        publishSignal("publish");
        Assert.assertEquals(70, result.longValue());
    }

    @Test
    public void overridePublishMapTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .map(Mappers.cumSumLong())
                .publishTriggerOverride(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(0, result.longValue());

        publishSignal("publish");
        Assert.assertEquals(70, result.longValue());
    }

    @Test
    public void updateMapOnTriggerTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .map(Mappers.cumSumLong())
                .updateTrigger(EventFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(0, result.longValue());

        publishSignal("update");
        publishSignal("update");
        publishSignal("update");
        Assert.assertEquals(150, result.longValue());
    }

    //AGGREGATE TESTS
    @Test
    public void resetAggregateTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .aggregate(Aggregates.longSumFactory())
                .resetTrigger(EventFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(70, result.longValue());

        publishSignal("reset");
        Assert.assertEquals(0, result.longValue());

        publishLongSignal("in", 90L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(140, result.longValue());
    }

    @Test
    public void additionalPublishAggregateTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .aggregate(AggregateLongSum::new)
                .publishTrigger(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(70, result.longValue());

        result.setValue(0);
        publishSignal("publish");
        Assert.assertEquals(70, result.longValue());
    }

    @Test
    public void overridePublishAggregateTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .aggregate(Aggregates.longSumFactory())
                .publishTriggerOverride(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(0, result.longValue());

        publishSignal("publish");
        Assert.assertEquals(70, result.longValue());
    }

    @Test
    public void updateAggregateOnTriggerTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .aggregate(Aggregates.longSumFactory())
                .updateTrigger(EventFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(0, result.longValue());

        publishSignal("update");
        publishSignal("update");
        publishSignal("update");
        Assert.assertEquals(150, result.longValue());
    }

    //TUMBLING
    @Test
    public void resetTumblingMapTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .tumblingAggregate(AggregateLongSum::new, 100).id("sum")
                .resetTrigger(EventFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        setTime(0);
        publishLongSignal("in", 20L);
        publishLongSignal("in", 20L);
        publishLongSignal("in", 20L);
        tickDelta(100);
        Assert.assertEquals(60, result.longValue());

        publishLongSignal("in", 20L);
        publishSignal("reset");
        tickDelta(100);
        Assert.assertEquals(0, result.longValue());

        publishLongSignal("in", 40L);
        tickDelta(100);
        Assert.assertEquals(40, result.longValue());
    }

    @Test
    public void additionalPublishTumblingMapTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .tumblingAggregate(AggregateLongSum::new, 100).id("sum")
                .publishTrigger(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        setTime(0);
        publishLongSignal("in", 20L);
        publishLongSignal("in", 20L);
        publishLongSignal("in", 20L);
        tickDelta(100);
        Assert.assertEquals(60, result.longValue());

        result.setValue(0);
        publishLongSignal("in", 20L);
        tickDelta(20);
        Assert.assertEquals(0, result.longValue());
        publishSignal("publish");
        Assert.assertEquals(60, result.longValue());

        tickDelta(120);
        Assert.assertEquals(20, result.longValue());
    }

    @Test
    public void overridePublishTumblingMapTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .tumblingAggregate(AggregateLongSum::new, 100).id("sum")
                .publishTriggerOverride(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        setTime(0);
        publishLongSignal("in", 20L);
        publishLongSignal("in", 20L);
        publishLongSignal("in", 20L);
        tickDelta(100);
        Assert.assertEquals(0, result.longValue());

        publishLongSignal("in", 20L);
        tickDelta(20);
        publishSignal("publish");
        Assert.assertEquals(60, result.longValue());
    }

    @Test
    public void updateTriggerTumblingMapTest() {
        sep(c -> EventFlow.subscribeToLongSignal("in")
                .tumblingAggregate(AggregateLongSum::new, 100).id("sum")
                .updateTrigger(EventFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        setTime(0);
        publishLongSignal("in", 20L);
        Assert.assertEquals(0, result.longValue());

        tickDelta(30);
        Assert.assertEquals(0, result.longValue());

        publishSignal("update");
        Assert.assertEquals(20, result.longValue());

        tickDelta(30);
        publishLongSignal("in", 20L);
        publishLongSignal("in", 50L);
        Assert.assertEquals(20, result.longValue());

        publishSignal("update");
        Assert.assertEquals(90, result.longValue());

        publishLongSignal("in", 50L);
        result.setValue(0);
        tickDelta(100);
        Assert.assertEquals(0, result.longValue());

        publishLongSignal("in", 50L);
        publishSignal("update");
        Assert.assertEquals(50, result.longValue());
    }

    //SLIDING
    @Test
    public void slidingWindowNonDeductTest() {
        sep(c -> subscribe(String.class)
                .mapToLong(Mappers::parseLong)
                .slidingAggregate(AggregateLongMax::new, 100, 4).id("max")
                .resetTrigger(EventFlow.subscribeToSignal("reset"))
        );
        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);

        assertThat(getStreamed("max"), is(0L));

        onEvent("90");
        tickDelta(100);
        assertThat(getStreamed("max"), is(0L));

        onEvent("30");
        tickDelta(100);
        assertThat(getStreamed("max"), is(0L));

        tickDelta(100);
        assertThat(getStreamed("max"), is(100L));

        tickDelta(100);
        assertThat(getStreamed("max"), is(90L));

        tickDelta(100);
        assertThat(getStreamed("max"), is(30L));

        tickDelta(100);
        assertThat(getStreamed("max"), is(0L));

        onEvent("70");
        onEvent("50");
        assertThat(getStreamed("max"), is(0L));

        tickDelta(100);
        assertThat(getStreamed("max"), is(70L));

        publishSignal("reset");
        tickDelta(100);
        assertThat(getStreamed("max"), is(0L));
    }

    @Test
    public void additionalPublishSlidingWindowTest() {
        sep(c -> subscribe(String.class)
                .mapToLong(Mappers::parseLong)
                .slidingAggregate(AggregateLongMax::new, 100, 4).id("max")
                .publishTrigger(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);
        Assert.assertEquals(0, result.longValue());


        tickDelta(300);
        Assert.assertEquals(100, result.longValue());

        result.setValue(0);
        onEvent(150);
        Assert.assertEquals(0, result.longValue());

        publishSignal("publish");
        Assert.assertEquals(100, result.longValue());
    }

    @Test
    public void overridePublishSlidingWindowTest() {
        sep(c -> subscribe(String.class)
                .mapToLong(Mappers::parseLong)
                .slidingAggregate(AggregateLongMax::new, 100, 4).id("max")
                .updateTrigger(EventFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableLong result = new MutableLong();
        addLongSink("out", result::setValue);

        addClock();
        onEvent("70");
        onEvent("50");
        onEvent("100");
        tickDelta(100);
        Assert.assertEquals(0, result.longValue());

        tickDelta(300);
        Assert.assertEquals(0, result.longValue());

        onEvent(150);
        Assert.assertEquals(0, result.longValue());

        publishSignal("update");
        Assert.assertEquals(100, result.longValue());
    }
}
