package com.fluxtion.compiler.generation.context;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class InjectContextTest extends MultipleSepTargetInProcessTest {
    public InjectContextTest(boolean compile) {
        super(compile);
    }

    @Test
    public void contextTest() {
        Map<Object, Object> ctxMap = new HashMap<>();
        ctxMap.put("test_1", "AAA");
        ctxMap.put("test_2", "BBB");
        writeSourceFile = true;
        sep(c -> {
            c.addNode(new NeedContext(), "A");
            c.addNode(new NeedContextConstructor(null), "B");
        }, ctxMap);
        NeedContext a = getField("A");
        NeedContextConstructor b = getField("B");

        Assert.assertEquals("AAA", a.value("test_1"));
        Assert.assertEquals("BBB", b.value("test_2"));
        //update map
        ctxMap.put("test_1", "DDD");
        Assert.assertEquals("AAA", a.value("test_1"));
        Assert.assertEquals("AAA", b.value("test_1"));

        //publish changes
        sep.setContextParameterMap(ctxMap);
        Assert.assertEquals("DDD", a.value("test_1"));
        Assert.assertEquals("DDD", b.value("test_1"));
    }


    public static class NeedContext {

        @Inject
        public EventProcessorContext context;

        @OnEventHandler
        public boolean stringHandler(String input) {
            return false;
        }

        public String value(String key) {
            return context.getContextProperty(key);
        }
    }

    public static class NeedContextConstructor {

        @Inject
        private final EventProcessorContext context;
        public String lookup;

        public NeedContextConstructor(EventProcessorContext context) {
            this.context = context;
        }

        public NeedContextConstructor() {
            this(null);
        }

        public String value(String key) {
            return context.getContextProperty(key);
        }

        @OnEventHandler
        public boolean stringHandler(String input) {
            return false;
        }
    }
}
