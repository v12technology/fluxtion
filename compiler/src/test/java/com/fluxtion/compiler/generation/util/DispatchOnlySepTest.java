package com.fluxtion.compiler.generation.util;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public abstract class DispatchOnlySepTest extends MultipleSepTargetInProcessTest {

    public DispatchOnlySepTest(SepTestConfig compile) {
        super(SepTestConfig.COMPILED_DISPATCH_ONLY);
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(SepTestConfig.COMPILED_DISPATCH_ONLY);
    }

}
