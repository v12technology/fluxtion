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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.nodegen;

import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.NodeRegistry;
import com.fluxtion.builder.generation.GenerationContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

/**
 * Creates instances of the averaging node and generates specific classes that
 * are used in the actual SEP
 *
 * @author Greg Higgins
 */
public class AveragingNodeFactory implements NodeFactory<AveragingNode> {

    private static final String NAME_SPACE = "AveragingNode";
    //Keys lookups for config values from the map provided in the NodeFactory.createNode()
    public static final String DATA_SOURCE = NAME_SPACE + ".dataSource";
    public static final String DATA_SOURCE_FIELD = NAME_SPACE + ".dataSourceField";
    public static final String WINDOW_SIZE = NAME_SPACE + ".windowSize";
    public static final String OUTPUT_AVERAGE = NAME_SPACE + ".average";

    private final String TEMPLATE_JAVA = "nodegen/javaAveragingNodeTemplate.vsl";
    private GenerationContext generationConfig;
    private static int count;

    @Override
    public AveragingNode createNode(Map config, NodeRegistry registry) {
        try {
            //creates instance
            AveragingNode node = new AveragingNode();
            node.dataSource = config.get(AveragingNodeFactory.DATA_SOURCE);
            String dataSourceClassName = node.dataSource.getClass().getCanonicalName();
            String dataSourceField = (String) config.get(AveragingNodeFactory.DATA_SOURCE_FIELD);
            int windowSize = ConfigHelper.intFromMap(config, AveragingNodeFactory.WINDOW_SIZE, 10);
            final String generatedClassName = "AveragingNode_" + count++;
            //generates the actual class
            String outputPackage = generationConfig.getPackageName();
            File outputDirectory = generationConfig.getPackageDirectory();
            Template template = Velocity.getTemplate(TEMPLATE_JAVA);
            Context ctx = new VelocityContext();
            ctx.put("windowSize", windowSize);
            ctx.put("dataSourceClass", dataSourceClassName);
            ctx.put("dataSourceField", dataSourceField);
            ctx.put("package", outputPackage);
            ctx.put("className", generatedClassName);
            File outFile = new File(outputDirectory, generatedClassName + ".java");
            FileWriter templateWriter = new FileWriter(outFile);
            template.merge(ctx, templateWriter);
            templateWriter.flush();

            generationConfig.getProxyClassMap().put(node, generatedClassName);
            return node;
        } catch (IOException ex) {
            throw new RuntimeException("unable to create proxied class for AveragingNode", ex);
        }
    }

    @Override
    public void preSepGeneration(GenerationContext context) {
        this.generationConfig = context;
    }

}
