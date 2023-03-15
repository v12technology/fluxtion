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
package com.fluxtion.compiler.generation.util;

import com.fluxtion.compiler.generation.model.CbMethodHandle;
import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.MergeProperty;
import net.vidageek.mirror.dsl.Mirror;
import org.apache.commons.lang3.StringEscapeUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Greg Higgins
 */
public interface ClassUtils {

    Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * finds the CbMethodHandle whose parameter most closely matches the class
     * of the parent in the inheritance tree.
     * <p>
     * If no match is found a null is returned.
     *
     * @param parent node to interrogate
     * @param cbs    collection of callbacks
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
        } catch (NoSuchMethodException | SecurityException ex) {
        }
        return type.isPrimitive()
                || type == String.class
                || type == Class.class
                || type.isEnum()
                || List.class.isAssignableFrom(type)
                || Set.class.isAssignableFrom(type)
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
            List newList = new ArrayList();
            values.stream().forEach(item -> {
                boolean foundMatch = false;
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance.equals(item)) {
                        newList.add(nodeField.instance);
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) {
                    newList.add(item);
                }

            });
            primitiveVal = newList.stream().map(f -> mapToJavaSource(f, nodeFields, importList)).collect(Collectors.joining(", ", "Arrays.asList(", ")"));
        }
        if (Set.class.isAssignableFrom(clazz)) {
            importList.add(Arrays.class);
            importList.add(HashSet.class);
            Set values = (Set) primitiveVal;
            Set newList = new HashSet();
            values.stream().forEach(item -> {
                boolean foundMatch = false;
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance.equals(item)) {
                        newList.add(nodeField.instance);
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) {
                    newList.add(item);
                }

            });
            primitiveVal = newList.stream()
                    .map(f -> mapToJavaSource(f, nodeFields, importList)).
                    collect(Collectors.joining(", ", "new HashSet<>(Arrays.asList(", "))"));
        }
        if (clazz.isArray()) {
            Class arrayType = clazz.getComponentType();
            importList.add(arrayType);

            ArrayList<String> strings = new ArrayList<>();
            int length = Array.getLength(primitiveVal);
            for (int i = 0; i < length; i++) {
                Object arrayElement = Array.get(primitiveVal, i);
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance.equals(arrayElement)) {
                        arrayElement = (nodeField.instance);
                        break;
                    }
                }
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
        if (clazz == String.class) {
            primitivePrefix = "\"";
            primitiveSuffix = "\"";
            primitiveVal = StringEscapeUtils.escapeJava(primitiveVal.toString());
        }
        if (clazz == Class.class) {
            importList.add((Class) primitiveVal);
            primitiveVal = ((Class) primitiveVal).getSimpleName() + ".class";
        }
        boolean foundMatch = false;
        if (MergeProperty.class.isAssignableFrom(clazz)) {
            importList.add(MergeProperty.class);
            MergeProperty<?, ?> mergeProperty = (MergeProperty<?, ?>) primitiveVal;
            LambdaReflection.SerializableBiConsumer<?, ?> setValue = mergeProperty.getSetValue();
            String containingClass = setValue.getContainingClass().getSimpleName();
            String methodName = setValue.method().getName();
            String lambda = containingClass + "::" + methodName;
            String triggerName = "null";
            //
            EventStream<?> trigger = mergeProperty.getTrigger();
            for (Field nodeField : nodeFields) {
                if (nodeField.instance == trigger) {
                    triggerName = nodeField.name;
                    break;
                }
            }
            primitiveVal = "new MergeProperty<>("
                    + triggerName + ", " + lambda + "," + mergeProperty.isTriggering() + "," + mergeProperty.isMandatory() + ")";
        }
        if (MethodReferenceReflection.class.isAssignableFrom(clazz)) {
            MethodReferenceReflection ref = (MethodReferenceReflection) primitiveVal;
            importList.add(ref.getContainingClass());

            if (ref.isDefaultConstructor()) {
                primitiveVal = ref.getContainingClass().getSimpleName() + "::new";
            } else if (ref.captured().length > 0) {
                //see if we can find the reference and set the instance
                Object functionInstance = ref.captured()[0];
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance == functionInstance) {
                        primitiveVal = nodeField.name + "::" + ref.method().getName();
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) {
                    primitiveVal = "new " + ref.getContainingClass().getSimpleName() + "()::" + ref.method().getName();
                }
            } else {
                if (ref.getContainingClass().getTypeParameters().length > 0) {
                    String typeParam = "<Object";
                    for (int i = 1; i < ref.getContainingClass().getTypeParameters().length; i++) {
                        typeParam += ", Object";
                    }
                    typeParam += ">";
                    primitiveVal = ref.getContainingClass().getSimpleName() + typeParam + "::" + ref.method().getName();
                } else {
                    primitiveVal = ref.getContainingClass().getSimpleName() + "::" + ref.method().getName();
                }
            }
        }
        for (Field nodeField : nodeFields) {
            if (nodeField.instance == primitiveVal) {
                primitiveVal = nodeField.name;
                foundMatch = true;
                break;
            }
        }

        if (!foundMatch && original == primitiveVal
                && !org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper(clazz)
                && !String.class.isAssignableFrom(clazz)
                && clazz.getCanonicalName() != null
        ) {
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

    static java.lang.reflect.Field getReflectField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getReflectField(superClass, fieldName);
            }
        }
    }
}
