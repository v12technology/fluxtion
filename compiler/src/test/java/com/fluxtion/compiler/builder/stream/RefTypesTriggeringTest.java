package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntMax;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RefTypesTriggeringTest extends MultipleSepTargetInProcessTest {
    public RefTypesTriggeringTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void resetTumblingMapTest() {
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .tumblingAggregate(AggregateIntSum::new, 100).id("sum")
                .resetTrigger(EventFlow.subscribeToSignal("reset"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> {
            result.setValue(i);
        });

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
                .tumblingAggregate(AggregateIntSum::new, 100).id("sum")
                .publishTrigger(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> {
            result.setValue(i);
        });

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
                .tumblingAggregate(AggregateIntSum::new, 100).id("sum")
                .publishTriggerOverride(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> {
            result.setValue(i);
        });

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
        writeSourceFile = true;
        generateMetaInformation = true;
        sep(c -> subscribe(String.class)
                .map(Mappers::parseInt)
                .tumblingAggregate(AggregateIntSum::new, 100).id("sum")
                .updateTrigger(EventFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> {
            result.setValue(i);
        });

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
                .slidingAggregate(AggregateIntMax::new, 100, 4).id("max")
                .resetTrigger(EventFlow.subscribeToSignal("reset"))
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
                .slidingAggregate(AggregateIntMax::new, 100, 4).id("max")
                .publishTrigger(EventFlow.subscribeToSignal("publish"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> {
            result.setValue(i);
        });

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
                .slidingAggregate(AggregateIntMax::new, 100, 4).id("max")
                .updateTrigger(EventFlow.subscribeToSignal("update"))
                .sink("out"));

        MutableInt result = new MutableInt();
        addSink("out", (Integer i) -> {
            result.setValue(i);
        });

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
