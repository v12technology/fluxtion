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
package com.fluxtion.ext.streaming.builder.util;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Average;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Count;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Max;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Min;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.PercentDelta;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Sum;
import com.fluxtion.ext.streaming.builder.event.EventSelect;
import com.fluxtion.ext.streaming.builder.stream.FilterBuilder;
import com.fluxtion.ext.streaming.builder.stream.FunctionBuilder;
import com.fluxtion.ext.streaming.builder.stream.StreamBuilder;
import com.fluxtion.generator.targets.JavaGenHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
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

    private String templateFile = "/template/FunctionsTemplate.vsl";
    private String packageName = "com.fluxtion.ext.streaming.builder.stream";
    private String className = "StreamFunctionsBuilder";
    private final ImportMap imports = ImportMap.newMap();
    private static final String SRC_DIR = "src/main/java";
    private List<FunctionInfo> functionList = new ArrayList<>();
    private List<FunctionInfo> biFunctionList = new ArrayList<>();
    private List<FunctionInfo> consumerFunctionList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        StreamFunctionGenerator gen = new StreamFunctionGenerator();
        //bifunctions
        gen.addBinaryFunction(StreamFunctions::add, "add");
        gen.addBinaryFunction(StreamFunctions::subtract, "subtract");
        gen.addBinaryFunction(StreamFunctions::multiply, "multiply");
        gen.addBinaryFunction(StreamFunctions::divide, "divide");
        //unary functions
        gen.addUnaryFunction(new Sum()::addValue, "cumSum");
        gen.addUnaryFunction(new Average()::addValue, "avg");
        gen.addUnaryFunction(new Max()::max, "max");
        gen.addUnaryFunction(new Min()::min, "min");
        gen.addUnaryFunction(new PercentDelta()::value, "percentChange");
        gen.addUnaryFunction(Math::ceil, "ceil");
        gen.addUnaryFunction(Math::floor, "floor");
        //consumer
        gen.addConsumerFunction(new Count()::increment, "count");
//        gen.addConsumerFunction(new IntCount()::increment, "intCount");
        gen.generate();
    }

    public <T, S, R> void addBinaryFunction(SerializableBiFunction<T, S, R> func, String name) {
        biFunctionList.add(new FunctionInfo(func, name));
    }

    public <T, R> void addUnaryFunction(SerializableFunction<T, R> func, String name) {
        functionList.add(new FunctionInfo(func, name));
    }

    public <T, R> void addConsumerFunction(SerializableFunction<T, R> func, String name) {
        consumerFunctionList.add(new FunctionInfo(func, name));
    }

    public void generate() throws IOException {
        //velocity setup
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
        Template template = Velocity.getTemplate(templateFile);
        Context ctx = new VelocityContext();
        //standard imports 
        imports.addImport(Event.class);
        imports.addImport(SerializableFunction.class);
        imports.addImport(SerializableBiFunction.class);
        imports.addImport(SerializableSupplier.class);
        imports.addImport(FilterBuilder.class);
        imports.addImport(Wrapper.class);
        imports.addStaticImport(EventSelect.class);
        imports.addStaticImport(FunctionBuilder.class);
        imports.addImport(FunctionArg.class);
        imports.addStaticImport(FunctionArg.class);
        imports.addStaticImport(StreamBuilder.class);
        //setup context
        ctx.put("imports", imports.asString());
        ctx.put("functions", functionList);
        ctx.put("bifunctions", biFunctionList);
        ctx.put("consumers", consumerFunctionList);
        ctx.put("package", packageName);
        ctx.put("className", className);
        //generate
        File srcPackageDirectory = new File(SRC_DIR, packageName.replace(".", "/"));
        srcPackageDirectory.mkdirs();
        File outFile = new File(srcPackageDirectory, className + ".java");
        FileWriter templateWriter = new FileWriter(outFile);
        template.merge(ctx, templateWriter);
        templateWriter.flush();
    }

    public class FunctionInfo {

        String methodName;
        String invoke;
        String returnType;
        List<String[]> argsList = new ArrayList<>();
        String functionName;

        public FunctionInfo(SerializableBiFunction func, String name) {
            final Method m = func.method();
            this.functionName = name;
            String clazz = imports.addImport(func.getContainingClass());
            methodName = clazz + "#" + m.getName();
            if (Modifier.isStatic(m.getModifiers())) {
                invoke = clazz + "::" + m.getName();
            } else {
                invoke = "new " + clazz + "()::" + m.getName();
            }
            Class<?> returnClass = m.getReturnType();
            if(returnClass.isPrimitive()){
                returnClass = Number.class;
            }
            returnType = imports.addImport(returnClass);            
            for (Class<?> parameterType : m.getParameterTypes()) {
                argsList.add(inTypes(parameterType));
            }
        }
        
        public FunctionInfo(SerializableFunction func, String name) {
            final Method m = func.method();
            this.functionName = name;
            String clazz = imports.addImport(func.getContainingClass());
            methodName = clazz + "#" + m.getName();
            if (Modifier.isStatic(m.getModifiers())) {
                invoke = clazz + "::" + m.getName();
            } else {
                invoke = "new " + clazz + "()::" + m.getName();
            }
            Class<?> returnClass = m.getReturnType();
            if(returnClass.isPrimitive()){
                returnClass = Number.class;
            }
            returnType = imports.addImport(returnClass);            
            for (Class<?> parameterType : m.getParameterTypes()) {
                argsList.add(inTypes(parameterType));
            }
        }
        
        private String[] inTypes(Class inType){
            String inputType;
            String inputTypeReal;
            if (inType == Object.class) {
                inputType = "T";
                inputTypeReal = inputType;
            } else if (inType.isPrimitive()) {
                inputTypeReal = imports.addImport(JavaGenHelper.mapPrimitiveToWrapper(inType));
                inputType = imports.addImport(Number.class);
            } else {
                inputType = imports.addImport(inType);
                inputTypeReal = inputType;
            }
            return new String[]{inputType, inputTypeReal};
        }
        

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public String getInvoke() {
            return invoke;
        }

        public void setInvoke(String invoke) {
            this.invoke = invoke;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public String getInputType() {
            return argsList.get(0)[0];
        }

        public String getInputTypeReal() {
            return argsList.get(0)[1];
        }
        
        public String getInput1Type() {
            return argsList.get(1)[0];
        }

        public String getInput1TypeReal() {
            return argsList.get(1)[1];
        }
        
    }
}
