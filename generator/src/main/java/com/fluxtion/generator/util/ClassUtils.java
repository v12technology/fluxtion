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
package com.fluxtion.generator.util;

import com.fluxtion.api.partition.LambdaReflection.MethodReferenceReflection;
import com.fluxtion.generator.model.CbMethodHandle;
import com.fluxtion.generator.model.Field;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.vidageek.mirror.dsl.Mirror;
import org.apache.commons.lang3.StringEscapeUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Higgins
 */
public interface ClassUtils {

    Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * finds the CbMethodHandle whose parameter most closely matches the class
     * of the parent in the inheritance tree.
     *
     * If no match is found a null is returned.
     *
     * @param parent node to interrogate
     * @param cbs collection of callbacks
     * @return The best matched callback handle
     */
    static CbMethodHandle findBestParentCB(Object parent, Collection<CbMethodHandle> cbs) {
        Set<Class<?>> classList = cbs.stream()
                .filter(cb -> cb.method.getParameterTypes()[0].isAssignableFrom(parent.getClass()))
                .map(cb -> cb.method.getParameterTypes()[0])
                .collect(Collectors.toSet());
        if (classList.isEmpty()) {
            return null;
        }

        Optional<Class<?>> bestMatch = classList.stream().sorted((c1, c2) -> {
            if (c1 == c2) {
                return 0;
            }
            if (c1.isAssignableFrom(c2)) {
                return 1;
            }
            return -1;
        }).findFirst();

        Optional<CbMethodHandle> findFirst = cbs.stream()
                .filter(cb -> cb.method.getParameterTypes()[0] == bestMatch.orElse(null))
                .findFirst();
        return findFirst.orElse(null);

    }

    static boolean typeSupported(Class<?> type) {
        boolean zeroArg = false;
        try {
            type.getDeclaredConstructor();
            zeroArg = true;
        } catch (NoSuchMethodException | SecurityException ex) {}
        return type.isPrimitive()
                || type == String.class
                || type == Class.class
                || type.isEnum()
                || List.class.isAssignableFrom(type)
                || type.isArray()
                || MethodReferenceReflection.class.isAssignableFrom(type)
                || zeroArg;
    }

    static String mapToJavaSource(Object primitiveVal, List<Field> nodeFields, Set<Class<?>> importList) {
        Class clazz = primitiveVal.getClass();
        String primitiveSuffix = "";
        String primitivePrefix = "";
        Object original = primitiveVal;
        if (List.class.isAssignableFrom(clazz)) {
            importList.add(Arrays.class);
            List values = (List) primitiveVal;
            primitiveVal = values.stream().map(f -> mapToJavaSource(f, nodeFields, importList)).collect(Collectors.joining(", ", "Arrays.asList(", ")"));
        }
        if (clazz.isArray()) {
            Class arrayType = clazz.getComponentType();
            importList.add(arrayType);

            ArrayList<String> strings = new ArrayList<>();
            int length = Array.getLength(primitiveVal);
            for (int i = 0; i < length; i++) {
                Object arrayElement = Array.get(primitiveVal, i);
//                System.out.println(arrayElement);
                strings.add(mapToJavaSource(arrayElement, nodeFields, importList));
            }
//            Object[] values = (Object[]) primitiveVal;
            primitiveVal = strings.stream().collect(Collectors.joining(", ", "new " + arrayType.getSimpleName() + "[]{", "}"));
        }
        if (clazz.isEnum()) {
            primitiveVal = clazz.getSimpleName() + "." + ((Enum) primitiveVal).name();
            importList.add(clazz);
        }
        if (clazz == Float.class || clazz == float.class) {
            primitiveSuffix = "f";
        }
        if (clazz == Double.class || clazz == double.class) {
            if (Double.isNaN((double) primitiveVal)) {
                primitiveVal = "Double.NaN";
            }
        }
        if (clazz == Byte.class || clazz == byte.class) {
            primitivePrefix = "(byte)";
        }
        if (clazz == Short.class || clazz == short.class) {
            primitivePrefix = "(short)";
        }
        if (clazz == Long.class || clazz == long.class) {
            primitiveSuffix = "L";
        }
        if (clazz == Character.class || clazz == char.class) {
            primitivePrefix = "'";
            primitiveSuffix = "'";
            primitiveVal = StringEscapeUtils.escapeJava(primitiveVal.toString());
        }
        if (clazz == String.class || clazz == String.class) {
            primitivePrefix = "\"";
            primitiveSuffix = "\"";
            primitiveVal = StringEscapeUtils.escapeJava(primitiveVal.toString());
        }
        if (clazz == Class.class) {
            importList.add((Class) primitiveVal);
            primitiveVal = ((Class) primitiveVal).getSimpleName() + ".class";
        }
        if(MethodReferenceReflection.class.isAssignableFrom(clazz)){
            MethodReferenceReflection ref = (MethodReferenceReflection)primitiveVal;
            importList.add(ref.getContainingClass());
            primitiveVal = ref.getContainingClass().getSimpleName() + "::" + ref.method().getName();
        }
        boolean foundMatch = false;
        for (Field nodeField : nodeFields) {
            if (nodeField.instance == primitiveVal) {
                primitiveVal = nodeField.name;
                foundMatch = true;
                break;
            }
        }
        
        if(!foundMatch && original==primitiveVal && !org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper(clazz)){
            importList.add(clazz);
            primitiveVal = "new " + (clazz).getSimpleName() + "()";  
        }
        return primitivePrefix + primitiveVal.toString() + primitiveSuffix;
    }

    static String mapPropertyToJavaSource(PropertyDescriptor property, Field field, List<Field> nodeFields,
        Set<Class<?>> importList) {
        String ret = null;
        if (!isPropertyTransient(property, field)) {
            try {
                Object value = property.getReadMethod().invoke(field.instance);
                String mappedValue = mapToJavaSource(value, nodeFields, importList);
                String writeMethod = property.getWriteMethod().getName();
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance == value) {
                        mappedValue = nodeField.name;
                        break;
                    }
                }
                ret = writeMethod + "(" + mappedValue + ")";
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOGGER.warn("cannot introspect bean property", e);
            }
        }
        return ret;
    }

    static boolean propertySupported(PropertyDescriptor property, Field field, List<Field> nodeFields) {
        try {
            boolean isTransient = isPropertyTransient(property, field);
            final boolean writeMethod = property.getWriteMethod() != null;
            final boolean hasValue = property.getReadMethod() != null && property.getReadMethod().invoke(field.instance) != null;
            boolean isNode = false;
            if (hasValue) {
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance == property.getReadMethod().invoke(field.instance)) {
                        isNode = true;
                        break;
                    }
                }
            }
            return !isTransient && writeMethod && hasValue && (typeSupported(property.getPropertyType()) || isNode);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
            LOGGER.warn("cannot introspect bean property", e);
        }
        return false;
    }

    static boolean isPropertyTransient(PropertyDescriptor property, Field field) throws SecurityException {
        final Class<?> fieldClass = field.instance.getClass();
        final String name = property.getName();
        final java.lang.reflect.Field fieldOfProperty;
        final Set<java.lang.reflect.Field> allFields = ReflectionUtils.getAllFields(fieldClass, ReflectionUtils.withName(name));
        boolean isTransient = true;
        if (allFields.isEmpty()) {
            //
        } else {
            fieldOfProperty = allFields.iterator().next();
            fieldOfProperty.setAccessible(true);
            isTransient = Modifier.isTransient(fieldOfProperty.getModifiers());
        }
        return isTransient;
    }

    static <T> T getField(String name, Object instance) {
        return (T) new Mirror().on(instance).get().field(name);
    }

    static java.lang.reflect.Field getReflectField(Class clazz, String fieldName)
            throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getReflectField(superClass, fieldName);
            }
        }
    }
}
