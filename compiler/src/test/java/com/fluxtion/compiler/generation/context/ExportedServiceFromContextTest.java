package com.fluxtion.compiler.generation.context;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.EventProcessorContextListener;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

public class ExportedServiceFromContextTest extends MultipleSepTargetInProcessTest {
    public ExportedServiceFromContextTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void contextListenerTest() {
        sep(new MyBroadcastSubscriber());
        MyBroadcastSubscriber subscriber = getField("MyBroadcastSubscriber");
        Assert.assertNotNull("context should not be null", subscriber.getContext());
    }

    @Test
    public void cbServiceTest() {
        sep(new MyBroadcastSubscriber());

        MyBroadcasterImpl myBroadcaster = new MyBroadcasterImpl();
        sep.registerService(myBroadcaster, Broadcaster.class);

        myBroadcaster.publish("hello");

        MyBroadcastSubscriber subscriber = getField("MyBroadcastSubscriber");
        Assert.assertEquals("hello", subscriber.getIn());
    }


    public interface BroadcastListener {
        boolean newMessage(String in);
    }

    public interface Broadcaster {
        void register(BroadcastListener cb);
    }

    private static class MyBroadcasterImpl implements Broadcaster {

        private BroadcastListener cb;

        @Override
        public void register(BroadcastListener cb) {
            this.cb = cb;
        }

        public void publish(String in) {
            cb.newMessage(in);
        }
    }

    public static class MyBroadcastSubscriber
            implements
            @ExportService BroadcastListener,
            EventProcessorContextListener,
            NamedNode {

        @Getter
        private String in;
        @Getter
        private EventProcessorContext context;
        private Broadcaster broadcaster;

        @Override
        public void currentContext(EventProcessorContext currentContext) {
            this.context = currentContext;
        }

        @ServiceRegistered
        public void broadcaster(Broadcaster broadcaster) {
            this.broadcaster = broadcaster;
            broadcaster.register(context.getExportedService());
        }

        @Override
        public boolean newMessage(String in) {
            Assert.assertNotNull("broadcaster should be non-null", broadcaster);
            this.in = in;
            return false;
        }

        @Override
        public String getName() {
            return "MyBroadcastSubscriber";
        }
    }
}
