package com.fluxtion.compiler.generation.anyobjectasevent;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import lombok.Data;
import org.junit.Test;

public class DefaultHandlerTest extends MultipleSepTargetInProcessTest {
    public DefaultHandlerTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void defaultTest() {
        writeOutputsToFile(true);
        sep(new A(), new B());
        onEvent("test");
        onEvent(1323);
    }

    @Data
    public static class A {
        @OnEventHandler
        public boolean handleObject(Object obj) {
            System.out.println("object handler");
            return true;
        }
    }

    @Data
    public static class B {
        @OnEventHandler
        public boolean handleObject(String obj) {
            System.out.println("string handler");
            return true;
        }
    }
}
