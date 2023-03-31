package com.fluxtion.compiler.generation.constructor;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import org.junit.Test;

public class CommonSuperTypeTest extends MultipleSepTargetInProcessTest {
    public CommonSuperTypeTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void testConstructor() {
        writeSourceFile = true;
        sep(c -> c.addNode(new Aggregator()));

    }

    public static class BaseClass {
        @OnEventHandler
        public boolean handleString(String in) {
            return true;
        }
    }

    public static class SubA extends BaseClass {

    }

    public static class SubB extends BaseClass {

    }

    public static class Aggregator {
        private final SubA subA;
        private final SubB subB;

        public Aggregator() {
            this(new SubA(), new SubB());
        }

        public Aggregator(SubA subA, SubB subB) {
            this.subA = subA;
            this.subB = subB;
        }

        @OnTrigger
        public boolean updated() {
            return true;
        }
    }
}
