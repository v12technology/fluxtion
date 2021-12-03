/*
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.generator.targets;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.event.FilteredHandlerInvoker;
import com.fluxtion.builder.generation.FilterDescription;
import com.fluxtion.generator.model.InvokerFilterTarget;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author greg
 */
public interface JavaGenHelper {

    StringBuilder builder = new StringBuilder(1 * 1000 * 1000);

    static String generateMapDispatch(ArrayList<InvokerFilterTarget> filteredInvokerList, List<Class<?>> importClassList) {
        builder.delete(0, builder.length());
        if (filteredInvokerList == null || filteredInvokerList.isEmpty()) {
            return "";
        }
        importClassList.add(FilteredHandlerInvoker.class);

        builder.append("//int filter maps\n");
        filteredInvokerList.stream().filter(i -> i.filterDescription.isIntFilter).map((invoker) -> invoker.intMapName)
                .collect(Collectors.toSet()).forEach(
                        (methodName) -> builder.append("\tprivate final Int2ObjectOpenHashMap<FilteredHandlerInvoker> ")
                                .append(methodName).append(" = init").append(methodName).append("();\n\n")
                );
        builder.append("//String filter maps\n");
        filteredInvokerList.stream().filter(i -> !i.filterDescription.isIntFilter).map((invoker) -> invoker.stringMapName)
                .collect(Collectors.toSet()).forEach(
                        (methodName) -> builder.append("\tprivate final HashMap<String, FilteredHandlerInvoker> ")
                                .append(methodName).append(" = init").append(methodName).append("();\n\n")
                );

        HashSet<Class<? extends Event>> setIntClasses = new HashSet<>();
        HashSet<Class<? extends Event>> setStrClasses = new HashSet<>();

        for (InvokerFilterTarget invoker1 : filteredInvokerList) {
            Class<? extends Event> e = invoker1.filterDescription.eventClass;
            if (invoker1.filterDescription.isIntFilter && !setIntClasses.contains(e)) {
                builder.append("\tprivate Int2ObjectOpenHashMap<FilteredHandlerInvoker> init" + invoker1.intMapName + "(){\n"
                        + "\t\tInt2ObjectOpenHashMap<FilteredHandlerInvoker> dispatchMap = new Int2ObjectOpenHashMap<>();\n");

                filteredInvokerList.stream().filter(i -> i.filterDescription.eventClass == e).filter(i -> i.filterDescription.isIntFilter).forEach(
                        (invoker)
                                -> builder.append("\t\tdispatchMap.put( " + invoker.filterDescription.value + ", new FilteredHandlerInvoker() {\n"
                                + "\n"
                                + "\t\t\t@Override\n"
                                + "\t\t\tpublic void invoke(Object event) {\n"
                                + "\t\t\t\t"
                                + generateFilteredDispatchMethodName(invoker.filterDescription)
                                + "( (" + invoker.filterDescription.eventClass.getCanonicalName() + ")event);\n"
                                + "\t\t\t}\n"
                                + "\t\t});"
                                + "\t\t\n"));

                builder.append("\t\treturn dispatchMap;\n"
                        + "\t}\n\n");

                setIntClasses.add(e);

            }

            if (!invoker1.filterDescription.isIntFilter && !setStrClasses.contains(e)) {
                builder.append("\tprivate HashMap<String, FilteredHandlerInvoker> init" + invoker1.stringMapName + "(){\n"
                        + "\t\tHashMap<String, FilteredHandlerInvoker> dispatchMap = new HashMap<>();\n");

                filteredInvokerList.stream().filter(i -> i.filterDescription.eventClass == e).filter(i -> !i.filterDescription.isIntFilter).forEach(
                        (invoker)
                                -> builder.append("\t\tdispatchMap.put(\"" + invoker.filterDescription.stringValue + "\", new FilteredHandlerInvoker() {\n"
                                + "\n"
                                + "\t\t\t@Override\n"
                                + "\t\t\tpublic void invoke(Object event) {\n"
                                + "\t\t\t\t"
                                + generateFilteredDispatchMethodName(invoker.filterDescription)
                                + "( (" + invoker.filterDescription.eventClass.getCanonicalName() + ")event);\n"
                                + "\t\t\t}\n"
                                + "\t\t});"
                                + "\t\t\n"));
                builder.append("\t\treturn dispatchMap;\n"
                        + "\t}\n\n");

                setStrClasses.add(e);
            }

            if (setStrClasses.size() > 0) {
                importClassList.add(HashMap.class);
            }
        }
        //loop and generate the methods - check no name clashes on the method names
        filteredInvokerList.forEach((invoker) ->
                builder.append("\tprivate void ").append(invoker.methodName).append("(")
                        .append(invoker.filterDescription.eventClass.getCanonicalName()).append(" typedEvent){\n")
                        .append("\t\t//method body - invoke call tree\n").append("\t\t")
                        .append(invoker.methodBody).append("\t}\n\n")
        );
        return builder.toString();
    }


    static String generateFilteredDispatchMethodName(FilterDescription filter) {
        String filterName = filter.variableName;
        if (filterName == null) {
            filterName = (filter.isIntFilter ? filter.value : filter.stringValue) + "";
        }
        filterName = filter.isFiltered ? filterName : "NoFilter";
        String filterClass = "noFilterClass";
        if (filter.eventClass != null) {
            filterClass = filter.eventClass.getSimpleName();
        }
        return getIdentifier("handle_" + filterClass + "_" + filterName);
    }

    static String generateFilteredDispatchMap(Class<? extends Event> clazz, boolean isInt) {
        FilterDescription filter = new FilterDescription(clazz, 0);
        if (!isInt) {
            filter = new FilterDescription(clazz, "");
        }
        return generateFilteredDispatchMap(filter);
    }

    static String generateFilteredDispatchMap(FilterDescription filter) {
        String type = (filter.isIntFilter ? "Int" : "String");
        String filterClass = "noFilterClass";
        if (filter.eventClass != null) {
            filterClass = filter.eventClass.getSimpleName();
        }
        return getIdentifier("dispatch" + type + "Map" + filterClass);
    }

    static String getIdentifier(String str) {
        StringBuilder sb = new StringBuilder();
        str = StringUtils.uncapitalize(str);
        if (!Character.isJavaIdentifierStart(str.charAt(0))) {
            sb.append("_");
        }
        for (char c : str.toCharArray()) {
            if (!Character.isJavaIdentifierPart(c)) {
                sb.append("_");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    static Class<?> mapWrapperToPrimitive(Class<?> clazz) {
        Class<?> retClass = void.class;
        switch (clazz.getSimpleName()) {
            case "Integer":
                retClass = int.class;
                break;
            case "Double":
                retClass = double.class;
                break;
            case "Float":
                retClass = float.class;
                break;
            case "Short":
                retClass = short.class;
                break;
            case "Byte":
                retClass = byte.class;
                break;
            case "Long":
                retClass = long.class;
                break;
            case "Character":
                retClass = char.class;
                break;
        }
        return retClass;
    }

    static Class<?> mapPrimitiveToWrapper(Class<?> clazz) {
        Class<?> retClass = void.class;
        switch (clazz.getSimpleName()) {
            case "int":
                retClass = Integer.class;
                break;
            case "double":
                retClass = Double.class;
                break;
            case "float":
                retClass = Float.class;
                break;
            case "short":
                retClass = Short.class;
                break;
            case "byte":
                retClass = Byte.class;
                break;
            case "long":
                retClass = Long.class;
                break;
            case "char":
                retClass = Character.class;
                break;
        }
        return retClass;
    }


}
