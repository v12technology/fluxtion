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
package com.fluxtion.generator.model;

import static java.lang.reflect.Modifier.isFinal;

import com.fluxtion.generator.util.ClassUtils;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Greg Higgins
 */
public class Field {

    public final String name;
    public final String fqn;
    public final boolean publicAccess;
    public final Object instance;

    public Field(String fqn, String name, Object instance, boolean publicAccess) {
        this.fqn = fqn;
        this.name = name;
        this.instance = instance;
        this.publicAccess = publicAccess;
    }

    @Override
    public String toString() {
        return "Field{"
                + "name=" + name
                + ", fqn=" + fqn
                + ", publicAccess=" + publicAccess
                + ", instance=" + instance
                + '}';
    }

    public static class MappedField extends Field {

        public final String mappedName;
        public boolean collection = false;
        public boolean primitive = false;
        public Object primitiveVal;
        public ArrayList<Field> elements;
        private String primitivePrefix = "";
        private String primitiveSuffix = "";
        
        
        public String derivedVal;

        public MappedField(String mappedName, Field f) {
            super(f.fqn, f.name, f.instance, f.publicAccess);
            this.mappedName = mappedName;
            Class<?> aClass = f.instance.getClass();
            collection = List.class.isAssignableFrom(aClass);
            elements = new ArrayList<>();
        }

        public MappedField(String mappedName) {
            super(List.class.getName(), null, null, false);
            this.mappedName = mappedName;
            collection = true;
            elements = new ArrayList<>();
        }

        public MappedField(String mappedName, Object primitiveValue) {
            super(null, null, null, false);
            this.mappedName = mappedName;
            collection = false;
            primitive = true;
            primitiveVal = primitiveValue;
            if (primitiveVal.getClass() == Float.class) {
                primitiveSuffix = "f";
            }
            if (primitiveVal.getClass() == Byte.class) {
                primitivePrefix = "(byte)";
            }
            if (primitiveVal.getClass() == Short.class) {
                primitivePrefix = "(short)";
            }
            if (primitiveVal.getClass() == Long.class) {
                primitiveSuffix = "L";
            }
            if (primitiveVal.getClass() == Character.class) {
                primitivePrefix = "'";
                primitiveSuffix = "'";
            }
            if (primitiveVal.getClass() == String.class) {
                primitivePrefix = "\"";
                primitiveSuffix = "\"";
            }
        }

        public Class parentClass() {
            if (collection) {
                return List.class;
            } else if (primitive) {
                if (primitiveVal.getClass() == Integer.class) {
                    return int.class;
                }
                if (primitiveVal.getClass() == Double.class) {
                    return double.class;
                }
                if (primitiveVal.getClass() == Float.class) {
                    return float.class;
                }
                if (primitiveVal.getClass() == Byte.class) {
                    return byte.class;
                }
                if (primitiveVal.getClass() == Short.class) {
                    return short.class;
                }
                if (primitiveVal.getClass() == Long.class) {
                    return long.class;
                }
                if (primitiveVal.getClass() == Boolean.class) {
                    return boolean.class;
                }
                if (primitiveVal.getClass() == Character.class) {
                    return char.class;
                }
                return primitiveVal.getClass();
            } else {
                return instance.getClass();
            }
        }

        public String value() {
            return derivedVal;
//            String val = name;
//            if (collection) {
//                val = elements.stream().map(f -> f.name).collect(Collectors.joining(", ", "Arrays.asList(", ")"));
//            } else if (primitive) {
//                val = primitivePrefix + primitiveVal.toString() + primitiveSuffix;
//            }
//            return val;
        }

        public void addField(Field field) {
            if (field != null) {
                elements.add(field);
            }
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }

        @Override
        public String toString() {
            return "MappedField{"
                    + "mappedName=" + mappedName
                    + ", name=" + name
                    + ", collection=" + collection
                    + ", fqn=" + fqn
                    + ", publicAccess=" + publicAccess
                    + ", instance=" + instance
                    + '}';
        }

        public static boolean typeSupported(java.lang.reflect.Field input) {
            final int modifiers = input.getModifiers();
//                    && (isPrivate(modifiers) || isProtected(modifiers))
            
            return isFinal(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && ClassUtils.typeSupported(input.getType());
//                    && (input.getType().isPrimitive() || input.getType() == String.class);
        }

    }

}
