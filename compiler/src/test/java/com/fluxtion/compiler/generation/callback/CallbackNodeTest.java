package com.fluxtion.compiler.generation.callback;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.callback.CallBackNode;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

public class CallbackNodeTest extends MultipleSepTargetInProcessTest {
    public CallbackNodeTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void directInvokeTest() {
        //writeOutputsToFile(true);
        sep(c -> {
            c.addNode(new Child(new ExternalCallback("callback1")), "child1");
            c.addNode(new Child(new ExternalCallback("callback2")), "child2");
        });

        Child child1 = getField("child1");
        Child child2 = getField("child2");

        ExternalCallback callback1 = getField("callback1");
        ExternalCallback callback2 = getField("callback2");


        callback1.doubleEvent(new MyEvent<>(32.4));
        Assert.assertEquals(32.4, (Double) child1.getResult(), 0.0001);
        Assert.assertNull(child2.getResult());

        callback1.stringEvent(new MyEvent<>("jjjj"));
        Assert.assertEquals("jjjj", child1.getResult());
        Assert.assertNull(child2.getResult());

        child1.setResult(null);
        child2.setResult(null);

        callback2.doubleEvent(new MyEvent<>(32.4));
        Assert.assertNull(child1.getResult());
        Assert.assertEquals(32.4, (Double) child2.getResult(), 0.0001);


        callback2.stringEvent(new MyEvent<>("jjjj"));
        Assert.assertNull(child1.getResult());
        Assert.assertEquals("jjjj", child2.getResult());
    }


    public static class ExternalCallback extends CallBackNode implements NamedNode {

        private final String name;
        Object update;

        public ExternalCallback(String name) {
            this.name = name;
        }

        public void stringEvent(MyEvent<String> myEvent) {
            update = myEvent.getData();
            triggerGraphCycle();
        }

        public void doubleEvent(MyEvent<Double> myEvent) {
            update = myEvent.getData();
            triggerGraphCycle();
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Data
    public static class Child {
        private final ExternalCallback externalCallback;
        private Object result;

        @OnTrigger
        public boolean triggered() {
            result = externalCallback.update;
            return true;
        }
    }

    @Value
    public static class MyEvent<T> {
        T data;
    }
}
