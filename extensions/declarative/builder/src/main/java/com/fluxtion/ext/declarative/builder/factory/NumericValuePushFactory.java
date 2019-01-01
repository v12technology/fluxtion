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
package com.fluxtion.ext.declarative.builder.factory;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.EventWrapper;
import static com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper.methodFromLambda;
import static com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper.numericSetMethod;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetMethod;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import java.io.IOException;
import java.lang.reflect.Method;
import org.apache.velocity.VelocityContext;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceMethod;
import com.fluxtion.ext.declarative.api.numeric.NumericValuePush;
import com.fluxtion.ext.declarative.builder.Templates;
import static com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper.setCharMethod;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.imports;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.parameterClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetClassFqn;
import com.fluxtion.ext.declarative.builder.util.ImportMap;
import java.util.function.BiConsumer;
import net.vidageek.mirror.dsl.Mirror;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * Factory for pushing primitives from source to target node.
 * 
 * @author Greg Higgins
 */
public class NumericValuePushFactory {

    
    private static final String TEMPLATE = Templates.PACKAGE + "/NumericFieldPushTemplate.vsl";
    
    public static <T> NumericValuePush<T> setChar(NumericValue sourceValue, 
            T targetInstance, 
            BiConsumer<T, ? super Character> targetFunction) throws Exception {
        Method sourceMethod = methodFromLambda(sourceValue, NumericValue::charValue);
        Method targetSetMethod = setCharMethod(targetInstance,  targetFunction);
        return generatePush(sourceValue, sourceMethod, targetInstance, targetSetMethod);
    }

    public static <T> NumericValuePush<T> setNumeric(NumericValue sourceValue, 
            T targetInstance, 
            BiConsumer<T, ? super Byte> targetFunction) throws Exception {
        
        Method targetSetMethod = numericSetMethod(targetInstance,  targetFunction);
        String s = targetSetMethod.getParameterTypes()[0].getName() + "Value";
        Mirror m = new Mirror();
        Method sourceMethod = m.on(NumericValue.class).reflect().method(s).withAnyArgs();
        return generatePush(sourceValue, sourceMethod, targetInstance, targetSetMethod);
    }
 
    public static <T> NumericValuePush<T> generatePush(NumericValue sourceValue, 
            Method sourceGetMethod,
            T targetInstance, 
            Method targetSetMethod) throws MethodInvocationException, ClassNotFoundException, IOException, InstantiationException, ParseErrorException, ResourceNotFoundException, IllegalAccessException {
        //build aggregate target instance
        VelocityContext ctx = new VelocityContext();
        ImportMap importMap = ImportMap.newMap(OnEvent.class, NumericValuePush.class, targetInstance.getClass());
        ctx.put(imports.name(), importMap.asString());
        String genClassName = "Push_" + targetInstance.getClass().getSimpleName() + "_" + targetSetMethod.getName() + "_" + GenerationContext.nextId();
        ctx.put(functionClass.name(), genClassName);
        ctx.put(targetMethod.name(), targetSetMethod.getName());
        ctx.put(targetClass.name(), targetInstance.getClass().getSimpleName());
        ctx.put(parameterClass.name(), targetSetMethod.getParameterTypes()[0].getName());
        ctx.put(sourceMethod.name(), sourceGetMethod.getName());
        //        
        Class<NumericValuePush> pushClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
        NumericValuePush pusher = pushClass.newInstance();
        pusher.source = sourceValue;
        pusher.target = targetInstance;
        //
        GenerationContext.SINGLETON.getNodeList().add(pusher);
        return pusher;
    }

}
