package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.time.MutableNumber;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

public class JoinTest extends MultipleSepTargetInProcessTest {
    public JoinTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void resetJoin() {
        MutableNumber mutableNumber = new MutableNumber();
        sep(c -> {
            JoinFlowBuilder.innerJoin(
                            DataFlow.groupBy(LeftData::getName),
                            DataFlow.groupBy(RightData::getName)
                    )
                    .resetTrigger(DataFlow.subscribeToSignal("reset"))
                    .sink("joined");
        });
        addSink("joined", (GroupBy g) -> mutableNumber.set(g.toMap().size()));

        onEvent(new LeftData("greg", 47));
        Assert.assertEquals(0, mutableNumber.intValue());

        onEvent(new RightData("greg", "UK"));
        Assert.assertEquals(1, mutableNumber.intValue());

        onEvent(new RightData("Bill", "UK"));
        Assert.assertEquals(1, mutableNumber.intValue());

        onEvent(new LeftData("Bill", 28));
        Assert.assertEquals(2, mutableNumber.intValue());
        //

        publishSignal("reset");
        Assert.assertEquals(0, mutableNumber.intValue());
        onEvent(new LeftData("greg", 47));
        onEvent(new RightData("greg", "UK"));
    }

    @Value
    public static class LeftData {
        String name;
        int age;
    }

    @Value
    public static class RightData {
        String name;
        String country;
    }
}
