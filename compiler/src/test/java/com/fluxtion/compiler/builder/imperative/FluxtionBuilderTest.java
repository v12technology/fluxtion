package com.fluxtion.compiler.builder.imperative;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.FluxtionGraphBuilder;
import com.fluxtion.compiler.generation.compiler.classcompiler.StringCompilation;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Disabled;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FluxtionBuilderTest {

    public static final String OUTPUT_DIRECTORY = "target/generated-test-sources/fluxtion";
    public static final String PACKAGE_NAME = "com.fluxtion.compiler.builder.imperative.buildFromFluxtionGraphBuilder";
    public static final String PACKAGE_DIR = OUTPUT_DIRECTORY + "/" + PACKAGE_NAME.replace(".", "/");
    public static final String PROCESSOR = "MyProcessor";


    @Test
    @SneakyThrows
    public void generateToStringWriterTest() {
        StringWriter writer = new StringWriter();
        Fluxtion.compile(c -> c.addNode(new MyStringHandler()), writer);
        Assert.assertFalse(writer.toString().isEmpty());
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void failCompileString() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnEventHandler\n" +
                "    void stringUpdated() {\n" +
                "        this.in = in;\n" +
                "    }\n" +
                "}";

        StringCompilation.compile("MyStringHandler", source);
    }

    @Test
    @SneakyThrows
    public void generateToStringWriterAndCompileTest() {
        StringWriter writer = new StringWriter();
        Fluxtion.compile(c -> c.addNode(new MyStringHandler(), "handler"), c -> {
            c.setSourceWriter(writer);
            c.setPackageName("com.whatever");
            c.setClassName("MYProcessor");
        });
        Class<EventProcessor> sepClass = StringCompilation.compile("com.whatever.MYProcessor", writer.toString());
        EventProcessor processor = sepClass.getDeclaredConstructor().newInstance();
        processor.init();
        processor.onEvent("HELLO");
        assertThat(processor.<MyStringHandler>getNodeById("handler").in, is("HELLO"));
    }

    @Test
    public void buildFromFluxtionGraphBuilder() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, URISyntaxException, NoSuchFieldException {
        int generationCount = Fluxtion.scanAndCompileFluxtionBuilders(
                new File("target/test-classes"), new File("target/test-classes"));
        assertThat(generationCount, is(1));

        String code = FileUtils.readFileToString(
                new File(PACKAGE_DIR + "/" + PROCESSOR + ".java"), Charset.defaultCharset());
        Class<EventProcessor> processorClass = StringCompilation.compile(PACKAGE_NAME + "." + PROCESSOR, code);
        EventProcessor processor = processorClass.getDeclaredConstructor().newInstance();
        processor.init();
        processor.onEvent("hello world");
        assertThat(processor.<MyStringHandler>getNodeById("handler").in, is("hello world"));
    }

    public static class MyBuilder implements FluxtionGraphBuilder {

        @Override
        public void buildGraph(EventProcessorConfig eventProcessorConfig) {
            eventProcessorConfig.addNode(new MyStringHandler(), "handler");
        }

        @Override
        public void configureGeneration(FluxtionCompilerConfig compilerConfig) {
            compilerConfig.setOutputDirectory(OUTPUT_DIRECTORY);
            compilerConfig.setResourcesOutputDirectory(OUTPUT_DIRECTORY);
            compilerConfig.setGenerateDescription(false);
            compilerConfig.setPackageName(PACKAGE_NAME);
            compilerConfig.setClassName(PROCESSOR);
        }
    }

    @Disabled
    public static class MyBuilder2 implements FluxtionGraphBuilder {

        @Override
        public void buildGraph(EventProcessorConfig eventProcessorConfig) {
            throw new UnsupportedOperationException("should not be called, generation is @Disabled");
        }

        @Override
        public void configureGeneration(FluxtionCompilerConfig compilerConfig) {
            throw new UnsupportedOperationException("should not be called, generation is @Disabled");
        }
    }

    public static class MyStringHandler {
        String in;

        @OnEventHandler
        public void stringUpdated(String in) {
            this.in = in;
        }
    }
}
