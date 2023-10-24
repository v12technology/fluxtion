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
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.builder.factory.NodeFactoryLocator;
import com.fluxtion.compiler.builder.factory.NodeFactoryRegistration;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.RuntimeConstants;
import com.fluxtion.compiler.generation.compiler.classcompiler.StringCompilation;
import com.google.googlejavaformat.java.Formatter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An executable class that will parse a set of class files to produce a SEP
 * that can be used in isolation from this generator.
 *
 * @author Greg Higgins
 */
public class EventProcessorCompilation {

    private static final Logger LOG = LoggerFactory.getLogger(EventProcessorCompilation.class);
    private FluxtionCompilerConfig compilerConfig;
    private EventProcessorConfig builderConfig;

    public <T> Class<T> compile(FluxtionCompilerConfig compilerConfig, EventProcessorConfig configOverride) throws Exception {
        LOG.debug("starting SEP compiler");
        this.compilerConfig = compilerConfig;
        initialiseGenerator(configOverride);
        locateFactories();
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
        File backupFile = null;
        boolean formatSuccess = true;
        if (compilerConfig.isWriteSourceToFile()) {
            File outFile = new File(GenerationContext.SINGLETON.getPackageDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".java");
            outFile.getParentFile().mkdirs();
            if (outFile.exists()) {
                backupFile = new File(outFile.getParentFile(), outFile.getName() + ".backup");
                if (backupFile.exists()) {
                    throw new RuntimeException("Fluxtion generation problem backup file exists - please move or delete file:" + backupFile.getCanonicalPath());
                }
                FileUtils.moveFile(outFile, backupFile);
            }
            writer = new FileWriter(outFile);
        } else {
            writer = new StringWriter();
        }

        EventProcessorGenerator eventProcessorGenerator = new EventProcessorGenerator();
        eventProcessorGenerator.templateSep(builderConfig, compilerConfig, writer);
        GenerationContext generationConfig = GenerationContext.SINGLETON;
        String fqn = generationConfig.getPackageName() + "." + generationConfig.getSepClassName();
        File file = new File(generationConfig.getPackageDirectory(), generationConfig.getSepClassName() + ".java");
        if (compilerConfig.isWriteSourceToFile()) {
            LOG.info("generated EventProcessor file: " + file.getCanonicalPath());
        } else {
            LOG.info("generated EventProcessor in memory");
            try {
                if (compilerConfig.isFormatSource()) {
                    String formatSource = new Formatter().formatSource(writer.toString());
                    writer = new StringWriter();
                    writer.write(formatSource);
                }
                if (compilerConfig.getSourceWriter() != null) {
                    compilerConfig.getSourceWriter().write(writer.toString());
                    writer = compilerConfig.getSourceWriter();
                }
            } catch (Throwable t) {
                formatSuccess = false;
                if (compilerConfig.getSourceWriter() != null) {
                    compilerConfig.getSourceWriter().write(writer.toString());
                    writer = compilerConfig.getSourceWriter();
                }
            }
        }
        if (compilerConfig.isWriteSourceToFile() && compilerConfig.isFormatSource()) {
            LOG.debug("start formatting source");
            EventProcessorGenerator.formatSource(file);
            LOG.debug("completed formatting source");
        }
        if (compilerConfig.isCompileSource() && !Boolean.getBoolean(RuntimeConstants.FLUXTION_NO_COMPILE)) {
            LOG.debug("start compiling source");
            if (compilerConfig.isWriteSourceToFile()) {
                builderConfig.getCompilerOptions();
                returnClass = StringCompilation.compile(fqn, readText(file.getCanonicalPath()),
                        builderConfig.getCompilerOptions());
            } else {
                returnClass = StringCompilation.compile(fqn, writer.toString(),
                        builderConfig.getCompilerOptions());
            }
            LOG.debug("completed compiling source");
            if (backupFile != null) {
                FileUtils.delete(backupFile);
                backupFile = null;
            }
        } else if (backupFile != null && formatSuccess) {
            FileUtils.delete(backupFile);
        }
        if (backupFile != null && !formatSuccess) {
            FileUtils.delete(backupFile);
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
