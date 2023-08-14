package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;

public class OverloadedExportedFunctionTest extends MultipleSepTargetInProcessTest {
    public OverloadedExportedFunctionTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void overloadMethodTest() {
        sep(new Incrementer());
        OverloadedService overloadedSvc = sep.getExportedService();
        MutableInt mutableInt = new MutableInt();
        overloadedSvc.increment();
        overloadedSvc.increment(100);
        overloadedSvc.writeValue(mutableInt);
        Assert.assertEquals(101, mutableInt.intValue());
    }

    public interface OverloadedService {
        void increment();

        void increment(int amount);

        void writeValue(MutableInt mutableInt);
    }

    public static class Incrementer implements @ExportService OverloadedService {

        private int currentValue;

        @Override
        public void increment() {
            currentValue++;
        }

        @Override
        public void increment(int amount) {
            currentValue += amount;
        }

        @Override
        public void writeValue(MutableInt mutableInt) {
            mutableInt.setValue(currentValue);
        }
    }
}
