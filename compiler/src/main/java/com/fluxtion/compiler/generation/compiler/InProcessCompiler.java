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
package com.fluxtion.compiler.generation.compiler;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.builder.factory.RootInjectedNode;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.EventProcessorGenerator;
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.generation.targets.InMemoryEventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.fluxtion.compiler.generation.GenerationContext.SINGLETON;

/**
 * Generates and compiles a SEP for use by a caller in the same process. The compilation is invoked programmatically
 * removing the need to execute the Fluxtion event stream compiler as an external process.<br><br>
 * <p>
 * To generate a SEP the caller invokes one of the static compileSep methods. An instance of {@link EventProcessorConfig} is passed
 * to the consumer to control the graph construction, such as adding nodes and defining scopes or identifiers. Simple
 * example adding a single node:<br><br>
 * <p>
 * {@code  sepTestInstance((c) -> c.addNode(new MyHandler(), "handler"), "com.fluxtion.examples.inprocess", "GenNode_1");}
 * <br><br>
 * <p>
 * Optionally creates an instance of the compiled StaticEventProcessor with or without calling the init method using one
 * of {@link #sepInstance(Consumer, String, String, String, String, boolean)
 * }.<br><br>
 *
 *
 * @author V12 Technology Ltd.
 */
@Slf4j
public class InProcessCompiler {

    public static StaticEventProcessor sepInstance(Consumer<EventProcessorConfig> cfgBuilder, String pckg, String sepName) throws Exception {
        return sepInstance(cfgBuilder, pckg, sepName, OutputRegistry.JAVA_SRC_DIR, OutputRegistry.RESOURCE_DIR, true);
    }

    public static StaticEventProcessor sepTestInstance(Consumer<EventProcessorConfig> cfgBuilder,
                                                       String pckg,
                                                       String sepName,
                                                       boolean writeSourceFile,
                                                       boolean generateMetaInformation) throws Exception {
        return sepInstance(cfgBuilder, pckg, sepName, OutputRegistry.JAVA_TESTGEN_DIR, OutputRegistry.RESOURCE_TEST_DIR, true, writeSourceFile, generateMetaInformation);
    }

    public static StaticEventProcessor sepInstanceNoInit(Consumer<EventProcessorConfig> cfgBuilder, String pckg, String sepName) throws Exception {
        return sepInstance(cfgBuilder, pckg, sepName, OutputRegistry.JAVA_GEN_DIR, OutputRegistry.RESOURCE_DIR, false);
    }

    public static StaticEventProcessor sepTestInstanceNoInit(Consumer<EventProcessorConfig> cfgBuilder, String pckg, String sepName) throws Exception {
        return sepInstance(cfgBuilder, pckg, sepName, OutputRegistry.JAVA_TESTGEN_DIR, OutputRegistry.RESOURCE_TEST_DIR, false);
    }

    /**
     * Builds an interpreted version of a {@link StaticEventProcessor}
     *
     * @param cfgBuilder
     * @return
     */
    @SneakyThrows
    public static InMemoryEventProcessor interpreted(SerializableConsumer<EventProcessorConfig> cfgBuilder) {
        EventProcessorConfig cfg = new EventProcessorConfig();
        String pkg = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        GenerationContext.setupStaticContext(pkg, "Processor", new File(OutputRegistry.JAVA_GEN_DIR), new File(OutputRegistry.RESOURCE_DIR));
        cfgBuilder.accept(cfg);
        return new EventProcessorGenerator().inMemoryProcessor(cfg, false);
    }

    @SneakyThrows
    public static InMemoryEventProcessor interpreted(RootInjectedNode rootNode) {
        return interpreted((EventProcessorConfig cfg) -> cfg.setRootInjectedNode(rootNode));
    }

    @SneakyThrows
    public static InMemoryEventProcessor interpretedTest(SerializableConsumer<EventProcessorConfig> cfgBuilder) {
        EventProcessorConfig cfg = new EventProcessorConfig();
        cfg.setSupportDirtyFiltering(true);
        cfgBuilder.accept(cfg);
        EventProcessorGenerator eventProcessorGenerator = new EventProcessorGenerator();
        String pkg = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        GenerationContext.setupStaticContext(pkg, "Processor", new File(OutputRegistry.JAVA_TESTGEN_DIR), new File(OutputRegistry.RESOURCE_TEST_DIR));
        return eventProcessorGenerator.inMemoryProcessor(cfg, false);
    }

    /**
     * Build a static event processor using the supplied consumer to populate the SEPConfig. Will always build a new
     * processor, supplying a newly created instance of the class to the caller.
     *
     * <p>
     * Set the system property fluxtion.cacheDirectory and fluxtion will create the following sub-directories:
     * <ul>
     * <li>classes - the compiled classes
     * <li>source - generated source files the classes are compiled from
     * <li>resources - any other resources generated by fluxtion, suchas meta-data
     * </ul>
     *
     * @param name    The name of the generated static event processor
     * @param pkg     The package name of the generated static event processor
     * @param builder The Consumer that populates the SEPConfig
     * @return An instance of the newly generated static event processor
     * @throws Exception
     */
    public static StaticEventProcessor compile(String name, String pkg, Consumer<EventProcessorConfig> builder) throws Exception {
        String dir = System.getProperty("fluxtion.cacheDirectory");
        buildClasspath();
        if (dir != null) {
            System.setProperty("fluxtion.build.outputdirectory", dir + "/classes/");
            return InProcessCompiler.sepInstance(builder, pkg, name, dir + "/source/", dir + "/resources/", true);
        }
        return InProcessCompiler.sepInstance(builder, pkg, name);
    }

    public static StaticEventProcessor compile(SerializableConsumer<EventProcessorConfig> builder) throws Exception {
        String name = "Processor";
        String pkg = (builder.getContainingClass().getCanonicalName() + "." + builder.method().getName()).toLowerCase();
        return (compile(name, pkg, builder));
    }

    public static StaticEventProcessor compile(RootInjectedNode rootNode) throws Exception {
        SerializableConsumer<EventProcessorConfig> builder = (EventProcessorConfig cfg) -> cfg.setRootInjectedNode(rootNode);
        String name = "Processor";
        String pkg = (builder.getContainingClass().getCanonicalName() + "." + builder.method().getName()).toLowerCase();
        String dir = System.getProperty("fluxtion.cacheDirectory");
        buildClasspath();
        if (dir != null) {
            System.setProperty("fluxtion.build.outputdirectory", dir + "/classes/");
            return InProcessCompiler.sepInstance(builder, pkg, name, dir + "/source/", dir + "/resources/", true);
        }
        return InProcessCompiler.sepInstance(builder, pkg, name);
    }

    private static URL[] urlsFromClassLoader(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader) classLoader).getURLs();
        }
        return Stream
                .of(ManagementFactory.getRuntimeMXBean().getClassPath()
                        .split(File.pathSeparator))
                .map(InProcessCompiler::toURL).toArray(URL[]::new);
    }

    private static URL toURL(String classPathEntry) {
        try {
            return new File(classPathEntry).toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(
                    "URL could not be created from '" + classPathEntry + "'", ex);
        }
    }

    private static Pair<Boolean, String> buildClasspath() {
        log.info("buildingClasspath");
        MutablePair<Boolean, String> result = new MutablePair<>(Boolean.TRUE, "");
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = urlsFromClassLoader(cl);
        log.debug("classpath");
        for (URL url : urls) {
            log.info(url.getFile());
        }
        log.info("user classpath URL list:" + Arrays.toString(urls));
        return result;
    }

    /**
     * Compiles and instantiates a SEP described with the provided {@link EventProcessorConfig}, optionally initialising the SEP
     * instance.
     *
     * @param cfgBuilder - A client consumer to buld sep using the provided
     * @param pckg       - output package of the generated class
     * @param sepName    - output class name of the generated SEP
     * @param srcGenDir  - output directory for generated SEP source files
     * @param resGenDir  - output directory for generated resources
     * @param initialise - if true call init method on SEP instance
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws Exception
     */
    public static StaticEventProcessor sepInstance(Consumer<EventProcessorConfig> cfgBuilder,
                                                   String pckg,
                                                   String sepName,
                                                   String srcGenDir,
                                                   String resGenDir,
                                                   boolean initialise,
                                                   boolean writeSourceFile,
                                                   boolean generateMetaInformation) throws InstantiationException, IllegalAccessException, Exception {
        FluxtionCompiler compiler = new FluxtionCompiler();
        final FluxtionCompilerConfig compilerCfg = getSepCompileConfig(pckg, sepName, srcGenDir, resGenDir, writeSourceFile, generateMetaInformation);
        Class<StaticEventProcessor> sepClass = compiler.compile(compilerCfg, new InProcessEventProcessorConfig(cfgBuilder));
        StaticEventProcessor sep = sepClass.getDeclaredConstructor().newInstance();
        if (initialise) {
            if (sep instanceof Lifecycle) {
                ((Lifecycle) sep).init();
            }
        }
        return sep;
    }

    public static StaticEventProcessor sepInstance(Consumer<EventProcessorConfig> cfgBuilder,
                                                   String pckg,
                                                   String sepName,
                                                   String srcGenDir,
                                                   String resGenDir,
                                                   boolean initialise) throws InstantiationException, IllegalAccessException, Exception {
        return sepInstance(cfgBuilder, pckg, sepName, srcGenDir, resGenDir, initialise, true, true);
    }


    private static class InProcessEventProcessorConfig extends EventProcessorConfig {

        private final Consumer<EventProcessorConfig> cfg;

        public InProcessEventProcessorConfig(Consumer<EventProcessorConfig> cfg) {
            this.cfg = cfg;
        }

        @Override
        public void buildConfig() {
            cfg.accept(this);
        }

    }

    public static FluxtionCompilerConfig getSepCompileConfig(
            String packageName,
            String className,
            String srcGenDir,
            String resGenDir,
            boolean writeSourceFile,
            boolean generateMetaInformation) throws IOException {
        File outputDir = new File(srcGenDir);
        File resourcesDir = new File(resGenDir);
        GenerationContext.setupStaticContext(packageName, className, outputDir, resourcesDir);
        FluxtionCompilerConfig cfg = new FluxtionCompilerConfig();
        cfg.setOutputDirectory(SINGLETON.getSourceRootDirectory().getCanonicalPath());
        cfg.setResourcesOutputDirectory(SINGLETON.getResourcesRootDirectory().getCanonicalPath());
        cfg.setPackageName(packageName);
        cfg.setClassName(className);
        cfg.setWriteSourceToFile(writeSourceFile);
        cfg.setGenerateDescription(generateMetaInformation);
        return cfg;
    }
}
