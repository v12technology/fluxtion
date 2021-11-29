package com.fluxtion.generator.function;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.generator.util.MultipleSepTargetInProcessTest;
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


    public static class StringHandler{

        @EventHandler
        public void onString(String in){

        }

        public Integer getNumber(){
            return 100;
        }
    }

    public static int multiply_10(int in){
        return 10 * in;
    }
}
