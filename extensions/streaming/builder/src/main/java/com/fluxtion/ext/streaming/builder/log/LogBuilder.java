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
package com.fluxtion.ext.streaming.builder.log;

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.builder.generation.GenerationContext;
import static com.fluxtion.builder.generation.GenerationContext.SINGLETON;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.log.AsciiConsoleLogger;
import com.fluxtion.ext.streaming.api.log.MsgBuilder;
import com.fluxtion.ext.streaming.builder.Templates;
import com.fluxtion.ext.streaming.builder.factory.EventSelect;
import com.fluxtion.ext.streaming.builder.util.FunctionGeneratorHelper;
import static com.fluxtion.ext.streaming.builder.util.FunctionKeys.functionClass;
import static com.fluxtion.ext.streaming.builder.util.FunctionKeys.imports;
import static com.fluxtion.ext.streaming.builder.util.FunctionKeys.sourceMappingList;
import static com.fluxtion.ext.streaming.builder.util.FunctionKeys.updateNotifier;
import com.fluxtion.ext.streaming.builder.util.ImportMap;
import com.fluxtion.ext.streaming.builder.util.SourceInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Value;
import org.apache.velocity.VelocityContext;

/**
 * Builder for a simple console logger. Static helper methods create a LogBuilder
 * or a MsgBuuilder. The MsgBuilder
 *
 * @author greg
 */
public class LogBuilder {

    private final HashMap<Object, SourceInfo> inst2SourceInfo = new HashMap<>();
    private ArrayList<ValueAccessor> valuesList = new ArrayList();
    private static final String TEMPLATE = Templates.PACKAGE + "/ConsoleLoggerTemplate.vsl";
    private String message;
    private String[] messageParts;
    private int count = 0;
    private Object logNotifier;
    private final ImportMap importMap = ImportMap.newMap(MsgBuilder.class, OnEvent.class,
            NoEventReference.class, OnParentUpdate.class);
    private static final AsciiConsoleLogger MAIN_LOGGER = new AsciiConsoleLogger();

    private LogBuilder(String message, Object notifier) {
        this.message = message;
        this.messageParts = message.split("\\{\\}");
        this.logNotifier = notifier;
    }

    public static LogBuilder buildLog(String message, Object notifier) {
        LogBuilder logger = new LogBuilder(message, notifier);
        return logger;
    }

    @SafeVarargs
    public static <T> LogBuilder buildLog(String message, T source, SerializableFunction<T, ?>... data) {
        LogBuilder logger = new LogBuilder(message, null);
        logger.input(source, data);
        return logger;
    }

    @SafeVarargs
    public static <N, S, V> MsgBuilder LogOnNotify(String message, N notifier, S source, SerializableSupplier<V>... data) {
        LogBuilder logger = new LogBuilder(message, notifier);
        logger.input(source, data);
        return logger.build();
    }

    @SafeVarargs
    public static <S, V> MsgBuilder Log(String message, S source, SerializableSupplier<V>... data) {
        LogBuilder logger = new LogBuilder(message, null);
        logger.input(source, data);
        return logger.build();
    }

    public <S, V> LogBuilder input(S source, SerializableSupplier<V>... data) {
        SourceInfo sourceInfo = addSource(source);
        for (SerializableSupplier<V> function : data) {
            Method getMethod = function.method(SINGLETON.getClassLoader());
            valuesList.add(new ValueAccessor(messageParts[count], sourceInfo, getMethod));
            count++;
        }
        return this;
    }

    public static <E> MsgBuilder Log(String message, Class<E> source) {
        return Log(message, EventSelect.select(source));
    }
    
    public static <E> MsgBuilder Log(String message, Class<E> source, SerializableFunction<E, ?>... data) {
        return Log(message, EventSelect.select(source), data);
    }
    
    @SafeVarargs
    public static <S> MsgBuilder Log(String message, S source, SerializableFunction<S, ?>... data) {
        LogBuilder logger = new LogBuilder(message, null);
        logger.input(source, data);
        return logger.build();
    }

    @SafeVarargs
    public static <E, W extends Wrapper<E>> MsgBuilder Log(String message, W source, SerializableFunction<E, ?>... data) {
        LogBuilder logger = new LogBuilder(message, null);
        logger.input(source, data);
        return logger.build();
    }
    
    public static <E> MsgBuilder Log(Class<E> source) {
        return Log(EventSelect.select(source));
    }
    
    public static <W> MsgBuilder Log(W source) {
        LogBuilder logger = new LogBuilder("", null);
        logger.input(source, W::toString);
        return logger.build();
    }
    
    public static <E, W extends Wrapper<E>> MsgBuilder Log(W source) {
        LogBuilder logger = new LogBuilder("", null);
        logger.input(source, E::toString);
        return logger.build();
    }

    public static <E, W extends Wrapper<E>> MsgBuilder Log(String message, W source) {
        LogBuilder logger = new LogBuilder(message, null);
        logger.input(source, E::toString);
        return logger.build();
    }

    @SafeVarargs
    public static <S, N> MsgBuilder LogOnNotify(String message, N notifier, S source, SerializableFunction<S, ?>... data) {
        LogBuilder logger = new LogBuilder(message, notifier);
        logger.input(source, data);
        return logger.build();
    }

    @SafeVarargs
    public static <N, E, W extends Wrapper<E>> MsgBuilder LogOnNotify(String message, N notifier, W source, SerializableFunction<E, ?>... data) {
        LogBuilder logger = new LogBuilder(message, notifier);
        logger.input(source, data);
        return logger.build();
    }

    public <T> LogBuilder input(T source, SerializableFunction<T, ?>... data) {
        SourceInfo sourceInfo = addSource(source);
        for (SerializableFunction<T, ?> function : data) {
            Method getMethod = function.method();
            valuesList.add(new ValueAccessor(messageParts[count], sourceInfo, getMethod));
            count++;
        }
        return this;
    }

    public <T, W extends Wrapper<T>> LogBuilder input(W functionWrapper, SerializableFunction<T, ?>... data) {
        SourceInfo sourceInfo = addSource(functionWrapper);
        for (SerializableFunction<T, ?> function : data) {
            Method getMethod =  function.method();
            valuesList.add(new ValueAccessor(messageParts[count], sourceInfo, functionWrapper, getMethod));
            count++;
        }
        return this;
    }

    public MsgBuilder build() {
        try {
            VelocityContext ctx = new VelocityContext();
            String genClassName = "MsgBuilder" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(updateNotifier.name(), logNotifier);
            ctx.put("valueAccessorList", valuesList);
            if (count >= messageParts.length) {
                ctx.put("lastMessage", "");
            } else {
                ctx.put("lastMessage", messageParts[messageParts.length - 1]);
            }
            ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
            ctx.put(imports.name(), importMap.asString());
            Class<MsgBuilder> msBuilderClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
            MsgBuilder msgBuilder = msBuilderClass.newInstance();
            //set sources via reflection
            Set<Map.Entry<Object, SourceInfo>> entrySet = inst2SourceInfo.entrySet();
            for (Map.Entry<Object, SourceInfo> entry : entrySet) {
                Object source = entry.getKey();
                String fieldName = entry.getValue().id;
                msBuilderClass.getField(fieldName).set(msgBuilder, source);
            }
            if (logNotifier != null) {
                msBuilderClass.getField("logNotifier").set(msgBuilder, logNotifier);
                ctx.put("logOnNotify", true);
            }
            MAIN_LOGGER.addMsgBuilder(msgBuilder);
            GenerationContext.SINGLETON.getNodeList().add(msgBuilder);
            GenerationContext.SINGLETON.getNodeList().add(MAIN_LOGGER);
            return msgBuilder;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + toString(), e);
        }
    }

    private SourceInfo addSource(Object input) {
        return inst2SourceInfo.computeIfAbsent(input, (in) -> new SourceInfo(
                input.getClass().getCanonicalName(),
                "source_" + input.getClass().getSimpleName() + "_" + GenerationContext.nextId()));

    }

    //add static helper methods that configure patterns and flush rate and add multiple input sources
    @Value
    public static class ValueAccessor {

        private final String message;
        private final String value;

        public ValueAccessor(String message, SourceInfo source, Method accessor) {
            this.message = message;
            value = source.id + "." + accessor.getName() + "()";
        }

        public ValueAccessor(String message, SourceInfo source, Wrapper eventWrapper, Method accessor) {
            this.message = message;
            String eventClass = eventWrapper.eventClass().getCanonicalName();
            value = "((" + eventClass + ")" + source.id + ".event())." + accessor.getName() + "()";
        }
    }

}
