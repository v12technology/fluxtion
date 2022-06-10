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

import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.builder.generation.GenerationContext;
import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeFactoryRegistration;
import com.fluxtion.compiler.generation.compiler.SepFactoryConfigBean;
import com.fluxtion.compiler.generation.exporter.PngGenerator;
import com.fluxtion.compiler.generation.graphbuilder.NodeFactoryLocator;
import com.fluxtion.compiler.generation.model.SimpleEventProcessorModel;
import com.fluxtion.compiler.generation.model.TopologicallySortedDependencyGraph;
import com.fluxtion.compiler.generation.targets.InMemoryEventProcessor;
import com.fluxtion.compiler.generation.targets.JavaSourceGenerator;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.fluxtion.compiler.generation.Templates.JAVA_TEMPLATE;

/**
 * @author Greg Higgins
 */
public class Generator {

    private SEPConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(Generator.class);
    private SimpleEventProcessorModel simpleEventProcessorModel;

    public InMemoryEventProcessor inMemoryProcessor(SEPConfig config) throws Exception {
        config.buildConfig();

        //Loading factories
        LOG.debug("locateFactories");
        SepFactoryConfigBean loadedConfig = new SepFactoryConfigBean();
        Set<Class<? extends NodeFactory<?>>> class2Factory = NodeFactoryLocator.nodeFactorySet();
        NodeFactoryRegistration cfgActual = loadedConfig.asDeclarativeNodeConfiguration();
        cfgActual.factoryClassSet.addAll(class2Factory);
        config.setDeclarativeConfig(cfgActual);
        config.getDeclarativeConfig().factoryClassSet.addAll(class2Factory);
        //Loading factories

        this.config = config;
        if (GenerationContext.SINGLETON == null) {
            GenerationContext.setupStaticContext("", "", null, null);
        }
        if (GenerationContext.SINGLETON == null) {
            throw new RuntimeException("could not initialise Generations.SINGLETON context");
        }
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(
                config.getNodeList(),
                config.getPublicNodes(),
                config.getDeclarativeConfig(),
                GenerationContext.SINGLETON,
                config.getAuditorMap(),
                config
        );
        simpleEventProcessorModel = new SimpleEventProcessorModel(graph, config.getFilterMap(), GenerationContext.SINGLETON.getProxyClassMap());
        simpleEventProcessorModel.generateMetaModel(config.isSupportDirtyFiltering());
        if (config.isGenerateDescription() || GenerationContext.SINGLETON.getPackageName().isEmpty()) {
            exportGraphMl(graph);
        }
        return new InMemoryEventProcessor(simpleEventProcessorModel);
    }

    public void templateSep(SEPConfig config) throws Exception {
        ExecutorService execSvc = Executors.newCachedThreadPool();
//        execSvc.submit(Generator::warmupCompiler);
        config.buildConfig();
        this.config = config;
        LOG.debug("init velocity");
        initVelocity();
        LOG.debug("start graph calc");
        GenerationContext context = GenerationContext.SINGLETON;
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(
                config.getNodeList(),
                config.getPublicNodes(),
                config.getDeclarativeConfig(),
                context,
                config.getAuditorMap(),
                config
        );
//        graph.registrationListenerMap = config.auditorMap;
        LOG.debug("start model gen");
        simpleEventProcessorModel = new SimpleEventProcessorModel(graph, config.getFilterMap(), context.getProxyClassMap());
        simpleEventProcessorModel.generateMetaModel(config.isSupportDirtyFiltering());
        //TODO add conditionality for different target languages
        //buildJava output
        execSvc.submit(() -> {
            LOG.debug("start exporting graphML/images");
            exportGraphMl(graph);
            LOG.debug("completed exporting graphML/images");
            LOG.debug("finished generating SEP");
        });
        LOG.debug("start template output");
        templateJavaOutput();
        LOG.debug("completed template output");
        execSvc.shutdown();
    }

    public SimpleEventProcessorModel getSimpleEventProcessorModel() {
        return simpleEventProcessorModel;
    }


    private static void initVelocity() {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(GenerationContext.SINGLETON.getClassLoader());
        Velocity.init();
        Thread.currentThread().setContextClassLoader(originalClassLoader);
    }

    private File templateJavaOutput() throws Exception {
        JavaSourceGenerator srcModel = new JavaSourceGenerator(
                simpleEventProcessorModel,
                config.isInlineEventHandling(),
                config.isAssignPrivateMembers()
        );
        srcModel.additionalInterfacesToImplement(config.interfacesToImplement());
        LOG.debug("building source model");
        srcModel.buildSourceModel();
        //set up defaults
        if (config.getTemplateFile() == null) {
            config.setTemplateFile(JAVA_TEMPLATE);
        }

        LOG.debug("templating output source - start");
        String templateFile = config.getTemplateFile();
        Template template;//= Velocity.getTemplate(config.templateFile);

        try {
            template = Velocity.getTemplate(templateFile);
        } catch (Exception e) {
            System.out.println("failed to load template, setting threadcontext class loader");
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(GenerationContext.SINGLETON.getClassLoader());
                template = Velocity.getTemplate(templateFile);
            } finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }

        Context ctx = new VelocityContext();
        addVersionInformation(ctx);
        ctx.put("MODEL", srcModel);
        ctx.put("package", GenerationContext.SINGLETON.getPackageName());
        ctx.put("className", GenerationContext.SINGLETON.getSepClassName());
        File outFile = new File(GenerationContext.SINGLETON.getPackageDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".java");
        FileWriter templateWriter = new FileWriter(outFile);
        template.merge(ctx, templateWriter);
        templateWriter.flush();
        LOG.debug("templating output source - finish");
        //add some formatting
        templateWriter.close();
        return outFile;
    }

    private void addVersionInformation(Context ctx) {
        ctx.put("generator_version_information", this.getClass().getPackage().getImplementationVersion());
        ctx.put("api_version_information", OnEventHandler.class.getPackage().getImplementationVersion());
        ctx.put("build_time", LocalDateTime.now());
    }

    public static void formatSource(File outFile) {
        try {
            LOG.debug("Reading source:'{}'", outFile.getCanonicalPath());
            CharSource source = Files.asCharSource(outFile, Charset.defaultCharset());
            CharSink output = Files.asCharSink(outFile, Charset.defaultCharset());
            LOG.debug("formatting source - start");
            new Formatter().formatSource(source, output);
            LOG.debug("formatting source - finish");
        } catch (FormatterException | IOException ex) {
            LOG.error("problem formatting source file", ex);
        }
    }

    private void exportGraphMl(TopologicallySortedDependencyGraph graph) {
        if (config.isGenerateDescription()) {
            try {
                LOG.debug("generating event images and graphml");
                File graphMl = new File(GenerationContext.SINGLETON.getResourcesOutputDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".graphml");
                File pngFile = new File(GenerationContext.SINGLETON.getResourcesOutputDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".png");
                if (graphMl.getParentFile() != null) {
                    graphMl.getParentFile().mkdirs();
                }
                try (FileWriter graphMlWriter = new FileWriter(graphMl)) {
                    graph.exportAsGraphMl(graphMlWriter, true);
                }
                PngGenerator.generatePNG(graphMl, pngFile);
            } catch (IOException | TransformerConfigurationException | SAXException iOException) {
                LOG.error("error writing png and graphml:", iOException);
            }
        }
    }

}
