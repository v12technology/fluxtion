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
package com.fluxtion.ext.declarative.builder.window;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.window.CountSlidingBuffer;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.imports;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.input;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.outputClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceClassFqn;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceMethod;
import com.fluxtion.ext.declarative.builder.util.ImportMap;
import com.fluxtion.ext.declarative.builder.util.SourceInfo;
import java.lang.reflect.Method;
import java.util.Objects;
import org.apache.velocity.VelocityContext;

/**
 *
 * @author Greg Higgins
 */
public class CountSlidingBufferFactory {

    private static final String TEMPLATE = "template/SlidingCountTemplate.vsl";

    public static CountSlidingBuffer build(Object source, Method getter, int windowSize, int publishFrequency, String dataAccessCall, SourceInfo slidingSrcInfo) {
        ImportMap importMap = ImportMap.newMap(
                Initialise.class, OnEvent.class,
                OnEventComplete.class, OnParentUpdate.class, 
                CountSlidingBuffer.class, AfterEvent.class,
                source.getClass(), Objects.class);

        try {
            VelocityContext ctx = new VelocityContext();

            String oldId = slidingSrcInfo.id;
            dataAccessCall = dataAccessCall.replaceAll(oldId, "input");
            slidingSrcInfo = new SourceInfo(slidingSrcInfo.type, "input");
            String genClassName = source.getClass().getSimpleName() + "SlidingWin_" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(outputClass.name(), getter.getReturnType().getName());
            ctx.put(sourceClass.name(), source.getClass().getSimpleName());
            ctx.put(input.name(), slidingSrcInfo.id);
            ctx.put(sourceClassFqn.name(), source.getClass().getCanonicalName());
            ctx.put(sourceMethod.name(), dataAccessCall);
            ctx.put(imports.name(), importMap.asString());

            Class<CountSlidingBuffer> aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
            CountSlidingBuffer result = aggClass.newInstance();
            result.bufferSize = windowSize;
            result.windowSpacing = publishFrequency;
            aggClass.getField(slidingSrcInfo.id).set(result, source);
            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld buffer " + e.getMessage(), e);
        }
    }

}
