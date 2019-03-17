/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.builder.util;

import static com.fluxtion.generator.util.TemplatingCompiler.TemplateKeys.packageName;
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
 *
 * @author V12 Technology Ltd.
 */
public class StreamFunctionGenerator {

    private String templateFile;
    private String packageName = "com.fluxtion.ext.declarative.builder.stream";
    private String className = "MyFunctions";
    private static final String SRC_DIR = "src/main/java";

    private void generate() throws IOException {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
        Template template = Velocity.getTemplate(templateFile);
        Context ctx = new VelocityContext();
//        ctx.put("package", packageName);
//        ctx.put("className", className);
//        ctx.put("functionName", functionName);
//        ctx.put("functionClass", functionClass);
//        ctx.put("functionClassFqn", functionClassFqn);
//        ctx.put("isArrayFunction", isArrayFunction);
//        ctx.put("isStatefulFunction", isStatefulFunction);
        File srcPackageDirectory = new File(SRC_DIR, packageName.replace(".", "/"));
        srcPackageDirectory.mkdirs();
        File outFile = new File(srcPackageDirectory, className + ".java");
        FileWriter templateWriter = new FileWriter(outFile);
        template.merge(ctx, templateWriter);
        templateWriter.flush();
    }



    public static class FunctionInfo {

        String methodName;
        String invoke;
        String type;

        public FunctionInfo(String methodName, String invoke, String type) {
            this.methodName = methodName;
            this.invoke = invoke;
            this.type = type;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getInvoke() {
            return invoke;
        }

        public void setInvoke(String invoke) {
            this.invoke = invoke;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}
