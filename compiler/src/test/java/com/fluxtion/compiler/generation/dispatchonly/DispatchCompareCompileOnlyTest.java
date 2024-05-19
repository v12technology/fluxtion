package com.fluxtion.compiler.generation.dispatchonly;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.SepNode;
import org.junit.Test;

public class DispatchCompareCompileOnlyTest extends MultipleSepTargetInProcessTest {
    public DispatchCompareCompileOnlyTest(CompiledAndInterpretedSepTest.SepTestConfig compile) {
        super(compile);
    }

    @Test
    public void dispatchCompareOnlyTest() {
        MyStringHandler myStringHandler = new MyStringHandler();
        myStringHandler.dataHolder = new DataHolder();
        myStringHandler.intHandler = new MyIntegerHandler();
        myStringHandler.intHandler.x = 100;
        sep(myStringHandler);
    }

    public static class MyStringHandler {
        public DataHolder dataHolder;
        public MyIntegerHandler intHandler;

        @OnEventHandler
        public boolean onString(String in) {
            return true;
        }
    }

    public static class MyIntegerHandler {

        public int x;

        @OnEventHandler
        public boolean onInteger(Integer in) {
            return true;
        }
    }

    @SepNode
    public static class DataHolder {


    }
}
