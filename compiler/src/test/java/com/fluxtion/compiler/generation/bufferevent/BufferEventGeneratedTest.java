package com.fluxtion.compiler.generation.bufferevent;

import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Value;
import org.junit.Test;

import java.util.Date;

public class BufferEventGeneratedTest extends CompiledOnlySepTest {

    public BufferEventGeneratedTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void triggeredOnlyCbListTest() {
        writeSourceFile = true;
        sep(c -> {
            c.addNode(new Child(new EventHolder()));
        });
    }

    @Test
    public void noTriggerClassTest() {
        writeSourceFile = true;
        sep(c -> {
            c.addNode(new DateHandler());
        });
    }

    @Test
    public void noTriggerClassWithAfterTest() {
        writeSourceFile = true;
        sep(c -> {
            c.addNode(new EventHolder());
        });
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
        public void onDate(Date date) {
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

    public static class DateHandler {
        @OnEventHandler
        public void onDate(Date date) {
        }
    }
}
