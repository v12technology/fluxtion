/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.integration.etl;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public interface Util {

    /**
     * Converts a fqn String to a {@link TypeName} required by JavaPoet
     * @param type
     * @return 
     */
    public static TypeName typeName(String type) {
        String lowerCase = type.toLowerCase();
        TypeName asType = TypeName.OBJECT;
        switch (lowerCase) {
            case "object":
                asType = TypeName.OBJECT;
                break;
            case "byte":
                asType = TypeName.BYTE;
                break;
            case "char":
                asType = TypeName.CHAR;
                break;
            case "short":
                asType = TypeName.SHORT;
                break;
            case "int":
                asType = TypeName.INT;
                break;
            case "long":
                asType = TypeName.LONG;
                break;
            case "float":
                asType = TypeName.FLOAT;
                break;
            case "double":
                asType = TypeName.DOUBLE;
                break;
            case "boolean":
                asType = TypeName.BOOLEAN;
                break;
            default:
                asType = ClassName.get(ClassUtils.getPackageCanonicalName(type), ClassUtils.getShortCanonicalName(type));
        }
        return asType;
    }

}
