package com.fluxtion.compiler.generation.constructor;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Test;

public class FailingConstructorTest extends MultipleSepTargetInProcessTest {
    public FailingConstructorTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test(expected = RuntimeException.class)
    public void failWithExceptionClashingPrivateFields() {
        sep(c -> c.addNode(new ClashingConstructorTypes("AAA", "BBBB")));
    }

    public static class ClashingConstructorTypes {
        private final String name1;
        private final String name2;

        public ClashingConstructorTypes(String randomName2, String ran1) {
            this.name1 = randomName2;
            this.name2 = ran1;
        }

        @OnEventHandler
        public boolean update(String in) {
            return true;
        }
    }
}
