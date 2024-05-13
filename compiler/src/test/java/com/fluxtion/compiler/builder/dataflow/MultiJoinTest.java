package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import lombok.Data;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class MultiJoinTest extends MultipleSepTargetInProcessTest {
    public MultiJoinTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void resetJoin() {
        writeSourceFile = true;
        sep(c -> {


            GroupByFlowBuilder<String, LeftData> leftBuilder = DataFlow.groupBy(LeftData::getName);
            GroupByFlowBuilder<String, MiddleData> middleBuilder = DataFlow.groupBy(MiddleData::getName);
            GroupByFlowBuilder<String, RightData> rightBuilder = DataFlow.groupBy(RightData::getName);


            MultiJoinBuilder.builder(String.class, MergedData::new)
                    .addJoin(leftBuilder, MergedData::setLeftData)
                    .addJoin(middleBuilder, MergedData::setMiddleData)
                    .addJoin(rightBuilder, MergedData::setRightData)
                    .dataFlow()
                    .mapValues(MergedData::formattedString)
                    .map(GroupBy::toMap)
                    .id("results")
                    .console("This is it : {}")
            ;
        });


//        addSink("joined", (GroupBy g) -> mutableNumber.set(g.toMap().size()));
//
        onEvent(new LeftData("greg", 47));
        onEvent(new MiddleData("greg", "male"));
        onEvent(new RightData("greg", "UK"));

        Map<String, String> resultMap = getStreamed("results");

        Assert.assertEquals(1, resultMap.size());
        Assert.assertEquals(resultMap.get("greg"), "47 male UK");

        onEvent(new LeftData("greg", 55));
        Assert.assertEquals(resultMap.get("greg"), "55 male UK");


        onEvent(new LeftData("tim", 47));
        onEvent(new MiddleData("greg", "male"));
        onEvent(new RightData("greg", "UK"));


//        Assert.assertEquals(1, mutableNumber.intValue());
//
//        onEvent(new RightData("Bill", "UK"));
//        Assert.assertEquals(1, mutableNumber.intValue());
//
//        onEvent(new LeftData("Bill", 28));
//        Assert.assertEquals(2, mutableNumber.intValue());
//        //
//
//        publishSignal("reset");
//        Assert.assertEquals(0, mutableNumber.intValue());
//        onEvent(new LeftData("greg", 47));
//        onEvent(new RightData("greg", "UK"));
    }


    @Data
    public static class MergedData {
        private LeftData leftData;
        private MiddleData middleData;
        private RightData rightData;

        public String formattedString() {
            return leftData.getAge() + " " + middleData.getSex() + " " + rightData.getCountry();
        }
    }

    @Value
    public static class LeftData {
        String name;
        int age;
    }

    @Value
    public static class MiddleData {
        String name;
        String sex;
    }


    @Value
    public static class RightData {
        String name;
        String country;
    }
}
