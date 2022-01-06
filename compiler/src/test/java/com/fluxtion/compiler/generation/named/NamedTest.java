package com.fluxtion.compiler.generation.named;

import com.fluxtion.runtime.Named;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.Value;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class NamedTest extends MultipleSepTargetInProcessTest {

    public static final String UNIQUE_NAME = "UniqueName";

    public NamedTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testNaming(){
        sep(c -> c.addNode(new NameMe(UNIQUE_NAME)));
        NameMe node = getField(UNIQUE_NAME);
        assertNotNull(node);
    }

    @Value
    public static class NameMe implements Named{

        String name;
        @Override
        public String getName() {
            return name;
        }
    }
}
