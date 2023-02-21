package com.fluxtion.compiler.generation.context;

import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import org.junit.Test;

public class InjectContextTest extends CompiledOnlySepTest {
    public InjectContextTest(boolean compile) {
        super(compile);
    }

    @Test
    public void contextTest() throws NoSuchFieldException, IllegalAccessException {
        writeSourceFile = true;
        sep(c ->{
            c.addNode(new NeedContext());
            c.addNode(new NeedContextConstructor(null));
        });

        EventProcessorContext ctxt = (EventProcessorContext) sep.getClass().getDeclaredField("CONTEXT").get(sep);
    }


    public static class NeedContext {

        @Inject
        public EventProcessorContext context;

        @OnEventHandler
        public boolean stringHandler(String input){
            return false;
        }
    }

    public static class NeedContextConstructor {

        @Inject
        private final EventProcessorContext context;

        public NeedContextConstructor(EventProcessorContext context) {
            this.context = context;
        }

        @OnEventHandler
        public boolean stringHandler(String input){
            return false;
        }
    }
}
