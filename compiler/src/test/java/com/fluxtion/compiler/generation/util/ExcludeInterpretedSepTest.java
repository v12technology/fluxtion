package com.fluxtion.compiler.generation.util;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;

@RunWith(Parameterized.class)
public abstract class ExcludeInterpretedSepTest extends MultipleSepTargetInProcessTest {

    public ExcludeInterpretedSepTest(SepTestConfig sepTestConfig) {
        super(sepTestConfig);
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(
                SepTestConfig.COMPILED_SWITCH_DISPATCH,
                SepTestConfig.COMPILED_METHOD_PER_EVENT,
                SepTestConfig.COMPILED_DISPATCH_ONLY
        );
    }

}
