package com.fluxtion.compiler.spring.extern;

import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.extern.spring.FluxtionSpring;
import com.fluxtion.compiler.generation.OutputRegistry;
import com.fluxtion.runtime.EventProcessor;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class SpringLoaderTest {

    @Test
    public void loadSingleSpringBeanInterpret() throws NoSuchFieldException {
        Path path = FileSystems.getDefault().getPath("src/test/spring/application-context-test-1.xml");
        EventProcessor<?> eventProcessor = FluxtionSpring.interpret(path);
        eventProcessor.init();
        eventProcessor.onEvent("HELLO WORLD");
        EventBean eventBean = eventProcessor.getNodeById("eventBean");
        Assert.assertEquals("HELLO WORLD", eventBean.input);
    }

    @Test
    public void loadSingleSpringBeanCompile() throws NoSuchFieldException {
        Path path = FileSystems.getDefault().getPath("src/test/spring/application-context-test-1.xml");
        EventProcessor<?> eventProcessor = FluxtionSpring.compile(path);
        eventProcessor.init();
        eventProcessor.onEvent("HELLO WORLD");
        EventBean eventBean = eventProcessor.getNodeById("eventBean");
        Assert.assertEquals("HELLO WORLD", eventBean.input);
    }

    @Test
    public void loadSingleSpringBeanCompileAot() throws NoSuchFieldException {
        Path path = FileSystems.getDefault().getPath("src/test/spring/application-context-test-1.xml");
        EventProcessor<?> eventProcessor = FluxtionSpring.compileAot(path, (FluxtionCompilerConfig c) -> {
            c.setOutputDirectory(OutputRegistry.JAVA_TESTGEN_DIR);
            c.setGenerateDescription(false);
            c.setWriteSourceToFile(false);
        });
        eventProcessor.init();
        eventProcessor.onEvent("HELLO WORLD");
        EventBean eventBean = eventProcessor.getNodeById("eventBean");
        Assert.assertEquals("HELLO WORLD", eventBean.input);
    }

    @Test
    public void customiseConfig() throws NoSuchFieldException {
        Path path = FileSystems.getDefault().getPath("src/test/spring/application-context-test-1.xml");
        EventProcessor<?> eventProcessor = FluxtionSpring.interpret(path, c -> {
            c.addNode(new EventBean(), "customBean");
        });
        eventProcessor.init();
        eventProcessor.onEvent("HELLO WORLD");
        EventBean eventBean = eventProcessor.getNodeById("eventBean");
        Assert.assertEquals("HELLO WORLD", eventBean.input);
        //
        eventBean = eventProcessor.getNodeById("customBean");
        Assert.assertEquals("HELLO WORLD", eventBean.input);
    }

    @Test
    public void loadGraphSpringInterpret() throws NoSuchFieldException {
        Path path = FileSystems.getDefault().getPath("src/test/spring/application-context-test-accountgraph.xml");
        EventProcessor<?> eventProcessor = FluxtionSpring.interpret(path);
        eventProcessor.init();
        Account account = eventProcessor.getExportedService();
        account.credit(12.4);
        account.debit(31.6);
    }
}
