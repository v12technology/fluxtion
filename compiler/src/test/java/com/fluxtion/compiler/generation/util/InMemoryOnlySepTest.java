package com.fluxtion.compiler.generation.util;

import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

public class InMemoryOnlySepTest extends MultipleSepTargetInProcessTest {


    public InMemoryOnlySepTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(false);
    }

    @Test
    public void doNothingTest() {
    }
}
