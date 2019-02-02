/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler;

import com.fluxtion.builder.annotation.ClassProcessor;
import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.compiler.ClassProcessorDispatcher;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.test.event.TimeEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class AnnotationCompilerTest {

    @Test
    public void testAnnotationLoading() {
        ClassProcessorDispatcher acp = new ClassProcessorDispatcher();
        MyClassProcessor.invokeCount = 0;
        acp.accept(null);
        Assert.assertThat(MyClassProcessor.invokeCount, is(1));
    }

    @Test
    public void testSepBuilderLoading() throws MalformedURLException, ClassNotFoundException {
        JavaTestGeneratorHelper.setupDefaultTestContext("com.fluxtion.compiler.gen", "");
        ClassProcessorDispatcher acp = new ClassProcessorDispatcher();
        acp.accept(new File("target/test-classes").toURI().toURL());
        Assert.assertNotNull(Class.forName("com.fluxtion.compiler.gen.TestEH_1"));
    }

    public static class MyClassProcessor implements ClassProcessor {

        public static int invokeCount = 0;

        @Override
        public void process(URL classPath) {
            invokeCount++;
        }

    }

    @SepBuilder(name = "TestEH_1", packageName = "com.fluxtion.compiler.gen")
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
}
