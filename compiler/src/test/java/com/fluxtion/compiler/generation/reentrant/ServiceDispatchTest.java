package com.fluxtion.compiler.generation.reentrant;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ServiceDispatchTest extends MultipleSepTargetInProcessTest {
    public ServiceDispatchTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    @Ignore
    public void testInternalServiceDispatch() {
        writeSourceFile = true;
        addAuditor();
        sep(c -> c.addNode(new StringConsumer()));
        onEvent("test");
        StringConsumer stringConsumer = getField("node1");
        Assert.assertEquals("test", stringConsumer.getMyEventString());
        Assert.assertEquals("callback", stringConsumer.getMyString());
    }

    @Getter
    @Setter
    public static class StringConsumer implements @ExportService StringHandler, NamedNode {
        private String myString;
        private String myEventString;
        @Inject
        private EventDispatcher eventDispatcher;

        @OnEventHandler
        public boolean stringUpdate(String s) {
            myEventString = s;
            StringHandler consumer = eventDispatcher.getService();
            consumer.processString("callback");
            return true;
        }

        @Override
        public void processString(String s) {
            myString = s.toString();
        }

        @Override
        public String getName() {
            return "node1";
        }
    }

    public interface StringHandler {
        void processString(String in);
    }
}
