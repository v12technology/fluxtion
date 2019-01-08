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
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.futext.builder.util;

import com.fluxtion.ext.declarative.api.numeric.NumericArrayFunctionStateless;
import com.fluxtion.ext.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.ext.futext.api.filter.BinaryPredicates;
import com.fluxtion.ext.futext.api.math.BinaryFunctions;
import com.fluxtion.ext.futext.api.math.UnaryFunctions;
import static com.fluxtion.ext.futext.builder.Templates.BINARY_FILTER_TEMPLATE;
import static com.fluxtion.ext.futext.builder.Templates.BINARY_TEMPLATE;
import static com.fluxtion.ext.futext.builder.Templates.UNARY_TEMPLATE;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Utility to generate helper function accessors for a UnaryNumeric function.
 *
 * @author greg
 */
public class FactoryFunctionGenerator {

    private static final String SRC_DIR = "src/main/java";
    private static final String GENERATED_DIR = "target/generated-sources/fluxtion";

    public String templateFile;
    public String packageName;
    public String className;
    public String classNameSuffix;
    public String functionName;
    public String functionClass;
    public String functionClassFqn;
    public String outputDirectory;
    private File srcPackageDirectory;

    public static void main(String[] args) throws IOException, Exception {
        FactoryFunctionGenerator helper = new FactoryFunctionGenerator();
        helper.outputDirectory = SRC_DIR;
        helper.outputDirectory = GENERATED_DIR;
        helper.packageName = "com.fluxtion.extension.functional.math";
        helper.generateUnaryFunctions(
                UnaryFunctions.Max.class,
                UnaryFunctions.Min.class,
                UnaryFunctions.Abs.class,
                UnaryFunctions.CumSum.class,
                UnaryFunctions.Avg.class
        );

        helper.packageName = "com.fluxtion.extension.functional.math";
        helper.generateBinaryFunctions(
                BinaryFunctions.Add.class,
                BinaryFunctions.Subtract.class,
                BinaryFunctions.Multiply.class,
                BinaryFunctions.Divide.class,
                BinaryFunctions.Modulo.class
        );
        
        helper.packageName = "com.fluxtion.extension.functional.test";
        helper.generateBinaryFilters(
                BinaryPredicates.GreaterThan.class,
                BinaryPredicates.GreaterThanOrEqual.class,
                BinaryPredicates.LessThan.class,
                BinaryPredicates.LessThanOrEqual.class,
                BinaryPredicates.EqualTo.class,
                BinaryPredicates.NotEqualTo.class
        );
    }
    private boolean isArrayFunction;
    private boolean isStatefulFunction;

    public void generateUnaryFunctions(Class... clazz) throws IOException, Exception {
//        packageName = "com.fluxtion.extension.functional.math1";
        classNameSuffix = "Functions";
        generateFunction(UNARY_TEMPLATE, clazz);
    }

    public void generateBinaryFunctions(Class... clazz) throws IOException, Exception {
//        packageName = "com.fluxtion.extension.functional.math1";
        classNameSuffix = "Functions";
        generateFunction(BINARY_TEMPLATE, clazz);
    }

    public void generateBinaryFilters(Class... clazz) throws IOException, Exception {
//        packageName = "com.fluxtion.extension.functional.test";
        classNameSuffix = "Helper";
        generateFunction(BINARY_FILTER_TEMPLATE, clazz);
    }

    public void generateFunction(String template, Class... clazz) throws IOException, Exception {
        for (Class classToGen : clazz) {
            setup(classToGen, template);
            initVelocity();
            generate();
        }
    }

    private void setup(Class clazz, String template) {
        templateFile = template;
        functionClass = clazz.getSimpleName();
        functionClassFqn = clazz.getCanonicalName();
        isArrayFunction = NumericArrayFunctionStateless.class.isAssignableFrom(clazz);
        isStatefulFunction = NumericFunctionStateful.class.isAssignableFrom(clazz);
        className = functionClass + classNameSuffix;//"Functions";
        functionName = functionClass.substring(0, 1).toLowerCase() + functionClass.substring(1);
        srcPackageDirectory = new File(outputDirectory, packageName.replace(".", "/"));
        srcPackageDirectory.mkdirs();
    }

    private void generate() throws IOException {
        Template template = Velocity.getTemplate(templateFile);
        Context ctx = new VelocityContext();
        ctx.put("package", packageName);
        ctx.put("className", className);
        ctx.put("functionName", functionName);
        ctx.put("functionClass", functionClass);
        ctx.put("functionClassFqn", functionClassFqn);
        ctx.put("isArrayFunction", isArrayFunction);
        ctx.put("isStatefulFunction", isStatefulFunction);
        File outFile = new File(srcPackageDirectory, className + ".java");
        FileWriter templateWriter = new FileWriter(outFile);
        template.merge(ctx, templateWriter);
        templateWriter.flush();
    }

    private static void initVelocity() throws Exception {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

}
