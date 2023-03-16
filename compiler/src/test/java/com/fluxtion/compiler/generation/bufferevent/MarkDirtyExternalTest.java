package com.fluxtion.compiler.generation.bufferevent;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.node.NamedNode;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class MarkDirtyExternalTest extends MultipleSepTargetInProcessTest {


    public MarkDirtyExternalTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void externalTriggerTest() {
        sep(c -> {
            c.addNode(new Combiner(), "combiner");
        });
        Combiner combiner = getField("combiner");
        IntegerHandler intHandler = getField("intHandler");
        StringHandler stringHandler = getField("stringHandler");


        MatcherAssert.assertThat(combiner.count, CoreMatchers.is(0));
        intHandler.dirtyStateMonitor.markDirty(intHandler);
        triggerCalculation();
        MatcherAssert.assertThat(combiner.count, CoreMatchers.is(1));

        triggerCalculation();
        MatcherAssert.assertThat(combiner.count, CoreMatchers.is(1));

        intHandler.dirtyStateMonitor.markDirty(intHandler);
        stringHandler.dirtyStateMonitor.markDirty(stringHandler);
        triggerCalculation();
        MatcherAssert.assertThat(combiner.count, CoreMatchers.is(2));

    }


    public static class Combiner {
        public IntegerHandler intHandler;
        public StringHandler stringHandler;
        public transient int count;

        public Combiner() {
            intHandler = new IntegerHandler();
            stringHandler = new StringHandler();
        }

        @OnTrigger
        public boolean triggered() {
            count++;
            return true;
        }
    }

    public static class IntegerHandler implements NamedNode {
        Integer value;
        @Inject
        public DirtyStateMonitor dirtyStateMonitor;

        @OnEventHandler
        public boolean onInteger(Integer value) {
            this.value = value;
            return true;
        }

        @Override
        public String getName() {
            return "intHandler";
        }
    }


    public static class StringHandler implements NamedNode {
        String value;
        @Inject
        public DirtyStateMonitor dirtyStateMonitor;

        @OnEventHandler
        public boolean onString(String value) {
            this.value = value;
            return true;
        }

        @Override
        public String getName() {
            return "stringHandler";
        }
    }
}
