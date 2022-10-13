package com.fluxtion.compiler.generation.anchor;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.Anchor;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableIntFunction;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.helpers.Predicates;
import org.junit.Assert;
import org.junit.Test;

public class AnchorTest extends MultipleSepTargetInProcessTest {

    public AnchorTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void methodRefTest() {
        writeSourceFile = true;
        sep(c -> {
            c.addNode(new AMyHolder(Predicates.gt(200)), "test");
        });
        AMyHolder holder = getField("test");
        Assert.assertTrue(holder.test(300));
        Assert.assertFalse(holder.test(100));
    }

    @Test
    public void testAnchor() {
        writeSourceFile = true;
        sep(c -> {
            FilterGroupByFunctionInvoker fg = new FilterGroupByFunctionInvoker(Predicates.greaterThanBoxed(25000));
            c.addNode(fg);
        });
    }


    public static class AMyHolder {
        private final SerializableIntFunction<Boolean> function;

        public AMyHolder(SerializableIntFunction<Boolean> function) {
            this.function = function;
            Anchor.anchorCaptured(this, function);
        }

        public boolean test(int value) {
            return function.apply(value);
        }
    }
}
