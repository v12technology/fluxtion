/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.compiler.generation;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.NodeDispatchTable;
import com.fluxtion.compiler.RootNodeConfig;
import com.fluxtion.compiler.generation.compiler.EventProcessorCompilation;
import com.fluxtion.compiler.generation.compiler.EventProcessorGenerator;
import com.fluxtion.compiler.generation.targets.InMemoryEventProcessor;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
 *
 * @author 2024 gregory higgins.
 */
@Slf4j
public class EventProcessorFactory {

    @SneakyThrows
    public static InMemoryEventProcessor interpreted(SerializableConsumer<EventProcessorConfig> cfgBuilder) {
        return interpreted(cfgBuilder, false);
    }

    @SneakyThrows
    public static InMemoryEventProcessor interpreted(SerializableConsumer<EventProcessorConfig> cfgBuilder, boolean generateDescription) {
        EventProcessorConfig cfg = new EventProcessorConfig();
        String pkg = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        GenerationContext.setupStaticContext(pkg, "Processor", new File(OutputRegistry.JAVA_GEN_DIR), new File(OutputRegistry.RESOURCE_DIR));
        cfgBuilder.accept(cfg);
        return new EventProcessorGenerator().inMemoryProcessor(cfg, generateDescription);
    }

    @SneakyThrows
    public static InMemoryEventProcessor interpreted(RootNodeConfig rootNode) {
        return interpreted((EventProcessorConfig cfg) -> cfg.setRootNodeConfig(rootNode));
    }

    public static InMemoryEventProcessor interpreted(RootNodeConfig rootNode, boolean generateDescription) {
        return interpreted((EventProcessorConfig cfg) -> cfg.setRootNodeConfig(rootNode), generateDescription);
    }

    @SneakyThrows
    public static InMemoryEventProcessor interpretedTest(SerializableConsumer<EventProcessorConfig> cfgBuilder) {
        EventProcessorConfig cfg = new EventProcessorConfig();
        cfgBuilder.accept(cfg);
        EventProcessorGenerator eventProcessorGenerator = new EventProcessorGenerator();
        String pkg = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        GenerationContext.setupStaticContext(pkg, "Processor", new File(OutputRegistry.JAVA_TESTGEN_DIR), new File(OutputRegistry.RESOURCE_GENERATED_TEST_DIR));
        return eventProcessorGenerator.inMemoryProcessor(cfg, false);
    }


    public static EventProcessor compile(SerializableConsumer<EventProcessorConfig> builder) throws Exception {
        String name = "Processor";
        String pkg = (builder.getContainingClass().getCanonicalName() + "." + builder.method().getName()).toLowerCase();
        return compile(name, pkg, builder);
    }

    public static EventProcessor<?> compileDispatcher(SerializableConsumer<EventProcessorConfig> builder, Writer sourceWriter) throws Exception {
        String className = "Processor";
        String packageName = (builder.getContainingClass().getCanonicalName() + "." + builder.method().getName()).toLowerCase();

        FluxtionCompilerConfig compilerCfg = new FluxtionCompilerConfig();
        compilerCfg.setDispatchOnlyVersion(true);
        compilerCfg.setInterpreted(false);
        if (sourceWriter != null) {
            compilerCfg.setSourceWriter(sourceWriter);
        }
        compilerCfg.setPackageName(packageName);
        compilerCfg.setClassName(className);
        compilerCfg.setWriteSourceToFile(false);
        compilerCfg.setFormatSource(true);
        compilerCfg.setGenerateDescription(false);

        EventProcessorCompilation compiler = new EventProcessorCompilation();
        Class<EventProcessor<?>> sepClass = compiler.compile(compilerCfg, new InProcessEventProcessorConfig(builder));
        EventProcessor sep = sepClass.getDeclaredConstructor().newInstance();

        Map<String, Object> instanceMap = new HashMap<>();
        compiler.getSimpleEventProcessorModel().getNodeFields().forEach(f -> instanceMap.put(f.getName(), f.getInstance()));
        ((NodeDispatchTable) sep).assignMembers(instanceMap);
        return sep;
    }

    public static EventProcessor compile(RootNodeConfig rootNode) throws Exception {
        SerializableConsumer<EventProcessorConfig> builder = (EventProcessorConfig cfg) -> cfg.setRootNodeConfig(rootNode);
        String name = "Processor";
        String pkg = (rootNode.getClass().getCanonicalName() + "." + rootNode.getName()).toLowerCase();
        return compile(pkg, name, builder);
    }

    @SneakyThrows
    public static EventProcessor compile(RootNodeConfig rootNode, SerializableConsumer<FluxtionCompilerConfig> cfgBuilder) {
        SerializableConsumer<EventProcessorConfig> builder = (EventProcessorConfig cfg) -> cfg.setRootNodeConfig(rootNode);
        return compile(builder, cfgBuilder);
    }

    public static EventProcessor compileTestInstance(Consumer<EventProcessorConfig> cfgBuilder,
                                                     String pckg,
                                                     String sepName,
                                                     boolean writeSourceFile,
                                                     boolean generateMetaInformation) throws Exception {
        return compile(
                cfgBuilder,
                pckg,
                sepName,
                OutputRegistry.JAVA_TESTGEN_DIR,
                OutputRegistry.RESOURCE_GENERATED_TEST_DIR,
                false,
                false,
                writeSourceFile,
                generateMetaInformation);
    }

    public static EventProcessor compileTestInstance(Consumer<EventProcessorConfig> cfgBuilder,
                                                     String pckg,
                                                     String sepName,
                                                     boolean dispatchOnly,
                                                     boolean writeSourceFile,
                                                     boolean generateMetaInformation) throws Exception {
        return compile(
                cfgBuilder,
                pckg,
                sepName,
                OutputRegistry.JAVA_TESTGEN_DIR,
                OutputRegistry.RESOURCE_GENERATED_TEST_DIR,
                dispatchOnly,
                false,
                writeSourceFile,
                generateMetaInformation);
    }

    /**
     * Build a static event processor using the supplied consumer to populate the SEPConfig. Will always build a new
     * processor, supplying a newly created instance of the class to the caller.
     *
     * @param name    The name of the generated static event processor
     * @param pkg     The package name of the generated static event processor
     * @param builder The Consumer that populates the SEPConfig
     * @return An instance of the newly generated static event processor
     * @throws Exception
     */
    public static EventProcessor compile(String name, String pkg, Consumer<EventProcessorConfig> builder) throws Exception {
        return compile(
                builder,
                pkg,
                name,
                OutputRegistry.JAVA_SRC_DIR,
                OutputRegistry.RESOURCE_DIR,
                false,
                false,
                false,
                false);
    }

    /**
     * Compiles and instantiates a SEP described with the provided {@link EventProcessorConfig}, optionally initialising the SEP
     * instance.
     *
     * @param cfgBuilder  - A client consumer to buld sep using the provided
     * @param packageName - output package of the generated class
     * @param className   - output class name of the generated SEP
     * @param srcGenDir   - output directory for generated SEP source files
     * @param resGenDir   - output directory for generated resources
     * @param initialise  - if true call init method on SEP instance
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws Exception
     */
    public static EventProcessor compile(Consumer<EventProcessorConfig> cfgBuilder,
                                         String packageName,
                                         String className,
                                         String srcGenDir,
                                         String resGenDir,
                                         boolean dispatchOnly,
                                         boolean initialise,
                                         boolean writeSourceFile,
                                         boolean generateMetaInformation) throws InstantiationException, IllegalAccessException, Exception {
        EventProcessorCompilation compiler = new EventProcessorCompilation();
        FluxtionCompilerConfig compilerCfg = new FluxtionCompilerConfig();
        compilerCfg.setDispatchOnlyVersion(dispatchOnly);
        compilerCfg.setOutputDirectory(new File(srcGenDir).getCanonicalPath());
        compilerCfg.setResourcesOutputDirectory(new File(resGenDir).getCanonicalPath());
        compilerCfg.setPackageName(packageName);
        compilerCfg.setClassName(className);
        compilerCfg.setWriteSourceToFile(writeSourceFile);
        compilerCfg.setFormatSource(writeSourceFile);
        compilerCfg.setGenerateDescription(generateMetaInformation);

        Class<EventProcessor> sepClass = compiler.compile(compilerCfg, new InProcessEventProcessorConfig(cfgBuilder));
        EventProcessor sep = sepClass.getDeclaredConstructor().newInstance();
        if (dispatchOnly) {
            Map<String, Object> instanceMap = new HashMap<>();
            compiler.getSimpleEventProcessorModel().getNodeFields().forEach(f -> instanceMap.put(f.getName(), f.getInstance()));
            ((NodeDispatchTable) sep).assignMembers(instanceMap);
        }

        if (initialise) {
            sep.init();
        }
        return sep;
    }

    public static EventProcessor compile(
            SerializableConsumer<EventProcessorConfig> sepConfig,
            SerializableConsumer<FluxtionCompilerConfig> cfgBuilder)
            throws Exception {
        String className = "Processor";
        String packageName = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        FluxtionCompilerConfig fluxtionCompilerConfig = new FluxtionCompilerConfig();
        fluxtionCompilerConfig.setOutputDirectory(new File(OutputRegistry.JAVA_SRC_DIR).getCanonicalPath());
        fluxtionCompilerConfig.setResourcesOutputDirectory(new File(OutputRegistry.RESOURCE_DIR).getCanonicalPath());
        fluxtionCompilerConfig.setPackageName(packageName);
        fluxtionCompilerConfig.setClassName(className);
        fluxtionCompilerConfig.setWriteSourceToFile(true);
        fluxtionCompilerConfig.setFormatSource(true);
        fluxtionCompilerConfig.setGenerateDescription(true);

        cfgBuilder.accept(fluxtionCompilerConfig);
        EventProcessorCompilation compiler = new EventProcessorCompilation();
        Class<EventProcessor<?>> sepClass = compiler.compile(fluxtionCompilerConfig, new InProcessEventProcessorConfig(sepConfig));
        return sepClass == null ? null : sepClass.getDeclaredConstructor().newInstance();
    }

    public static EventProcessor compile(
            EventProcessorConfig eventProcessorConfig,
            FluxtionCompilerConfig fluxtionCompilerConfig)
            throws Exception {
        EventProcessorCompilation compiler = new EventProcessorCompilation();
        Class<EventProcessor<?>> sepClass = compiler.compile(fluxtionCompilerConfig, eventProcessorConfig);
        return fluxtionCompilerConfig.isCompileSource() ? sepClass.getDeclaredConstructor().newInstance() : null;
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
}
