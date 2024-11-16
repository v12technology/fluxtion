/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.targets;

import com.fluxtion.compiler.builder.filter.FilterDescription;
import org.apache.commons.lang3.StringUtils;

/**
 * @author greg
 */
public interface JavaGenHelper {

    StringBuilder builder = new StringBuilder(1000 * 1000);

    static String generateFilteredDispatchMethodName(FilterDescription filter, boolean bufferDispatch) {
        String filterName = filter.variableName;
        if (filterName == null) {
            filterName = (filter.isIntFilter ? filter.value : filter.stringValue) + "";
        }
        filterName = filter.isFiltered ? filterName : "NoFilter";
        String filterClass = "noFilterClass";
        if (filter.eventClass != null) {
            filterClass = filter.eventClass.getSimpleName();
        }
        return getIdentifier("handle_" + filterClass + "_" + filterName + (bufferDispatch ? "_bufferDispatch" : ""));
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
        Class<?> retClass = clazz;
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
