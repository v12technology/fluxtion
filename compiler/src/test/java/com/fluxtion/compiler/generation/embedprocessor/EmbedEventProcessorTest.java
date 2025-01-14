/*
 * Copyright (c) 2025 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.compiler.generation.embedprocessor;

import com.fluxtion.compiler.generation.embedprocessor.generated.EmbedProcessor;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

public class EmbedEventProcessorTest extends MultipleSepTargetInProcessTest {
    public EmbedEventProcessorTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }


    @SneakyThrows
    @Test
    public void embedGeneratedProcessorTest() {
        EventProcessor<?> embeddedProcessor = new EmbedProcessor();
//        try {
//            Class<EventProcessor<?>> clazz = (Class<EventProcessor<?>>) Class.forName("com.fluxtion.runtime.EventProcessor");
//            processor = clazz.newInstance();
//        } catch (Exception e) {
//            System.out.println("compiling missing event processor");
//            String srcPath = new File(OutputRegistry.JAVA_TEST_SRC_DIR).getCanonicalPath();
//            String classesPath = new File(OutputRegistry.CLASSES_TEST_DIR).getCanonicalPath();
//            processor = Fluxtion.compile(
//                    cfg -> {
//                        cfg.addNode(new EmbeddedNode(), "embeddedNode");
//                    },
//                    compCfg -> {
//                        compCfg.setClassName("EmbedProcessor");
//                        compCfg.setPackageName("com.fluxtion.compiler.generation.embedprocessor.generated");
//                        compCfg.setOutputDirectory(srcPath);
//                        compCfg.setBuildOutputDirectory(classesPath);
//                        compCfg.setCompileSource(true);
//                    }
//            );
//        }

//        final EventProcessor<?> embeddedProcessor = processor;

        sep(c -> {
            MyNode eventProcessor = new MyNode(embeddedProcessor);
            c.addNode(eventProcessor, "myNode");
        });

        MyNode myNode = getField("myNode");
        Integer i = -1;

        Assert.assertFalse(myNode.isTriggered());
        Assert.assertEquals(i, myNode.getI());

        onEvent("hello");
        Assert.assertTrue(myNode.isTriggered());
        Assert.assertNull(myNode.getI());

        onEvent(24);
        i = 24;
        Assert.assertTrue(myNode.isTriggered());
        Assert.assertEquals(i, myNode.getI());
    }

    @Data
    public static class MyNode {

        private final StaticEventProcessor embeddedProcessor;
        private Integer i = -1;
        private boolean triggered = false;

        @OnEventHandler
        public boolean onEvent(String event) {
            return true;
        }

        @SneakyThrows
        @OnTrigger
        public boolean onTrigger() {
            EmbeddedNode embeddedNode = embeddedProcessor.getNodeById("embeddedNode");
            i = embeddedNode.getIntValue();
            triggered = true;
            return true;
        }
    }


    @Data
    public static class EmbeddedNode {

        private Integer intValue;

        @OnEventHandler
        public boolean onEvent(Integer event) {
            intValue = event;
            return true;
        }
    }
}
