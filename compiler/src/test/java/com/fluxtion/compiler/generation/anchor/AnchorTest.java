package com.fluxtion.compiler.generation.anchor;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.Anchor;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.helpers.Predicates;
import org.junit.Test;

public class AnchorTest extends MultipleSepTargetInProcessTest {

    public AnchorTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void methodRefTest() {
        writeSourceFile = true;
        sep(c -> {
            c.addNode(new AMyHolder(Predicates.greaterThanBoxed(200)));
        });
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
        private final SerializableFunction<?, ?> function;

        public <T, R> AMyHolder(SerializableFunction<T, R> function) {
            this.function = function;
            Anchor.anchorCaptured(this, function);
        }
    }
}
