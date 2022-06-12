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
import com.fluxtion.compiler.builder.factory.NodeFactoryRegistration;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.EventProcessorGenerator;
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.generation.compiler.classcompiler.StringCompilation;
import com.fluxtion.compiler.builder.factory.NodeFactoryLocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An executable class that will parse a set of class files to produce a SEP
 * that can be used in isolation from this generator.
 *
 * @author Greg Higgins
 */
public class FluxtionCompiler {

    private static final Logger LOG = LoggerFactory.getLogger(FluxtionCompiler.class);
    private FluxtionCompilerConfig compilerConfig;
    private EventProcessorConfig builderConfig;

    public <T> Class<T> compile(FluxtionCompilerConfig compilerConfig, EventProcessorConfig configOverride) throws Exception {
        LOG.debug("starting SEP compiler");
        this.compilerConfig = compilerConfig;
        initialiseGenerator(configOverride);
        locateFactories();
        processYamlConfig();
        Class<?> returnClass = generateSep();
        LOG.debug("finished SEP compiler");
        return (Class<T>) returnClass;
    }

    private void initialiseGenerator(EventProcessorConfig configOverride) {
        LOG.debug("initialiseGenerator");
        LOG.debug(compilerConfig.toString());
        File buildDir = compilerConfig.getBuildOutputDirectory() == null ? null : new File(compilerConfig.getBuildOutputDirectory());
        GenerationContext.setupStaticContext(compilerConfig.getClassLoader(), compilerConfig.getPackageName(),
                compilerConfig.getClassName(),
                new File(compilerConfig.getOutputDirectory()),
                new File(compilerConfig.getResourcesOutputDirectory()),
                compilerConfig.isGenerateDescription(),
                buildDir,
                true);
        builderConfig = configOverride;
        builderConfig.setTemplateFile(compilerConfig.getTemplateSep());
    }

    //TODO - rewrite so can override the RootInjectedNode in SEPConfig
    private void processYamlConfig() throws Exception {
//        LOG.debug("starting :: processYamlConfig - cfg{}", compilerConfig.getYamlFactoryConfig());
//        if (compilerConfig.getYamlFactoryConfig() != null && !compilerConfig.getYamlFactoryConfig().isEmpty()) {
//            File yamlFactoryConfig = new File(compilerConfig.getYamlFactoryConfig());
//            LOG.debug("processing yaml factory config file:" + yamlFactoryConfig.getCanonicalPath());
//            try (InputStream input = Files.newInputStream(yamlFactoryConfig.toPath())) {
//                Yaml beanLoader = new Yaml();
//                LOG.debug("loading SepFactoryConfigBean with beanLoader");
//                SepFactoryConfigBean loadedConfig = beanLoader.loadAs(input, SepFactoryConfigBean.class);
//                LOG.debug("DeclarativeNodeConfiguration load");
//                NodeFactoryRegistration cfgActual = loadedConfig.asDeclarativeNodeConfiguration();
//                LOG.debug("searching for NodeFactory's");
//                Set<Class<? extends NodeFactory<?>>> class2Factory = NodeFactoryLocator.nodeFactorySet();
//                cfgActual.factoryClassSet.addAll(class2Factory);
//                builderConfig.setDeclarativeConfig(cfgActual);
//                LOG.debug("completed :: processYamlConfig ");
//            }
//        } else {
//            LOG.debug("no yaml factory config file specified");
//        }
    }

    private void locateFactories() {
        LOG.debug("locateFactories");
        if (builderConfig.getNodeFactoryRegistration() == null) {
            builderConfig.setNodeFactoryRegistration(new NodeFactoryRegistration(NodeFactoryLocator.nodeFactorySet()));
        } else {
            builderConfig.getNodeFactoryRegistration().factoryClassSet.addAll(NodeFactoryLocator.nodeFactorySet());
        }
    }

    private Class<?> generateSep() throws Exception {
        LOG.debug("generateSep");
        Class<?> returnClass = null;
        Writer writer;
        if(compilerConfig.isWriteSourceToFile()){
            File outFile = new File(GenerationContext.SINGLETON.getPackageDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".java");
            outFile.getParentFile().mkdirs();
            writer = new FileWriter(outFile);
        }else{
            writer = new StringWriter();
        }

        EventProcessorGenerator eventProcessorGenerator = new EventProcessorGenerator();
        eventProcessorGenerator.templateSep(builderConfig, compilerConfig.isGenerateDescription(), writer);
        GenerationContext generationConfig = GenerationContext.SINGLETON;
        String fqn = generationConfig.getPackageName() + "." + generationConfig.getSepClassName();
        File file = new File(generationConfig.getPackageDirectory(), generationConfig.getSepClassName() + ".java");
        LOG.info("generated sep: " + file.getCanonicalPath());
        if (compilerConfig.isWriteSourceToFile() && compilerConfig.isFormatSource()) {
            LOG.debug("start formatting source");
            EventProcessorGenerator.formatSource(file);
            LOG.debug("completed formatting source");
        }
        if (compilerConfig.isCompileSource()) {
            LOG.debug("start compiling source");
            if(compilerConfig.isWriteSourceToFile()){
                returnClass = StringCompilation.compile(fqn, readText(file.getCanonicalPath()));
            }else{
                returnClass = StringCompilation.compile(fqn, writer.toString());
            }
            LOG.debug("completed compiling source");
        }
        return returnClass;
    }

    private static String readText(@NotNull String resourceName) throws IOException {
        LOG.debug("starting reading:" + resourceName);
        StringWriter sw = new StringWriter();
        Reader isr = new InputStreamReader(getInputStream(resourceName), UTF_8);
        try {
            char[] chars = new char[8 * 1024];
            int len;
            while ((len = isr.read(chars)) > 0) {
                sw.write(chars, 0, len);
            }
        } finally {
            close(isr);
        }
        LOG.debug("finished reading:" + resourceName);
        return sw.toString();
    }

    private static void close(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOG.trace("Failed to close {}", closeable, e);
            }
        }
    }

    private static InputStream getInputStream(@NotNull String filename) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = contextClassLoader.getResourceAsStream(filename);
        if (is != null) {
            return is;
        }
        InputStream is2 = contextClassLoader.getResourceAsStream('/' + filename);
        if (is2 != null) {
            return is2;
        }
        return Files.newInputStream(Paths.get(filename));
    }

}
