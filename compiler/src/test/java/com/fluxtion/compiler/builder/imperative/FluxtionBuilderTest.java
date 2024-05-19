package com.fluxtion.compiler.builder.imperative;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.FluxtionGraphBuilder;
import com.fluxtion.compiler.generation.EventProcessorFactory;
import com.fluxtion.compiler.generation.OutputRegistry;
import com.fluxtion.compiler.generation.compiler.classcompiler.StringCompilation;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Disabled;
import com.fluxtion.runtime.annotations.builder.Inject;
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
import java.util.HashMap;
import java.util.Map;

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
        Fluxtion.compile(c -> {
            c.addNode(new MyStringHandler());
        }, writer);
        Assert.assertFalse(writer.toString().isEmpty());
    }

    @Test
    public void generateToStringWriterTestFailingCompile() {
        System.setErr(new DoNothingPrintStream());
        System.setOut(new DoNothingPrintStream());
        StringWriter writer = new StringWriter();
        try {
            Fluxtion.compile(c -> c.addNode(new MyStringHandler(), "111_var"), writer);
        } catch (Exception e) {
        }
        Assert.assertFalse(writer.toString().isEmpty());
    }

    @Test
    public void generateDispatchOnlyTestSuccess() {
        StringWriter writer = new StringWriter();
        MyStringHandler myStringHandler = new MyStringHandler();
        EventProcessor<?> eventProcessor = Fluxtion.compileDispatcher(c -> {
            c.addNode(myStringHandler);
        }, writer);
        Assert.assertFalse(writer.toString().isEmpty());
        eventProcessor.init();
        eventProcessor.onEvent("TEST");
        Assert.assertEquals("TEST", myStringHandler.in);
    }

    @Test
    public void writeBackupFileForFailedTest() throws IOException {
        final File sampleFile = new File(OutputRegistry.RESOURCE_TEST_DIR + "generator-sample/MyProcessor.sample");
        final String pckg = "xxx.badgen";
        final String className = "MyProcessor";
        //paths
        final String javaTestGenFilePath = OutputRegistry.JAVA_TESTGEN_DIR
                + pckg.replace(".", "/") + "/" + className + ".java";
        final String backupFileName = javaTestGenFilePath + ".backup";
        final String rootPackagePath = OutputRegistry.JAVA_TESTGEN_DIR + "xxx";
        //files
        final File outFile = new File(javaTestGenFilePath);
        final File backupFile = new File(backupFileName);
        final File rootPackageFile = new File(rootPackagePath);
        //clean output files
        FileUtils.deleteQuietly(outFile);
        FileUtils.deleteQuietly(backupFile);
        //copy valid class to outfile
        FileUtils.copyFile(sampleFile, outFile);
        Assert.assertTrue(outFile.exists());
        Assert.assertFalse(backupFile.exists());
        try {
            Assert.assertTrue(outFile.exists());

            EventProcessorFactory.compileTestInstance(
                    c -> c.addNode(new MyStringHandler(), "111_var"),
                    pckg,
                    className, true, false);
        } catch (Exception e) {
            Assert.assertTrue(backupFile.exists());
        } finally {
            FileUtils.deleteQuietly(outFile);
            FileUtils.deleteQuietly(backupFile);
            rootPackageFile.delete();
            FileUtils.deleteQuietly(rootPackageFile);
        }
    }

    @Test
    public void setContext() throws NoSuchFieldException {
        EventProcessor processor = Fluxtion.compile(c -> c.addNode(new MyStringHandler(), "handler"));
        MyStringHandler handler = processor.getNodeById("handler");
        Assert.assertNull(handler.in);
        Map<Object, Object> m = new HashMap<>();
        m.put("started", "BBB");
        processor.setContextParameterMap(m);
        Assert.assertNull(handler.in);
        processor.init();
        Assert.assertEquals("BBB", handler.in);
    }

    @Test
    public void setContextInMemory() throws NoSuchFieldException {
        EventProcessor processor = Fluxtion.interpret(c -> c.addNode(new MyStringHandler(), "handler"));
        MyStringHandler handler = processor.getNodeById("handler");
        Assert.assertNull(handler.in);
        Map<Object, Object> m = new HashMap<>();
        m.put("started", "BBB");
        processor.setContextParameterMap(m);
        Assert.assertNull(handler.in);
        processor.init();
        Assert.assertEquals("BBB", handler.in);
    }


    @Test
    @SneakyThrows
    public void generateToStringWriterAndCompileTest() {
        StringWriter writer = new StringWriter();
        Fluxtion.compile(c -> c.addNode(new MyStringHandler(), "handler"), c -> {
            c.setSourceWriter(writer);
            c.setPackageName("com.whatever");
            c.setClassName("MYProcessor");
            c.setGenerateDescription(false);
        });
        Class<EventProcessor<?>> sepClass = StringCompilation.compile("com.whatever.MYProcessor", writer.toString());
        EventProcessor<?> processor = sepClass.getDeclaredConstructor().newInstance();
        processor.init();
        processor.onEvent("HELLO");
        assertThat(processor.<MyStringHandler>getNodeById("handler").in, is("HELLO"));
    }

    @Test
    @SneakyThrows
    public void generateNoCompileTestDeleteBackup() {
        String path = new File(OutputRegistry.JAVA_TESTGEN_DIR).getCanonicalPath();
        Fluxtion.compile(
                processorCfg -> processorCfg.addNode(new MyStringHandler(), "handler"),
                compilerConfig -> {
                    compilerConfig.setCompileSource(false);
                    compilerConfig.setPackageName("com.whatever");
                    compilerConfig.setClassName("MYProcessor");
                    compilerConfig.setGenerateDescription(false);
                    compilerConfig.setOutputDirectory(path);
                });
        Fluxtion.compile(
                processorCfg -> processorCfg.addNode(new MyStringHandler(), "handler"),
                compilerConfig -> {
                    compilerConfig.setCompileSource(false);
                    compilerConfig.setPackageName("com.whatever");
                    compilerConfig.setClassName("MYProcessor");
                    compilerConfig.setGenerateDescription(false);
                    compilerConfig.setOutputDirectory(path);
                });
    }

    @Test
    public void buildFromFluxtionGraphBuilder() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, URISyntaxException, NoSuchFieldException {
        int generationCount = Fluxtion.scanAndCompileFluxtionBuilders(
                new File("target/test-classes"), new File("target/test-classes"));
        assertThat(generationCount, is(1));
        File generatedFile = new File(PACKAGE_DIR + "/" + PROCESSOR + ".java");
        generatedFile.deleteOnExit();
        String code = FileUtils.readFileToString(
                generatedFile, Charset.defaultCharset());
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
        @Inject
        public EventProcessorContext context;
        String in;

        @OnEventHandler
        public boolean stringUpdated(String in) {
            this.in = in;
            return true;
        }

        public Object getValue() {
            return context.getContextProperty(in);
        }

        @Initialise
        public void init() {
            in = context.getContextProperty("started");
        }
    }
}
