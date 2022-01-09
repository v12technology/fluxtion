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

import com.fluxtion.compiler.builder.generation.GenerationContext;
import com.fluxtion.compiler.builder.node.NodeFactoryRegistration;
import com.fluxtion.compiler.builder.node.NodeFactory;
import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.generation.Generator;
import com.fluxtion.compiler.generation.compiler.classcompiler.StringCompilation;
import com.fluxtion.compiler.generation.graphbuilder.NodeFactoryLocator;
import net.openhft.compiler.CachedCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    public static void main(String[] args) throws Exception {
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
    public Class<?> compile() throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
        return compile(SepCompilerConfig.initFromSystemProperties());
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
    public Class<?> compile(SepCompilerConfig compilerConfig) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
        return compile(compilerConfig, null);
    }

    public Class<?> compile(SepCompilerConfig compilerConfig, SEPConfig configOverride) throws Exception {
        LOG.debug("starting SEP compiler");
        this.compilerConfig = compilerConfig;
        initialiseGenerator(configOverride);
        locateFactories();
        processYamlConfig();
        processRootFactoryConfig();
        Class<?> returnClass = generateSep();
        LOG.debug("finished SEP compiler");
        return returnClass;
    }

    private void initialiseGenerator(SEPConfig configOverride) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        LOG.debug("initialiseGenerator");
        LOG.debug(compilerConfig.toString());
        File buildDir = compilerConfig.getBuildOutputdirectory() == null ? null : new File(compilerConfig.getBuildOutputdirectory());
        GenerationContext.setupStaticContext(compilerConfig.getClassLoader(), compilerConfig.getPackageName(),
                compilerConfig.getClassName(),
                new File(compilerConfig.getOutputDirectory()),
                new File(compilerConfig.getResourcesOutputDirectory()),
                compilerConfig.isGenerateDescription(),
                buildDir,
                true,
                compilerConfig.getCachedCompiler());
        //compiler
        if (configOverride == null) {
            Class<?> rootClazz;
            try{
                rootClazz = compilerConfig.getClassLoader().loadClass(compilerConfig.getConfigClass());
            }catch(Exception e){
                LOG.info("loading class from cached compiler");
                rootClazz = compilerConfig.getCachedCompiler().forName(compilerConfig.getConfigClass(), compilerConfig.getClassLoader());
            }
            builderConfig = (SEPConfig) rootClazz.getDeclaredConstructor().newInstance();
        } else {
            builderConfig = configOverride;
        }
        builderConfig.setTemplateFile(compilerConfig.getTemplateSep());
        builderConfig.setSupportDirtyFiltering(compilerConfig.isSupportDirtyFiltering());
        //TODO add configuration back in when split png and debug generation
        builderConfig.setGenerateDescription(compilerConfig.isGenerateDescription());
        builderConfig.setAssignPrivateMembers(compilerConfig.isAssignNonPublicMembers());
    }

    private void processYamlConfig() throws Exception {
        LOG.debug("starting :: processYamlConfig - cfg{}", compilerConfig.getYamlFactoryConfig());
        if (compilerConfig.getYamlFactoryConfig() != null && !compilerConfig.getYamlFactoryConfig().isEmpty()) {
            File yamlFactoryConfig = new File(compilerConfig.getYamlFactoryConfig());
            LOG.debug("processing yaml factory config file:" + yamlFactoryConfig.getCanonicalPath());
            InputStream input = new FileInputStream(yamlFactoryConfig);
            Yaml beanLoader = new Yaml();
            LOG.debug("loading SepFactoryConfigBean with beanLoader");
            SepFactoryConfigBean loadedConfig = beanLoader.loadAs(input, SepFactoryConfigBean.class);
            LOG.debug("DeclarativeNodeConiguration load");
            NodeFactoryRegistration cfgActual = loadedConfig.asDeclarativeNodeConfiguration();
            LOG.debug("searching for NodeFactory's");
            Set<Class<? extends NodeFactory<?>>> class2Factory = NodeFactoryLocator.nodeFactorySet();
            cfgActual.factoryClassSet.addAll(class2Factory);
            builderConfig.setDeclarativeConfig(cfgActual);
            LOG.debug("completed :: processYamlConfig ");
        } else {
            LOG.debug("no yaml factory config file specified");
        }
    }

    private void processRootFactoryConfig() throws Exception {
        LOG.debug("processRootFactoryConfig");
        if (compilerConfig.getRootFactoryClass() != null && !compilerConfig.getRootFactoryClass().isEmpty()) {
            if (builderConfig.getDeclarativeConfig() == null) {
                SepFactoryConfigBean loadedConfig = new SepFactoryConfigBean();
                NodeFactoryRegistration cfgActual = loadedConfig.asDeclarativeNodeConfiguration();
                Set<Class<? extends NodeFactory<?>>> class2Factory = NodeFactoryLocator.nodeFactorySet();
                cfgActual.factoryClassSet.addAll(class2Factory);
                builderConfig.setDeclarativeConfig(cfgActual);
            }
        }
    }

    private void locateFactories() throws Exception {
        LOG.debug("locateFactories");
        SepFactoryConfigBean loadedConfig = new SepFactoryConfigBean();
        Set<Class<? extends NodeFactory<?>>> class2Factory = NodeFactoryLocator.nodeFactorySet();
        NodeFactoryRegistration cfgActual = loadedConfig.asDeclarativeNodeConfiguration();
        if (builderConfig == null || builderConfig.getDeclarativeConfig() ==null) {
            cfgActual.factoryClassSet.addAll(class2Factory);
            builderConfig.setDeclarativeConfig(cfgActual);
        } else {
            builderConfig.getDeclarativeConfig().factoryClassSet.addAll(class2Factory);
        }
    }

    private Class<?> generateSep() throws Exception {
        LOG.debug("generateSep");
        Class<?> returnClass = null;
        Generator generator = new Generator();
        builderConfig.setFormatSource(compilerConfig.isFormatSource());
        generator.templateSep(builderConfig);
        GenerationContext generationConfig = GenerationContext.SINGLETON;
        String fqn = generationConfig.getPackageName() + "." + generationConfig.getSepClassName();
        File file = new File(generationConfig.getPackageDirectory(), generationConfig.getSepClassName() + ".java");
        LOG.info("generated sep: " + file.getCanonicalPath());
        if(compilerConfig.isFormatSource()){
            LOG.debug("start formatting source");
            Generator.formatSource(file);
//            new Thread(() -> Generator.formatSource(file)).start();
            LOG.debug("completed formatting source");
        }
        if (compilerConfig.isCompileSource()) {
            LOG.debug("start compiling source");

            returnClass = StringCompilation.compile(fqn, readText(file.getCanonicalPath()));

//            CachedCompiler javaCompiler = GenerationContext.SINGLETON.getJavaCompiler();
//            returnClass = javaCompiler.loadFromJava(GenerationContext.SINGLETON.getClassLoader(), fqn, readText(file.getCanonicalPath()));
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
