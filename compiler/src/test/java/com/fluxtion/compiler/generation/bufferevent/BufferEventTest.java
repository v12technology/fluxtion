package com.fluxtion.compiler.generation.bufferevent;

import com.fluxtion.compiler.generation.model.CbMethodHandle;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.InMemoryOnlySepTest;
import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class BufferEventTest extends InMemoryOnlySepTest {

    public BufferEventTest(SepTestConfig compiledSep) {
        super(SepTestConfig.INTERPRETED);
    }

    @Test
    public void triggeredOnlyCbListTest() {
        sep(c -> {
            c.addNode(new Child(new EventHolder()));
        });
        List<CbMethodHandle> triggerOnlyCallBacks = simpleEventProcessorModel.getTriggerOnlyCallBacks();
        Assert.assertEquals(1, triggerOnlyCallBacks.size());
        Assert.assertEquals("triggered", triggerOnlyCallBacks.get(0).getMethod().getName());
    }

    public static class EventHolder {

        @OnEventHandler
        public boolean onString(String in) {
            return true;
        }

        @AfterEvent
        public void afterHolderEvent() {
        }

        @AfterTrigger
        public void afterHolderTrigger() {
        }

        @Initialise
        public void initHolder() {
        }
    }

    @Value
    public static class Child {
        EventHolder parent;

        @OnTrigger
        public boolean triggered() {
            return true;
        }

        @OnEventHandler
        public boolean onDate(Date date) {
            return true;
        }

        @OnParentUpdate
        public void onParent(EventHolder parent) {
        }

        @AfterEvent
        public void afterEvent() {
        }

        @Initialise
        public void initChild() {
        }
    }
}
