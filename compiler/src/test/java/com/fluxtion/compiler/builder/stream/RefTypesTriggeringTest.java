package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class RefTypesTriggeringTest extends MultipleSepTargetInProcessTest {
    public RefTypesTriggeringTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void resetTumblingMapTest() {
        sep(c -> subscribe(String.class)
                .map(StreamBuildTest::valueOfInt)
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
                .map(StreamBuildTest::valueOfInt)
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
                .map(StreamBuildTest::valueOfInt)
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
                .map(StreamBuildTest::valueOfInt)
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
}
