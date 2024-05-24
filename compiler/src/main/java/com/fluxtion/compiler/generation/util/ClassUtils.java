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
package com.fluxtion.compiler.generation.util;

import com.fluxtion.compiler.generation.model.CbMethodHandle;
import com.fluxtion.compiler.generation.model.ExportFunctionData;
import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.compiler.generation.model.SimpleEventProcessorModel;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.FluxtionDontSerialize;
import com.fluxtion.runtime.annotations.NoPropagateFunction;
import lombok.SneakyThrows;
import net.vidageek.mirror.dsl.Mirror;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
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
            isTransient = Modifier.isTransient(fieldOfProperty.getModifiers()) || fieldOfProperty.getAnnotation(FluxtionDontSerialize.class) != null;
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

    //sorting by class type most specific first
    static List<Class<?>> sortClassHierarchy(Set<Class<?>> classSet) {
        ArrayList<Class<?>> clazzListAlpha = new ArrayList<>(classSet);
        ArrayList<Class<?>> clazzSorted = new ArrayList<>();
        clazzListAlpha.sort(new NaturalOrderComparator<>());
        clazzListAlpha.forEach(clazz -> {
            boolean added = false;
            for (int i = 0; i < clazzSorted.size(); i++) {
                Class<?> sortedClazz = clazzSorted.get(i);
                if (sortedClazz.isAssignableFrom(clazz)) {
                    clazzSorted.add(i, clazz);
                    added = true;
                    break;
                }
            }
            if (!added) {
                clazzSorted.add(clazz);
            }
        });
        return clazzSorted;
    }

    @SneakyThrows
    static String wrapExportedFunctionCall(Method delegateMethod, String exportedMethodName, String instanceName) {
        LongAdder argNumber = new LongAdder();
        StringBuilder signature = new StringBuilder("public void " + exportedMethodName);
        signature.append('(');
        StringJoiner sj = new StringJoiner(", ");
        Type[] params = delegateMethod.getGenericParameterTypes();
        for (int j = 0; j < params.length; j++) {
            String param = params[j].getTypeName();
            if (delegateMethod.isVarArgs() && (j == params.length - 1)) // replace T[] with T...
                param = param.replaceFirst("\\[\\]$", "...");
            param += " arg" + argNumber.intValue();
            sj.add(param);
            argNumber.increment();
        }
        signature.append(sj.toString());
        signature.append(", String identifer");
        signature.append("){");
        signature.append("try {\n" +
                "            ExportingNode instance = getNodeById(identifer);");
        signature.append("\n  instance." + delegateMethod.getName() + "(");
        StringJoiner sjInvoker = new StringJoiner(", ");
        for (int i = 0; i < argNumber.intValue(); i++) {
            sjInvoker.add("arg" + i);
        }
        signature.append(sjInvoker.toString());
        signature.append(");\n");
        signature.append("} catch (NoSuchFieldException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }" +
                "    }");
        return signature.toString();
    }

    @SneakyThrows
    static String wrapExportedFunctionCall(Method exportedMethod, List<CbMethodHandle> callBackList, SimpleEventProcessorModel model) {
        String exportedMethodName = exportedMethod.getName();
        LongAdder argNumber = new LongAdder();
        Method delegateMethod = callBackList.get(0).getMethod();
        StringBuilder signature = new StringBuilder("public void " + exportedMethodName);
        signature.append('(');
        StringJoiner sj = new StringJoiner(", ");
        Type[] params = delegateMethod.getGenericParameterTypes();
        for (int j = 0; j < params.length; j++) {
            String param = params[j].getTypeName();
            if (delegateMethod.isVarArgs() && (j == params.length - 1)) // replace T[] with T...
                param = param.replaceFirst("\\[\\]$", "...");
            param += " arg" + argNumber.intValue();
            sj.add(param);
            argNumber.increment();
        }
        signature.append(sj);
        signature.append("){");
        //method calls
        StringJoiner sjInvoker = new StringJoiner(", ", "(", "));");
        for (int i = 0; i < argNumber.intValue(); i++) {
            sjInvoker.add("arg" + i);
        }
        callBackList.forEach(cb -> {
            signature.append("setDirty(").append(cb.getVariableName()).append(", ").
                    append(cb.getVariableName()).append(".").append(cb.getMethod().getName()).append(sjInvoker);
        });
        //close
        signature.append("  triggerCalculation();\n  }");
        return signature.toString();
    }

    @SneakyThrows
    static String wrapExportedFunctionCall(Method exportedMethod, ExportFunctionData exportFunctionData, boolean onEventDispatch) {
        String exportedMethodName = exportedMethod.getName();
        LongAdder argNumber = new LongAdder();
        List<CbMethodHandle> callBackList = exportFunctionData.getFunctionCallBackList();
        Method delegateMethod = callBackList.get(0).getMethod();
        boolean booleanReturn = exportFunctionData.isBooleanReturn();
        StringBuilder signature = booleanReturn ? new StringBuilder("public boolean " + exportedMethodName) : new StringBuilder("public void " + exportedMethodName);
        signature.append('(');
        StringJoiner sj = new StringJoiner(", ");
        Type[] params = delegateMethod.getGenericParameterTypes();
        for (int j = 0; j < params.length; j++) {
            String param = params[j].getTypeName()
                    .replace("$", ".")
                    .replace("java.lang.", "");
            if (delegateMethod.isVarArgs() && (j == params.length - 1)) // replace T[] with T...
                param = param.replaceFirst("\\[\\]$", "...");
            param += " arg" + argNumber.intValue();
            sj.add(param);
            argNumber.increment();
        }
        signature.append(sj);
        signature.append("){\n\t");
        //
        signature.append("processor.auditNewEvent( functionAudit.setFunctionDescription(\"" + exportFunctionData.getExportedmethod().toGenericString() + "\"));\n" +
                "    if(processor.buffering){\n" +
                "      processor.triggerCalculation();\n" +
                "    }\n" +
                "    processor.processing = false;\n\t");
        //method calls
        StringJoiner sjInvoker = new StringJoiner(", ", "(", "));\n\t");
        for (int i = 0; i < argNumber.intValue(); i++) {
            sjInvoker.add("arg" + i);
        }
        callBackList.forEach(cb -> {
            String variableName = cb.getVariableName();
            String methodName = cb.getMethod().getName();
            signature.append("processor.nodeInvoked(" + variableName + ", \"" + variableName + "\", \"" + methodName + "\", functionAudit);\n");
            if (!exportFunctionData.isPropagateMethod()) {
                signature.append(variableName).append(".").append(methodName).append(sjInvoker.toString().replace("));", ");"));
            } else if (cb.getMethod().getReturnType() == void.class) {
                signature.append(variableName).append(".").append(methodName).append(sjInvoker.toString().replace("));", ");"));
                signature.append("processor.setDirty(").append(variableName).append(", true);\n\t");
            } else {
                signature.append("processor.setDirty(").
                        append(variableName).append(", ").append(variableName).append(".").append(methodName).append(sjInvoker);
            }
        });
        signature.append("processor.triggerCalculation();\n" +
                "    processor.dispatchQueuedCallbacks();\n" +
                "    processor.processing = false;\n");
        if (booleanReturn) {
            signature.append("    return true;\n");
        }
        signature.append("}");
        return signature.toString();
    }

    static List<AnnotatedType> getAllAnnotatedAnnotationTypes(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<AnnotatedType> interfaceList = new ArrayList<>();
        while (clazz != null) {
            Arrays.asList(clazz.getAnnotatedInterfaces()).stream().filter(a -> a.isAnnotationPresent(annotation)).forEach(interfaceList::add);
            clazz = clazz.getSuperclass();
        }
        return interfaceList;
    }

    static boolean isPropagatingExportService(Class<?> clazz) {
        return isPropagatingExportService(clazz, null);
    }

    static boolean isPropagatingExportService(Class<?> clazz, Class<?> exportedService) {
        return Arrays.stream(clazz.getAnnotatedInterfaces())
                .filter(c -> exportedService == null || c.getType().equals(exportedService))
                .filter(a -> a.isAnnotationPresent(ExportService.class))
                .filter(c -> c.getAnnotation(ExportService.class).propagate())
                .map(AnnotatedType::getType)
                .filter(Class.class::isInstance)
                .map(Class.class::cast)
                .map(Class::getMethods)
                .flatMap(Arrays::stream)
                .anyMatch(m -> {
                    try {
                        Method implMethod = clazz.getMethod(m.getName(), m.getParameterTypes());
                        return !implMethod.isAnnotationPresent(NoPropagateFunction.class);
                    } catch (NoSuchMethodException e) {
                        //cant find method - assume exported
                    }
                    return true;
                });
    }

    static List<Type> getAllAnnotatedTypes(Class<?> clazz, Class<? extends Annotation> annotation) {
        return ClassUtils.getAllAnnotatedAnnotationTypes(clazz, annotation).stream().map(AnnotatedType::getType).collect(Collectors.toList());
    }
}
