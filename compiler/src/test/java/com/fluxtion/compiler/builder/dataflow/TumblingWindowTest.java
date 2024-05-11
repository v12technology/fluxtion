package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import org.junit.Assert;
import org.junit.Test;

public class TumblingWindowTest extends MultipleSepTargetInProcessTest {

    public TumblingWindowTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void tumbleTest() {
        sep(c -> DataFlow
                .subscribe(Integer.class)
                .tumblingAggregate(Aggregates.intSumFactory(), 100)
                .id("sum"));

        startTime(0);
        onEvent(10);
        onEvent(20);

        tick(90);
        Assert.assertNull(getStreamed("sum", Integer.class));

        //window expires and publish sum
        tick(120);
        Assert.assertEquals(30, getStreamed("sum", Integer.class).intValue());

        //window expires no data
        tick(200);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());

        //window expires and publish sum
        onEvent(90);
        tick(310);
        Assert.assertEquals(90, getStreamed("sum", Integer.class).intValue());

        //window expires no data
        onEvent(500);
        tick(2000);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());
        System.out.println();
    }

    @Test
    public void tumbleTestNoTick() {
        sep(c -> DataFlow
                .subscribe(Integer.class)
                .tumblingAggregate(Aggregates.intSumFactory(), 100)
                .id("sum"));

        startTime(0);

        setTime(130);
        onEvent(50);
        setTime(150);
        onEvent(60);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());

        setTime(220);
        onEvent(90);
        Assert.assertEquals(110, getStreamed("sum", Integer.class).intValue());

        tick(350);
        Assert.assertEquals(90, getStreamed("sum", Integer.class).intValue());

        tick(550);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());

        setTime(560);
        onEvent(50);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());

        setTime(560);
        onEvent(150);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());

        setTime(660);
        onEvent(250);
        Assert.assertEquals(200, getStreamed("sum", Integer.class).intValue());

        setTime(820);
        onEvent(15);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());

        tick(925);
        Assert.assertEquals(15, getStreamed("sum", Integer.class).intValue());

        tick(1000);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());
    }
}
