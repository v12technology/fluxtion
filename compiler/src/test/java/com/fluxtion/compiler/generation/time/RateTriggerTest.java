package com.fluxtion.compiler.generation.time;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.helpers.Mappers;
import com.fluxtion.runtime.time.FixedRateTrigger;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class RateTriggerTest extends MultipleSepTargetInProcessTest {
    public RateTriggerTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void rateTest(){
        sep(c ->{
//            FixedRateTrigger fixedRateTrigger = c.addNode(new FixedRateTrigger(null, 100));

            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .map(Mappers.cumSumInt()).id("sum")
                    .resetTrigger(new FixedRateTrigger(100))
                    .publishTrigger(new FixedRateTrigger(5))
//                    .peek(Peekers.console("sum:{}"))
            ;
        });
        setTime(0);
        onEvent(new MutableInt(100));
        tickDelta(70);
        tickDelta(20);
        onEvent(new MutableInt(300));
        tickDelta(20);
        onEvent(new MutableInt(45));
        tickDelta(70);
        tickDelta(20);
        onEvent(new MutableInt(20));
    }
}
