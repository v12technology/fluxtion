package com.fluxtion.compiler.generation.callback;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.callback.CallBackNode;
import lombok.Value;
import org.junit.Test;

public class CallbackNodeTest extends MultipleSepTargetInProcessTest {
    public CallbackNodeTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void directInvokeTest() {
        writeOutputsToFile(true);
        sep(c -> {
            c.addNode(new Child(new ExternalCallback("callback1")));
            c.addNode(new Child(new ExternalCallback("callback2")));
        });

        ExternalCallback callback1 = getField("callback1");
        callback1.doubleEvent(new MyEvent<>(32.4));
        callback1.stringEvent(new MyEvent<>("jjjj"));

        ExternalCallback callback2 = getField("callback2");
        callback2.doubleEvent(new MyEvent<>(32.4));
        callback2.stringEvent(new MyEvent<>("jjjj"));
    }


    public static class ExternalCallback extends CallBackNode {

        Object update;

        public ExternalCallback(String name) {
            super(name);
        }

        public void stringEvent(MyEvent<String> myEvent) {
            update = myEvent.getData();
            triggerGraphCycle();
        }

        public void doubleEvent(MyEvent<Double> myEvent) {
            update = myEvent.getData();
            triggerGraphCycle();
        }

    }

    @Value
    public static class Child {
        ExternalCallback externalCallback;

        @OnTrigger
        public boolean triggered() {
            System.out.println("parent:" + externalCallback.getName() + " updated:" + externalCallback.update);
            return true;
        }
    }

    @Value
    public static class MyEvent<T> {
        T data;
    }
}
