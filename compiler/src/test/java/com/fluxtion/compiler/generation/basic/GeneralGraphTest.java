package com.fluxtion.compiler.generation.basic;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeneralGraphTest extends MultipleSepTargetInProcessTest {
    public GeneralGraphTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void singleIntegerAddTest() {
        sep(c -> {
            c.addNode(new Integer(23), "value");
        });
        Integer field = getField("value");
        assertThat(field, is(23));

    }
}
