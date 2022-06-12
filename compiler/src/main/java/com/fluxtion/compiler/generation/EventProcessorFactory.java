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
package com.fluxtion.compiler.generation;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.builder.factory.RootInjectedNode;
import com.fluxtion.compiler.generation.compiler.EventProcessorCompilation;
import com.fluxtion.compiler.generation.compiler.EventProcessorGenerator;
import com.fluxtion.compiler.generation.targets.InMemoryEventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.function.Consumer;

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
 *
 * @author V12 Technology Ltd.
 */
@Slf4j
public class EventProcessorFactory {

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
        cfgBuilder.accept(cfg);
        EventProcessorGenerator eventProcessorGenerator = new EventProcessorGenerator();
        String pkg = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        GenerationContext.setupStaticContext(pkg, "Processor", new File(OutputRegistry.JAVA_TESTGEN_DIR), new File(OutputRegistry.RESOURCE_TEST_DIR));
        return eventProcessorGenerator.inMemoryProcessor(cfg, false);
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
        return EventProcessorFactory.compile(pkg, name, builder);
    }

    public static StaticEventProcessor compileTestInstance(Consumer<EventProcessorConfig> cfgBuilder,
                                                           String pckg,
                                                           String sepName,
                                                           boolean writeSourceFile,
                                                           boolean generateMetaInformation) throws Exception {
        return compile(
                cfgBuilder,
                pckg,
                sepName,
                OutputRegistry.JAVA_TESTGEN_DIR,
                OutputRegistry.RESOURCE_TEST_DIR,
                true,
                writeSourceFile,
                generateMetaInformation);
    }

    /**
     * Build a static event processor using the supplied consumer to populate the SEPConfig. Will always build a new
     * processor, supplying a newly created instance of the class to the caller.
     *
     *
     * @param name    The name of the generated static event processor
     * @param pkg     The package name of the generated static event processor
     * @param builder The Consumer that populates the SEPConfig
     * @return An instance of the newly generated static event processor
     * @throws Exception
     */
    public static StaticEventProcessor compile(String name, String pkg, Consumer<EventProcessorConfig> builder) throws Exception {
        return compile(
                builder,
                pkg,
                name,
                OutputRegistry.JAVA_SRC_DIR,
                OutputRegistry.RESOURCE_DIR,
                true,
                true,
                true);
    }

    /**
     * Compiles and instantiates a SEP described with the provided {@link EventProcessorConfig}, optionally initialising the SEP
     * instance.
     *
     * @param cfgBuilder - A client consumer to buld sep using the provided
     * @param packageName       - output package of the generated class
     * @param className    - output class name of the generated SEP
     * @param srcGenDir  - output directory for generated SEP source files
     * @param resGenDir  - output directory for generated resources
     * @param initialise - if true call init method on SEP instance
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws Exception
     */
    public static StaticEventProcessor compile(Consumer<EventProcessorConfig> cfgBuilder,
                                               String packageName,
                                               String className,
                                               String srcGenDir,
                                               String resGenDir,
                                               boolean initialise,
                                               boolean writeSourceFile,
                                               boolean generateMetaInformation) throws InstantiationException, IllegalAccessException, Exception {
        EventProcessorCompilation compiler = new EventProcessorCompilation();
        FluxtionCompilerConfig compilerCfg = new FluxtionCompilerConfig();
        compilerCfg.setOutputDirectory(SINGLETON.getSourceRootDirectory().getCanonicalPath());
        compilerCfg.setResourcesOutputDirectory(SINGLETON.getResourcesRootDirectory().getCanonicalPath());
        compilerCfg.setPackageName(packageName);
        compilerCfg.setClassName(className);
        compilerCfg.setWriteSourceToFile(writeSourceFile);
        compilerCfg.setGenerateDescription(generateMetaInformation);

        Class<StaticEventProcessor> sepClass = compiler.compile(compilerCfg, new InProcessEventProcessorConfig(cfgBuilder));
        StaticEventProcessor sep = sepClass.getDeclaredConstructor().newInstance();
        if (initialise) {
            if (sep instanceof Lifecycle) {
                ((Lifecycle) sep).init();
            }
        }
        return sep;
    }

    public static StaticEventProcessor compile(
            SerializableBiConsumer<EventProcessorConfig,  FluxtionCompilerConfig> cfgBuilder)
            throws Exception {
        String className = "Processor";
        String packageName = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        FluxtionCompilerConfig fluxtionCompilerConfig = new FluxtionCompilerConfig();
        fluxtionCompilerConfig.setOutputDirectory(new File(OutputRegistry.JAVA_SRC_DIR).getCanonicalPath());
        fluxtionCompilerConfig.setResourcesOutputDirectory(new File(OutputRegistry.RESOURCE_DIR).getCanonicalPath());
        fluxtionCompilerConfig.setPackageName(packageName);
        fluxtionCompilerConfig.setClassName(className);
        fluxtionCompilerConfig.setWriteSourceToFile(true);
        fluxtionCompilerConfig.setGenerateDescription(true);
        EventProcessorConfig eventProcessorConfig = new EventProcessorConfig();

        cfgBuilder.accept(eventProcessorConfig, fluxtionCompilerConfig);
        EventProcessorCompilation compiler = new EventProcessorCompilation();
        Class<StaticEventProcessor> sepClass = compiler.compile(fluxtionCompilerConfig, eventProcessorConfig);
        StaticEventProcessor sep = sepClass.getDeclaredConstructor().newInstance();
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).init();
        }
        return sep;
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
