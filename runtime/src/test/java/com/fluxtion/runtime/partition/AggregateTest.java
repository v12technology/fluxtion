package com.fluxtion.runtime.partition;

import com.fluxtion.runtime.dataflow.aggregate.function.BucketedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import org.junit.Assert;
import org.junit.Test;

public class AggregateTest {

    @Test
    public void windowValueTest() {
        BucketedSlidingWindow<Integer, Integer, IntSumFlowFunction> windowSum =
                new BucketedSlidingWindow<>(IntSumFlowFunction::new, 4);

        windowSum.aggregate(10);
        windowSum.aggregate(10);
        windowSum.roll();
        Assert.assertEquals(20, windowSum.get().intValue());

        windowSum.aggregate(50);
        windowSum.aggregate(50);
        windowSum.aggregate(50);
        windowSum.aggregate(10);
        windowSum.roll();
        Assert.assertEquals(180, windowSum.get().intValue());

        windowSum.aggregate(50);
        windowSum.roll();
        Assert.assertEquals(230, windowSum.get().intValue());

        windowSum.roll();
        Assert.assertEquals(230, windowSum.get().intValue());

        windowSum.roll();
        Assert.assertEquals(210, windowSum.get().intValue());

        windowSum.roll();
        Assert.assertEquals(50, windowSum.get().intValue());

        windowSum.roll();
        Assert.assertEquals(0, windowSum.get().intValue());
    }
}
