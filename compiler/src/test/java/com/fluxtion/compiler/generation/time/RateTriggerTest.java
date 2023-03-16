package com.fluxtion.compiler.generation.time;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.helpers.Mappers;
import com.fluxtion.runtime.time.FixedRateTrigger;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class RateTriggerTest extends MultipleSepTargetInProcessTest {
    public RateTriggerTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void rateTest() {
        sep(c -> {
            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .map(Mappers.cumSumInt()).id("sum")
                    .resetTrigger(new FixedRateTrigger(100))
                    .publishTriggerOverride(new FixedRateTrigger(5))
                    .sink("result");
        });
        MutableInt result = new MutableInt();
        addIntSink("result", result::setValue);
        setTime(0);
        onEvent(new MutableInt(100));
        Assert.assertEquals(0, result.intValue());

        tickDelta(70);
        Assert.assertEquals(100, result.intValue());

        result.setValue(0);
        tickDelta(20);
        Assert.assertEquals(100, result.intValue());

        onEvent(new MutableInt(300));
        Assert.assertEquals(100, result.intValue());

        tickDelta(6);
        Assert.assertEquals(400, result.intValue());

        tickDelta(6);
        Assert.assertEquals(0, result.intValue());

        onEvent(new MutableInt(20));
        Assert.assertEquals(0, result.intValue());

        onEvent(new MutableInt(20));
        tickDelta(20);
        Assert.assertEquals(40, result.intValue());
    }
}
