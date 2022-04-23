package com.fluxtion.compiler.generation.util;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
@Ignore
public class CompiledAndInterpretedSepTest extends MultipleSepTargetInProcessTest {

    public CompiledAndInterpretedSepTest(SepTestConfig sepTestConfig) {
        super(sepTestConfig.isCompiled());
        inlineCompiled = sepTestConfig == SepTestConfig.COMPILED_INLINE;
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(
                SepTestConfig.COMPILED_INLINE,
                SepTestConfig.COMPILED_METHOD_PER_EVENT,
                SepTestConfig.INTERPRETED
        );
    }

    public enum SepTestConfig {
        COMPILED_INLINE(true), COMPILED_METHOD_PER_EVENT(true), INTERPRETED(false);
        private final boolean compiled;

        public boolean isCompiled() {
            return compiled;
        }

        SepTestConfig(boolean compiled) {
            this.compiled = compiled;
        }
    }
}
