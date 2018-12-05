/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator;

import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.api.generation.GenerationContext;
import static com.fluxtion.generator.Templates.JAVA_DEBUG_TEMPLATE;
import static com.fluxtion.generator.Templates.JAVA_INTROSPECTOR_TEMPLATE;
import static com.fluxtion.generator.Templates.JAVA_TEMPLATE;
import static com.fluxtion.generator.Templates.JAVA_TEST_DECORATOR_TEMPLATE;
import com.fluxtion.generator.exporter.PngGenerator;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
import com.fluxtion.generator.model.TopologicallySortedDependecyGraph;
import com.fluxtion.generator.targets.SepJavaSourceModelHugeFilter;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.googlejavaformat.java.Formatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.xml.transform.TransformerConfigurationException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Greg Higgins
 */
public class Generator {

    private SEPConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(Generator.class);
    private SimpleEventProcessorModel sep;

    public void templateSep(SEPConfig config) throws Exception {
        config.buildConfig();
        this.config = config;
        LOG.debug("init velocity");
        initVelocity();
        LOG.debug("start graph calc");
        GenerationContext context = GenerationContext.SINGLETON;
        //generate model
        TopologicallySortedDependecyGraph graph = new TopologicallySortedDependecyGraph(
                config.nodeList,
                config.publicNodes,
                config.declarativeConfig,
                config.nodeNameStrategy,
                context,
                config.auditorMap,
                config
        );
//        graph.registrationListenerMap = config.auditorMap;
        LOG.debug("start model gen");
        sep = new SimpleEventProcessorModel(graph, config.filterMap, config.filterDescriptionProducer, context.getProxyClassMap());
        sep.generateMetaModel(config.supportDirtyFiltering);
        //TODO add conditionality for different target languages
        //buildJava output
        LOG.debug("start template output");
        templateJavaOutput();

        LOG.debug("export graphML");
        exportGraphMl(graph);
        LOG.debug("finished generating SEP");
    }

    private static void initVelocity() throws Exception {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    private void templateJavaOutput() throws Exception {
        SepJavaSourceModelHugeFilter srcModelHuge = new SepJavaSourceModelHugeFilter(sep, config.inlineEventHandling, config.assignPrivateMembers, config.maxFiltersInline);
        SepJavaSourceModelHugeFilter srcModel = srcModelHuge;
//        SepJavaSourceModel srcModelOriginal = new SepJavaSourceModel(sep, config.inlineEventHandling);
//        SepJavaSourceModel srcModel = srcModelOriginal;
        LOG.debug("building source model");
        srcModel.buildSourceModel();
        //set up defaults
        if (config.templateFile == null) {
            config.templateFile = JAVA_TEMPLATE;
        }
        if (config.debugTemplateFile == null) {
            config.debugTemplateFile = JAVA_DEBUG_TEMPLATE;
        }
        if (config.testTemplateFile == null) {
            config.testTemplateFile = JAVA_TEST_DECORATOR_TEMPLATE;
        }
        if (config.introspectorTemplateFile == null) {
            config.introspectorTemplateFile = JAVA_INTROSPECTOR_TEMPLATE;
        }

        LOG.debug("templating output source - start");
        String templateFile = config.templateFile;
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
        ctx.put("MODEL", srcModel);
        ctx.put("MODEL_EXTENSION", config.templateContextExtension);
        ctx.put("package", GenerationContext.SINGLETON.getPackageName());
        ctx.put("className", GenerationContext.SINGLETON.getSepClassName());
        File outFile = new File(GenerationContext.SINGLETON.getPackageDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".java");
        FileWriter templateWriter = new FileWriter(outFile);
        template.merge(ctx, templateWriter);
        templateWriter.flush();
        LOG.debug("templating output source - finish");
        //TODO separate sep diagram and debugger generation - by default always generate images
        if (config.generateDebugPrep && false) {
            //debug class
            template = Velocity.getTemplate(config.debugTemplateFile);
            ctx = new VelocityContext();
            ctx.put("MODEL", srcModel);
            ctx.put("MODEL_EXTENSION", config.templateContextExtension);

            ctx.put("package", GenerationContext.SINGLETON.getPackageName());
            ctx.put("className", GenerationContext.SINGLETON.getSepClassName());
            ctx.put("debugClassName", GenerationContext.SINGLETON.getSepClassName() + "Debug");
            outFile = new File(GenerationContext.SINGLETON.getPackageDirectory(), GenerationContext.SINGLETON.getSepClassName() + "Debug.java");
            templateWriter = new FileWriter(outFile);
            template.merge(ctx, templateWriter);
            templateWriter.flush();
            //introspector
            template = Velocity.getTemplate(config.introspectorTemplateFile);
            ctx = new VelocityContext();
            ctx.put("MODEL", srcModel);
            ctx.put("MODEL_EXTENSION", config.templateContextExtension);
            ctx.put("package", GenerationContext.SINGLETON.getPackageName());
            ctx.put("className", GenerationContext.SINGLETON.getSepClassName());
            ctx.put("debugClassName", GenerationContext.SINGLETON.getSepClassName() + "Debug");
            ctx.put("introspectorClassName", GenerationContext.SINGLETON.getSepClassName() + "Introspector");
            outFile = new File(GenerationContext.SINGLETON.getPackageDirectory(), GenerationContext.SINGLETON.getSepClassName() + "Introspector.java");
            templateWriter = new FileWriter(outFile);
            template.merge(ctx, templateWriter);
            templateWriter.flush();
        }
        if (config.generateTestDecorator) {
            //test class
            template = Velocity.getTemplate(config.testTemplateFile);
            ctx = new VelocityContext();
            ctx.put("MODEL", srcModel);
            ctx.put("MODEL_EXTENSION", config.templateContextExtension);
            ctx.put("package", GenerationContext.SINGLETON.getPackageName());
            ctx.put("className", GenerationContext.SINGLETON.getSepClassName());
            ctx.put("decoratorClassName", GenerationContext.SINGLETON.getSepClassName() + "TestDecorator");
            outFile = new File(GenerationContext.SINGLETON.getPackageDirectory(), GenerationContext.SINGLETON.getSepClassName() + "TestDecorator.java");
            templateWriter = new FileWriter(outFile);
            template.merge(ctx, templateWriter);
            templateWriter.flush();
        }
        //add some formatting
        templateWriter.close();
        if (config.formatSource) {
            LOG.debug("Reading source");
            CharSource source = Files.asCharSource(outFile, Charset.defaultCharset());
            CharSink output = Files.asCharSink(outFile, Charset.defaultCharset());
            LOG.debug("formatting source - start");
            new Formatter().formatSource(source, output);
            LOG.debug("formatting source - finish");
        }
    }

    private void exportGraphMl(TopologicallySortedDependecyGraph graph) {
        if (config.generateDescription) {
            try {
                LOG.debug("generating event images and graphml");
                File graphMl = new File(GenerationContext.SINGLETON.getResourcesOutputDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".graphml");
                File pngFile = new File(GenerationContext.SINGLETON.getResourcesOutputDirectory(), GenerationContext.SINGLETON.getSepClassName() + ".png");
                if (graphMl.getParentFile() != null) {
                    graphMl.getParentFile().mkdirs();
                }
                FileWriter graphMlWriter = new FileWriter(graphMl);
                graph.exportAsGraphMl(graphMlWriter, true);
                PngGenerator.generatePNG(graphMl, pngFile);
            } catch (IOException | TransformerConfigurationException | SAXException iOException) {
                System.out.println("error writing png and graphml:" + iOException.getMessage());
            }
        }
    }

}
