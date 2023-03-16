package com.fluxtion.compiler.generation.util;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public abstract class CompiledOnlySepTest extends MultipleSepTargetInProcessTest {

    public CompiledOnlySepTest(SepTestConfig compile) {
        super(SepTestConfig.COMPILED_METHOD_PER_EVENT);
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(SepTestConfig.COMPILED_METHOD_PER_EVENT);
    }

}
