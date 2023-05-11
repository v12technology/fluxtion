package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.time.MutableNumber;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.helpers.Predicates;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class JoinTest extends MultipleSepTargetInProcessTest {
    public JoinTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void resetJoin() {
        MutableNumber mutableNumber = new MutableNumber();
        sep(c -> JoinFlowBuilder.innerJoin(
                        DataFlow.groupBy(LeftData::getName),
                        DataFlow.groupBy(RightData::getName))
                .resetTrigger(DataFlow.subscribeToSignal("reset"))
                .map(GroupBy::toMap)
                .filter(Predicates.hasMapChanged())
                .sink("joined"));
        addSink("joined", (Map<String, Tuple<LeftData, RightData>> g) -> mutableNumber.set(g.size()));

        onEvent(new LeftData("greg", 47));
        Assert.assertEquals(0, mutableNumber.intValue());

        mutableNumber.set(0);
        onEvent(new RightData("greg", "UK"));
        Assert.assertEquals(1, mutableNumber.intValue());

        mutableNumber.set(0);
        onEvent(new RightData("Bill", "UK"));
        Assert.assertEquals(0, mutableNumber.intValue());

        mutableNumber.set(0);
        onEvent(new LeftData("Bill", 28));
        Assert.assertEquals(2, mutableNumber.intValue());

        mutableNumber.set(0);
        publishSignal("reset");
        Assert.assertEquals(0, mutableNumber.intValue());
        onEvent(new LeftData("greg", 47));
        Assert.assertEquals(0, mutableNumber.intValue());
        onEvent(new RightData("greg", "UK"));
        Assert.assertEquals(2, mutableNumber.intValue());
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
