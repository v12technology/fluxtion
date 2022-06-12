package com.fluxtion.compiler.generation.inmemory;

import com.fluxtion.compiler.generation.compiler.InProcessCompiler;
import com.fluxtion.compiler.generation.targets.InMemoryEventProcessor;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.TearDown;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
public class InMemoryTest {

    private Recorder recorder;

    @Before
    public void initTest() {
        recorder = new Recorder();
    }

    @Test
    public void test1() {
        InMemoryEventProcessor inMemoryEventProcessor = InProcessCompiler.interpretedTest(
                cfg ->  cfg.addNode(new ChildNode(new StringHandler()))
        );
        assertTrue(recorder.allFalse());

        inMemoryEventProcessor.init();
        assertTrue(recorder.isChildInit());
        assertTrue(recorder.isParentInit());
        assertFalse(recorder.isChildUpdated());
        assertFalse(recorder.isParentUpdated());
        assertFalse(recorder.isChildTeardown());
        assertFalse(recorder.isParentTeardown());

        inMemoryEventProcessor.onEvent("HelloWorld");
        assertTrue(recorder.isChildInit());
        assertTrue(recorder.isParentInit());
        assertTrue(recorder.isChildUpdated());
        assertTrue(recorder.isParentUpdated());
        assertFalse(recorder.isChildTeardown());
        assertFalse(recorder.isParentTeardown());

        inMemoryEventProcessor.tearDown();
        assertTrue(recorder.isChildInit());
        assertTrue(recorder.isParentInit());
        assertTrue(recorder.isChildUpdated());
        assertTrue(recorder.isParentUpdated());
        assertTrue(recorder.isChildTeardown());
        assertTrue(recorder.isParentTeardown());
    }

    @Test
    public void testInterpretedBuilder() {
        InMemoryEventProcessor interpreted = InProcessCompiler.interpretedTest(
                cfg -> cfg.addNode(new ChildNode(new StringHandler()))
        );

        assertTrue(recorder.allFalse());

        interpreted.init();
        assertTrue(recorder.isChildInit());
        assertTrue(recorder.isParentInit());
        assertFalse(recorder.isChildUpdated());
        assertFalse(recorder.isParentUpdated());
        assertFalse(recorder.isChildTeardown());
        assertFalse(recorder.isParentTeardown());

        interpreted.onEvent("HelloWorld");
        assertTrue(recorder.isChildInit());
        assertTrue(recorder.isParentInit());
        assertTrue(recorder.isChildUpdated());
        assertTrue(recorder.isParentUpdated());
        assertFalse(recorder.isChildTeardown());
        assertFalse(recorder.isParentTeardown());

        interpreted.tearDown();
        assertTrue(recorder.isChildInit());
        assertTrue(recorder.isParentInit());
        assertTrue(recorder.isChildUpdated());
        assertTrue(recorder.isParentUpdated());
        assertTrue(recorder.isChildTeardown());
        assertTrue(recorder.isParentTeardown());
    }

    class StringHandler {

        @Initialise
        public void init() {
            recorder.setParentInit(true);
        }

        @TearDown
        public void tearDown() {
            recorder.setParentTeardown(true);
        }

        @OnEventHandler
        public void inString(String in) {
            recorder.setParentUpdated(true);
        }
    }

    @RequiredArgsConstructor
    class ChildNode {
        final StringHandler stringHandler;

        @Initialise
        public void init() {
            recorder.setChildInit(true);
        }

        @TearDown
        public void tearDown() {
            recorder.setChildTeardown(true);
        }

        @OnTrigger
        public void updated() {
            recorder.setChildUpdated(true);
        }

    }

    @Data
    static class Recorder {
        boolean childInit;
        boolean childTeardown;
        boolean childUpdated;

        boolean parentInit;
        boolean parentTeardown;
        boolean parentUpdated;

        boolean allFalse() {
            return !childInit &&
                    !childTeardown &&
                    !childUpdated &&
                    !parentInit &&
                    !parentTeardown &&
                    !parentUpdated;
        }
    }
}
