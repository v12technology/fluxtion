package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import org.junit.Assert;
import org.junit.Test;

public class SlidingWindowTest extends MultipleSepTargetInProcessTest {

    public SlidingWindowTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void slidingTest() {
        sep(c -> DataFlow
                .subscribe(Integer.class)
                .slidingAggregate(Aggregates.intSumFactory(), 100, 2)
                .id("sum"));

        startTime(0);

        setTime(50);
        onEvent(30);
        setTime(80);
        onEvent(50);
        Assert.assertNull(getStreamed("sum", Integer.class));

        setTime(150);
        onEvent(60);
        Assert.assertNull(getStreamed("sum", Integer.class));


        setTime(230);
        onEvent(70);
        Assert.assertEquals(140, getStreamed("sum", Integer.class).intValue());

        setTime(350);
        onEvent(90);
        Assert.assertEquals(130, getStreamed("sum", Integer.class).intValue());

        tick(550);
        Assert.assertEquals(90, getStreamed("sum", Integer.class).intValue());

        onEvent(888888888);
        Assert.assertEquals(90, getStreamed("sum", Integer.class).intValue());

        setTime(910);
        onEvent(125);
        Assert.assertEquals(0, getStreamed("sum", Integer.class).intValue());


        setTime(1155);
        onEvent(500);
        Assert.assertEquals(125, getStreamed("sum", Integer.class).intValue());

        tick(1199);
        Assert.assertEquals(125, getStreamed("sum", Integer.class).intValue());

        tick(1200);
        Assert.assertEquals(500, getStreamed("sum", Integer.class).intValue());
    }


//    public static void buildGraphSliding(EventProcessorConfig processorConfig) {
//        DataFlow.subscribe(Integer.class)
//                .slidingAggregate(Aggregates.intSumFactory(), 300, 4)
//                .console("current sliding 1.2 second sum:{} eventTime:%dt");
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        var processor = Fluxtion.interpret(SlidingWindowTest::buildGraphSliding);
//        processor.init();
//        Random rand = new Random();
//
//        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
//            executor.scheduleAtFixedRate(
//                    () -> {
////                        processor.onEvent("tick");
//                        processor.onEvent(rand.nextInt(100));
//                    },
//                    10, 10, TimeUnit.MILLISECONDS);
//            Thread.sleep(4_000);
//        }
//    }
}
