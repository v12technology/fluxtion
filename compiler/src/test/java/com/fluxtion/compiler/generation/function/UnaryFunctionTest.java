package com.fluxtion.compiler.generation.function;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnaryFunctionTest extends MultipleSepTargetInProcessTest {

    public UnaryFunctionTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testClassFilter() {
        sep(cfg -> cfg.addNode(
                new UnaryFunction<>(new StringHandler()::getNumber, UnaryFunctionTest::multiply_10),
                "result"
        ));
        UnaryFunction<String, Integer> uf = getField("result");
        onEvent("hello world");
        assertThat(uf.getIntResult(), is(1_000));
    }


    public static class StringHandler {

        @OnEventHandler
        public boolean onString(String in) {
            return true;
        }

        public Integer getNumber() {
            return 100;
        }
    }

    public static int multiply_10(int in) {
        return 10 * in;
    }
}
