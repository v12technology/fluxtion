package com.fluxtion.compiler.generation.constructor;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Test;

public class FailingConstructorTest extends CompiledOnlySepTest {
    public FailingConstructorTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test(expected = RuntimeException.class)
    public void failWithExceptionClashingPrivateFields() {
        sep(c -> c.addNode(new ClashingConstructorTypes("AAA", "BBBB")));
    }

    @Test(expected = RuntimeException.class)
    public void namedParamsInParentClass_FailNoAssignToMember() {
        sep(c -> c.addNode(new MapFieldWithAnnotationTest.FailingChild("bill", "smith")));
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
