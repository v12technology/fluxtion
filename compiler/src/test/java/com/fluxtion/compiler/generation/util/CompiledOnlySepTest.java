package com.fluxtion.compiler.generation.util;

import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public abstract class CompiledOnlySepTest extends MultipleSepTargetInProcessTest {

    public CompiledOnlySepTest(boolean compile) {
        super(true);
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(true);
    }

}
