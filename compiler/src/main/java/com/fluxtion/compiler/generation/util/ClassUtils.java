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
import net.vidageek.mirror.dsl.Mirror;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
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
}
