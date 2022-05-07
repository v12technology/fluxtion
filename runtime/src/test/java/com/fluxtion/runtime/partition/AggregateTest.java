package com.fluxtion.runtime.partition;

import com.fluxtion.runtime.stream.aggregate.BucketedSlidingWindowedFunction;
import com.fluxtion.runtime.stream.aggregate.SlidingWindowFunctionIntSum;
import org.junit.Assert;
import org.junit.Test;

public class AggregateTest {

    @Test
    public void windowValueTest(){
        BucketedSlidingWindowedFunction<Integer, Integer, SlidingWindowFunctionIntSum> windowSum =
                new BucketedSlidingWindowedFunction<>(SlidingWindowFunctionIntSum::new, 4);

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
