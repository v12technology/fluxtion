/* 
 * Copyright (c) 2019, V12 Technology Ltd.
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
package com.fluxtion.compiler;

import static org.hamcrest.CoreMatchers.is;

import com.fluxtion.builder.annotation.ClassProcessor;
import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.annotation.SepInstance;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.compiler.ClassProcessorDispatcher;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import com.fluxtion.test.event.TimeEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class AnnotatedCompilerTest {

    @Test
    public void testAnnotationLoading() {
        ClassProcessorDispatcher acp = new ClassProcessorDispatcher();
        MyClassProcessor.invokeCount = 0;
        acp.accept(null, null);
        Assert.assertThat(MyClassProcessor.invokeCount, is(1));
    }

    @Test
    public void testSepBuilderLoading() throws MalformedURLException, ClassNotFoundException {
        JavaTestGeneratorHelper.setupDefaultTestContext("com.fluxtion.compiler.gen.methoduilder", "");
        ClassProcessorDispatcher acp = new ClassProcessorDispatcher();
        acp.accept(new File("target/test-classes").toURI().toURL(), new File("."));
        Assert.assertNotNull(Class.forName("com.fluxtion.compiler.gen.methoduilder.TestEH_1"));
        Assert.assertNotNull(Class.forName("com.fluxtion.compiler.gen.classbuilder.TestNode_1"));
    }

    public static class MyClassProcessor implements ClassProcessor {

        public static int invokeCount = 0;

        @Override
        public void process(URL classPath) {
            invokeCount++;
        }

    }

    @SepBuilder(name = "TestEH_1", packageName = "com.fluxtion.compiler.gen.methoduilder", cleanOutputDir = true)
    public void buildSepTest(SEPConfig cfg) {
        cfg.addNode(new MyHandler());
    }

    public static class MyHandler {

        int count;

        @com.fluxtion.api.annotations.EventHandler
        public void onAllTimeEvents(TimeEvent e) {
            count++;
        }
    }

    @SepInstance(
            name = "TestNode_1",
            packageName = "com.fluxtion.compiler.gen.classbuilder",
            cleanOutputDir = true
    )
    public static class MyNodeIntsance {

        int count;

        @com.fluxtion.api.annotations.EventHandler
        public void onAllTimeEvents(TimeEvent e) {
            count++;
        }
    }
}
