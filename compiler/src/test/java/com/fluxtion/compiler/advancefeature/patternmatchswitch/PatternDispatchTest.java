package com.fluxtion.compiler.advancefeature.patternmatchswitch;

import com.fluxtion.compiler.EventProcessorConfig.DISPATCH_STRATEGY;
import com.fluxtion.compiler.generation.EventProcessorFactory;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;

public class PatternDispatchTest {

    public static final boolean WRITE_SOURCE_FILE = true;

    public boolean patternSwitchSupported() {
        Runtime.Version version = Runtime.version();
        return version.feature() >= 19;
    }

    @Test
    public void runPatternMatchSwitch() throws Exception {
        assumeTrue("ignore version < Java 19", patternSwitchSupported());
        EventProcessor<?> eventProcessor = EventProcessorFactory.compileTestInstance(
                c -> {
                    c.addNode(new MyShapeHandler(), "shapeHandler");
                    c.setDispatchStrategy(DISPATCH_STRATEGY.PATTERN_MATCH);
                },
                "com.fluxtion.compiler.generation.experimental.patternDispatchTest.runPatternMatchSwitch",
                "TestProcessor", WRITE_SOURCE_FILE, false
        );
        testProcessor(eventProcessor);
    }

    @Test
    public void runInstanceOf() throws Exception {
        assumeTrue("ignore version < Java 19", patternSwitchSupported());
        EventProcessor<?> eventProcessor = EventProcessorFactory.compileTestInstance(
                c -> {
                    c.addNode(new MyShapeHandler(), "shapeHandler");
                    c.setDispatchStrategy(DISPATCH_STRATEGY.INSTANCE_OF);
                },
                "com.fluxtion.compiler.generation.experimental.patternDispatchTest.runInstanceOf",
                "TestProcessor", WRITE_SOURCE_FILE, false
        );
        testProcessor(eventProcessor);
    }

    @Test
    public void runClassName() throws Exception {
        assumeTrue("ignore version < Java 19", patternSwitchSupported());
        EventProcessor<?> eventProcessor = EventProcessorFactory.compileTestInstance(
                c -> {
                    c.addNode(new MyShapeHandler(), "shapeHandler");
                    c.setDispatchStrategy(DISPATCH_STRATEGY.CLASS_NAME);
                },
                "com.fluxtion.compiler.generation.experimental.patternDispatchTest.runClassName",
                "TestProcessor", WRITE_SOURCE_FILE, false
        );
        testProcessor(eventProcessor);
    }

    @Test
    public void objectHandlerPattern() throws Exception {
        assumeTrue("ignore version < Java 19", patternSwitchSupported());
        EventProcessor<?> eventProcessor = EventProcessorFactory.compileTestInstance(
                c -> {
                    c.addNode(new ObjectHandler(), "shapeHandler");
                    c.setDispatchStrategy(DISPATCH_STRATEGY.PATTERN_MATCH);
                },
                "com.fluxtion.compiler.generation.experimental.patternDispatchTest.objectHandlerPattern",
                "TestProcessor", WRITE_SOURCE_FILE, false
        );
    }

    @Test
    public void objectShapeHandlerPattern() throws Exception {
        assumeTrue("ignore version < Java 19", patternSwitchSupported());
        EventProcessor<?> eventProcessor = EventProcessorFactory.compileTestInstance(
                c -> {
                    c.addNode(new ShapeObjectHandler(), "shapeHandler");
                    c.setDispatchStrategy(DISPATCH_STRATEGY.PATTERN_MATCH);
                },
                "com.fluxtion.compiler.generation.experimental.patternDispatchTest.objectShapeHandlerPattern",
                "TestProcessor", WRITE_SOURCE_FILE, false
        );
    }

    @Test
    public void objectShapeHandlerInstanceOf() throws Exception {
        assumeTrue("ignore version < Java 19", patternSwitchSupported());
        EventProcessor<?> eventProcessor = EventProcessorFactory.compileTestInstance(
                c -> {
                    c.addNode(new ShapeObjectHandler(), "shapeHandler");
                    c.setDispatchStrategy(DISPATCH_STRATEGY.INSTANCE_OF);
                },
                "com.fluxtion.compiler.generation.experimental.patternDispatchTest.objectShapeHandlerInstanceOf",
                "TestProcessor", WRITE_SOURCE_FILE, false
        );
    }

    @Test
    public void objectShapeHandlerClassName() throws Exception {
        assumeTrue("ignore version < Java 19", patternSwitchSupported());
        EventProcessor<?> eventProcessor = EventProcessorFactory.compileTestInstance(
                c -> {
                    c.addNode(new ShapeObjectHandler(), "shapeHandler");
                    c.setDispatchStrategy(DISPATCH_STRATEGY.CLASS_NAME);
                },
                "com.fluxtion.compiler.generation.experimental.patternDispatchTest.objectShapeHandlerClassName",
                "TestProcessor", WRITE_SOURCE_FILE, false
        );
    }

    public static class ShapeObjectHandler {
        private int count;

        @OnEventHandler
        public boolean objectUpdate(Object obj) {
            return true;
        }

        @OnEventHandler
        public boolean handleShape(BaseShape baseShape) {
            count++;
            return true;
        }
    }

    public static class ObjectHandler {

        private int count;

        @OnEventHandler
        public boolean objectUpdate(Object obj) {
            return true;
        }
    }

    private void testProcessor(EventProcessor<?> eventProcessor) throws NoSuchFieldException {
        eventProcessor.init();
        eventProcessor.onEvent(new Triangle());
        eventProcessor.onEvent(new Square());
        eventProcessor.onEvent(new Circle());
        eventProcessor.onEvent(new BaseShape());
        eventProcessor.onEvent(new ShapeRectangle());
        eventProcessor.onEvent(new ShapeDiamond());

        MyShapeHandler myShapeHandler = eventProcessor.getNodeById("shapeHandler");
        Assert.assertEquals(8, myShapeHandler.getCount());
    }

}
