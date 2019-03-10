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
package com.fluxtion.ext.declarative.builder.event;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterId;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.ext.declarative.builder.Templates;
import com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.eventClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.filter;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.filterType;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.imports;
import com.fluxtion.ext.declarative.builder.util.ImportMap;
import com.fluxtion.api.event.Event;
import com.fluxtion.ext.declarative.api.ReusableEventHandler;
import java.util.Map;
import org.apache.velocity.VelocityContext;

/**
 *
 * @author Greg Higgins
 */
public interface EventSelect {

    static final String TEMPLATE = Templates.PACKAGE + "/EventSelectTemplate.vsl";

    public static <T extends Event> EventWrapper<T> select(Class<T> eventClazz) {
        EventWrapper<T> handler = new ReusableEventHandler(eventClazz);
        return GenerationContext.SINGLETON.addOrUseExistingNode(handler);
    }

    public static <T extends Event> EventWrapper<T> selectOLD(Class<T> eventClazz) {
        return build(eventClazz, null, null);
    }

    public static <T extends Event> EventWrapper<T>[] select(Class<T> eventClazz, int... filterId) {
        EventWrapper[] result = new EventWrapper[filterId.length];
        for (int i = 0; i < filterId.length; i++) {
            result[i] = select(eventClazz, filterId[i]);
        }
        return result;
    }

    public static <T extends Event> EventWrapper<T>[] select(Class<T> eventClazz, String... filterId) {
        EventWrapper[] result = new EventWrapper[filterId.length];
        for (int i = 0; i < filterId.length; i++) {
            result[i] = select(eventClazz, filterId[i]);
        }
        return result;
    }

    public static <T extends Event> EventWrapper<T> select(Class<T> eventClazz, String filterId) {
        return build(eventClazz, filterId, "String");
    }

    public static <T extends Event> EventWrapper<T> selectOld(Class<T> eventClazz, int filterId) {
        return build(eventClazz, "" + filterId, "int");
    }
    
    
    public static <T extends Event> EventWrapper<T> select(Class<T> eventClazz, int filterId) {
        EventWrapper<T> handler = new ReusableEventHandler(filterId, eventClazz);
        return GenerationContext.SINGLETON.addOrUseExistingNode(handler);
    } 

    static <T extends Event> EventWrapper<T> build(Class<T> eventClazz, String filterId, String filteringType) {
        String classKey = eventClazz.getSimpleName() + filteringType;
        String instanceKey = classKey + filterId;

        Map<String, EventWrapper<T>> handlerInstanceMap = GenerationContext.SINGLETON.getCache(EventSelect.class);

        EventWrapper<T> handler = handlerInstanceMap.computeIfAbsent(instanceKey, (k) -> {
            try {
                boolean isStringFilter = filteringType != null && filteringType.equalsIgnoreCase("String");
                Map<String, Class<EventWrapper<T>>> handlerMap = GenerationContext.SINGLETON.getCache(EventSelect.class);
                Class<EventWrapper<T>> eventHandler = handlerMap.computeIfAbsent(classKey, (t) -> {
                    try {
                        VelocityContext ctx = new VelocityContext();
                        String genClassName = eventClazz.getSimpleName() + "Handler";
                        ImportMap importMap = ImportMap.newMap(EventHandler.class, EventWrapper.class, eventClazz);
                        if (isStringFilter) {
                            genClassName += "StringFilter";
                            importMap.addImport(FilterId.class);
                        } else if (filteringType != null) {
                            genClassName += "IntFilter";
                            importMap.addImport(FilterId.class);
                        }
                        ctx.put(functionClass.name(), genClassName);
                        ctx.put(eventClass.name(), eventClazz.getSimpleName());
//                        ctx.put(eventClassFqn.name(), eventClazz.getCanonicalName());
                        ctx.put(filter.name(), (isStringFilter ? "\"" + filterId + "\"" : filterId));
                        ctx.put(filterType.name(), filteringType);
                        ctx.put(imports.name(), importMap.asString());
                        Class<EventWrapper<T>> aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
                        return aggClass;
                    } catch (Exception ex) {
                        throw new RuntimeException("Cannot generate event handler class", ex);
                    }
                });
                EventWrapper<T> eventHandlerTmp = eventHandler.newInstance();
                //set filter value
                if (isStringFilter) {
                    eventHandlerTmp.getClass().getField("filter").set(eventHandlerTmp, filterId);
                } else if (filteringType != null) {
                    eventHandlerTmp.getClass().getField("filter").set(eventHandlerTmp, Integer.valueOf(filterId));
                }
                GenerationContext.SINGLETON.getNodeList().add(eventHandlerTmp);
                return eventHandlerTmp;
            } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                throw new RuntimeException("Cannot generate event handler instance", ex);
            }
        });
        return handler;
    }

    public static String getIdentifier(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isJavaIdentifierStart(str.charAt(0)) || i > 0 && Character.isJavaIdentifierPart(str.charAt(i))) {
                sb.append(str.charAt(i));
            } else {
                sb.append((int) str.charAt(i));
            }
        }
        return sb.toString();
    }
}
