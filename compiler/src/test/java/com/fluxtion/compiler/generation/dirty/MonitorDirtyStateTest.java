package com.fluxtion.compiler.generation.dirty;

import com.fluxtion.compiler.builder.stream.EventFlow;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.stream.EventStream.EventSupplier;
import org.junit.Assert;
import org.junit.Test;

public class MonitorDirtyStateTest extends MultipleSepTargetInProcessTest {
    public MonitorDirtyStateTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void validateDirtyMonitorTest() {
//        writeSourceFile = true;
        sep(c -> {
            MyDirtChecker dirtChecker = new MyDirtChecker();
            dirtChecker.stringEventStream = EventFlow.subscribe(String.class).getEventSupplier();
            dirtChecker.intEventStream = EventFlow.subscribe(Integer.class).getEventSupplier();
            c.addNode(dirtChecker, "dirtChecker");
        });
        MyDirtChecker dirtChecker = getField("dirtChecker");
        Assert.assertFalse(dirtChecker.isStringEventStreamDirty);
        Assert.assertFalse(dirtChecker.isIntEventStreamDirty);
        onEvent("test");
        Assert.assertTrue(dirtChecker.isStringEventStreamDirty);
        Assert.assertFalse(dirtChecker.isIntEventStreamDirty);
        onEvent(200);
        Assert.assertFalse(dirtChecker.isStringEventStreamDirty);
        Assert.assertTrue(dirtChecker.isIntEventStreamDirty);
    }

    public static class MyDirtChecker {

        public EventSupplier<String> stringEventStream;
        public EventSupplier<Integer> intEventStream;
        @Inject
        public DirtyStateMonitor dirtyStateMonitor;

        public boolean isStringEventStreamDirty;
        public boolean isIntEventStreamDirty;

        @OnTrigger
        public boolean triggered() {
            isStringEventStreamDirty = dirtyStateMonitor.isDirty(stringEventStream);
            isIntEventStreamDirty = dirtyStateMonitor.isDirty(intEventStream);
            return true;
        }
    }
}
