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
package com.fluxtion.generator.compiler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.DeclarativeNodeConiguration;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.Generator;
import com.fluxtion.generator.graphbuilder.NodeFactoryLocator;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import net.openhft.compiler.CachedCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * An executable class that will parse a set of class files to produce a SEP
 * that can be used in isolation from this generator.
 *
 * @author Greg Higgins
 */
public class SepCompiler {

    private static final Logger LOG = LoggerFactory.getLogger(SepCompiler.class);
    private SepCompilerConfig compilerConfig;
    private SEPConfig builderConfig;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
        StatusPrinter.print(lc);
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();
        LOG.debug("classpath");
        for (URL url : urls) {
            LOG.debug(url.getFile());
        }
        SepCompiler compiler = new SepCompiler();
        compiler.compile();
    }

    /**
     * Compile method initialising SepCompilerConfig from system properties, see
     *
     * @see SepCompilerConfig#initFromSystemProperties() System properties
     * mapping.
     * @throws ClassNotFoundException exception during compile
     * @throws InstantiationException exception during compile
     * @throws IllegalAccessException exception during compile
     * @throws Exception exception during compile
     */
    public void compile() throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
        compile(SepCompilerConfig.initFromSystemProperties());
    }

    /**
     * Compile method using a provided SepCompilerConfig
     *
     * @param compilerConfig the config to drive the SEP generation process
     * @throws ClassNotFoundException exception during compile
     * @throws InstantiationException exception during compile
     * @throws IllegalAccessException exception during compile
     * @throws Exception exception during compile
     */
    public void compile(SepCompilerConfig compilerConfig) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
//        LOG.debug("starting SEP compiler");
//        this.compilerConfig = compilerConfig;
//        initialiseGenerator();
//        initialiseNamingStrategy();
//        locateFactories();
//        processYamlConfig();
//        processRootFactoryConfig();
//        generateSep();
//        LOG.debug("finished SEP compiler");
        compile(compilerConfig, null);
    }

    public void compile(SepCompilerConfig compilerConfig, SEPConfig configOverride) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
        LOG.debug("starting SEP compiler");
        this.compilerConfig = compilerConfig;
        initialiseGenerator(configOverride);
        locateFactories();
        processYamlConfig();
        processRootFactoryConfig();
        generateSep();
        LOG.debug("finished SEP compiler");
    }

    private void initialiseGenerator(SEPConfig configOverride) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        LOG.debug("initialiseGenerator");
        LOG.debug(compilerConfig.toString());
        File buildDir = compilerConfig.getBuildOutputdirectory() == null ? null : new File(compilerConfig.getBuildOutputdirectory());
        GenerationContext.setupStaticContext(compilerConfig.getClassLoader(), compilerConfig.getPackageName(),
                compilerConfig.getClassName(),
                new File(compilerConfig.getOutputDirectory()),
                new File(compilerConfig.getResourcesOutputDirectory()),
                compilerConfig.isGenerateDebugPrep() || compilerConfig.isGenerateDescription(),
                buildDir,
                true,
                compilerConfig.getCachedCompiler());
        //compiler
        if (configOverride == null) {
            Class rootClazz = compilerConfig.getClassLoader().loadClass(compilerConfig.getConfigClass());
            builderConfig = (SEPConfig) rootClazz.newInstance();
        } else {
            builderConfig = configOverride;
        }
        builderConfig.templateFile = compilerConfig.getTemplateSep();
        builderConfig.debugTemplateFile = compilerConfig.getTemplateDebugSep();
        builderConfig.supportDirtyFiltering = compilerConfig.isSupportDirtyFiltering();
        //TODO add configuration back in when split png and debug generation
        builderConfig.generateDebugPrep = compilerConfig.isGenerateDebugPrep();
        builderConfig.generateDescription = compilerConfig.isGenerateDescription();
        builderConfig.generateTestDecorator = compilerConfig.isGenerateTestDecorator();
        builderConfig.assignPrivateMembers = compilerConfig.isAssignNonPublicMembers();
    }

    private void processYamlConfig() throws IOException, ClassNotFoundException, Exception {
        LOG.debug("starting :: processYamlConfig - cfg{}", compilerConfig.getYamlFactoryConfig());
        if (compilerConfig.getYamlFactoryConfig() != null && !compilerConfig.getYamlFactoryConfig().isEmpty()) {
            File yamlFactoryConfig = new File(compilerConfig.getYamlFactoryConfig());
            LOG.debug("processing yaml factory config file:" + yamlFactoryConfig.getCanonicalPath());
            InputStream input = new FileInputStream(yamlFactoryConfig);
            Yaml beanLoader = new Yaml();
            LOG.debug("loading SepFactoryConfigBean with beanLoader");
            SepFactoryConfigBean loadedConfig = beanLoader.loadAs(input, SepFactoryConfigBean.class);
            LOG.debug("DeclarativeNodeConiguration load");
            DeclarativeNodeConiguration cfgActual = loadedConfig.asDeclarativeNodeConiguration();
            LOG.debug("searching for NodeFactory's");
            Set<Class<? extends NodeFactory>> class2Factory = NodeFactoryLocator.nodeFactorySet();
            cfgActual.factoryClassSet.addAll(class2Factory);
            builderConfig.declarativeConfig = cfgActual;
            LOG.debug("completed :: processYamlConfig ");
        } else {
            LOG.debug("no yaml factory config file specified");
        }
    }

    private void processRootFactoryConfig() throws ClassNotFoundException, Exception {
        LOG.debug("processRootFactoryConfig");
        if (compilerConfig.getRootFactoryClass() != null && !compilerConfig.getRootFactoryClass().isEmpty()) {
            if (builderConfig.declarativeConfig == null) {
                Map<String, String> rootNodeMappings = new HashMap<>();
                rootNodeMappings.put(compilerConfig.getRootFactoryClass(), "root");
                SepFactoryConfigBean loadedConfig = new SepFactoryConfigBean();
                loadedConfig.setRootNodeMappings(rootNodeMappings);
                loadedConfig.setConfig(new HashMap());
                DeclarativeNodeConiguration cfgActual = loadedConfig.asDeclarativeNodeConiguration();
                Set<Class<? extends NodeFactory>> class2Factory = NodeFactoryLocator.nodeFactorySet();
                cfgActual.factoryClassSet.addAll(class2Factory);
                builderConfig.declarativeConfig = cfgActual;
            } else {
                builderConfig.declarativeConfig.rootNodeMappings.put(builderConfig.getClass(), "root");
            }
        }
    }

    private void locateFactories() throws Exception {
        LOG.debug("locateFactories");
        SepFactoryConfigBean loadedConfig = new SepFactoryConfigBean();
        Set<Class<? extends NodeFactory>> class2Factory = NodeFactoryLocator.nodeFactorySet();
        loadedConfig.setConfig(new HashMap());
        DeclarativeNodeConiguration cfgActual = loadedConfig.asDeclarativeNodeConiguration();
        if (builderConfig == null || builderConfig.declarativeConfig==null) {
            cfgActual.factoryClassSet.addAll(class2Factory);
            builderConfig.declarativeConfig = cfgActual;
        } else {
            builderConfig.declarativeConfig.factoryClassSet.addAll(class2Factory);
        }
//        cfgActual.factoryClassSet.addAll(class2Factory);
//        builderConfig.declarativeConfig = cfgActual;
    }

    private void generateSep() throws Exception {
        LOG.debug("generateSep");
        Generator generator = new Generator();
        builderConfig.formatSource = compilerConfig.isFormatSource();
        generator.templateSep(builderConfig);
        GenerationContext generationConfig = GenerationContext.SINGLETON;
        String fqn = generationConfig.getPackageName() + "." + generationConfig.getSepClassName();
        File file = new File(generationConfig.getPackageDirectory(), generationConfig.getSepClassName() + ".java");
        LOG.info("generated sep: " + file.getCanonicalPath());
        if (compilerConfig.isCompileSource()) {
            LOG.debug("start compiling source");
            CachedCompiler javaCompiler = GenerationContext.SINGLETON.getJavaCompiler();
            javaCompiler.loadFromJava(GenerationContext.SINGLETON.getClassLoader(), fqn, readText(file.getCanonicalPath()));
            LOG.debug("completed compiling source");
        }
        if(compilerConfig.isFormatSource()){
            LOG.debug("start formatting source");
            new Thread(() -> Generator.formatSource(file)).start();
            LOG.debug("completed formatting source");
        }
//        Class newClass = CompilerUtils.loadFromResource(fqn, file.getCanonicalPath());
    }

    public static Class loadFromResource(@NotNull String className, @NotNull String resourceName) throws IOException, ClassNotFoundException {
        return GenerationContext.SINGLETON.getJavaCompiler().loadFromJava(GenerationContext.SINGLETON.getClassLoader(), className, readText(resourceName));
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

    private static InputStream getInputStream(@NotNull String filename) throws FileNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = contextClassLoader.getResourceAsStream(filename);
        if (is != null) {
            return is;
        }
        InputStream is2 = contextClassLoader.getResourceAsStream('/' + filename);
        if (is2 != null) {
            return is2;
        }
        return new FileInputStream(filename);
    }

}
