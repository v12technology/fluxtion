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
        sep(c -> MultiJoinBuilder
                .builder(String.class, MergedData::new)
                .addJoin(DataFlow.groupBy(LeftData::getName), MergedData::setLeftData)
                .addJoin(DataFlow.groupBy(MiddleData::getName), MergedData::setMiddleData)
                .addJoin(DataFlow.groupBy(RightData::getName), MergedData::setRightData)
                .dataFlow()
                .mapValues(MergedData::formattedString)
                .map(GroupBy::toMap)
                .id("results"));

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
