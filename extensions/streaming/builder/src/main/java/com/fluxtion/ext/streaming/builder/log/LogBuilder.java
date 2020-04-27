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
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.log.AsciiConsoleLogger;
import com.fluxtion.ext.streaming.api.log.MsgBuilder;
import com.fluxtion.ext.streaming.api.stream.Argument;
import com.fluxtion.ext.streaming.builder.Templates;
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
 * Builder for a simple console logger. 
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

    public static MsgBuilder buildLog(String message, Object notifier) {
        LogBuilder logger = new LogBuilder(message, notifier);
        return logger.build();
    }

    public static MsgBuilder log(String message, Argument... arguments){
        return log(message, null, arguments);
    }
    
    public static MsgBuilder log(String message, Object notifier, Argument... arguments){
        LogBuilder logger = new LogBuilder(message, notifier);
        for (Argument arg : arguments) {
            Object source = arg.getSource();
            Method method = arg.getAccessor();
            if(method == null){
                try {
                    method = source.getClass().getMethod("toString");
                } catch (NoSuchMethodException | SecurityException ex) {
                    throw new RuntimeException("cant fins toString!!!");
                }
            }
            SourceInfo sourceInfo = logger.addSource(source);
            if(arg.isWrapper()){
                logger.valuesList.add(new ValueAccessor(logger.messageParts[logger.count], sourceInfo, (Wrapper) source, method));
            }else{
                logger.valuesList.add(new ValueAccessor(logger.messageParts[logger.count], sourceInfo, method));
            }
            logger.count++;
        }
        return logger.build();
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
