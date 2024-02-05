/*
 * Copyright (c) 2020, 2024 gregory higgins.
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

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SuperMethodAnnotationScanner {
    private SuperMethodAnnotationScanner() {
    }

    /**
     * Returns the 0th element of the list returned by
     * {@code getAnnotations}, or {@code null} if the
     * list would be empty.
     *
     * @param <A> the type of the annotation to find.
     * @param m   the method to begin the search from.
     * @param t   the type of the annotation to find.
     * @return the first annotation found of the specified type which
     * is present on {@code m}, or present on any methods which
     * {@code m} overrides.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <A extends Annotation> A getAnnotation(Method m, Class<A> t) {
        List<A> list = getAnnotations(m, t);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Let {@code D} be the class or interface which declares the method
     * {@code m}.
     * <p>
     * Returns a list of all of the annotations of the specified type
     * which are either present on {@code m}, or present on any methods
     * declared by a supertype of {@code D} which {@code m} overrides.
     * <p>
     * Annotations are listed in order of nearest proximity to {@code D},
     * that is, assuming {@code D extends E} and {@code E extends F}, then
     * the returned list would contain annotations in the order of
     * {@code [D, E, F]}. A bit more formally, if {@code Sn} is the nth
     * superclass of {@code D} (where {@code n} is an integer starting at 0),
     * then the index of the annotation present on {@code Sn.m} is {@code n+1},
     * assuming annotations are present on {@code m} for every class.
     * <p>
     * Annotations from methods declared by the superinterfaces of {@code D}
     * appear <em>last</em> in the list, in order of their declaration,
     * recursively. For example, if {@code class D implements X, Y} and
     * {@code interface X extends Z}, then annotations will appear in the
     * list in the order of {@code [D, X, Z, Y]}.
     *
     * @param <A> the type of the annotation to find.
     * @param m   the method to begin the search from.
     * @param t   the type of the annotation to find.
     * @return a list of all of the annotations of the specified type
     * which are either present on {@code m}, or present on any
     * methods which {@code m} overrides.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <A extends Annotation> List<A> getAnnotations(Method m, Class<A> t) {
        List<A> list = new ArrayList<>();
        Collections.addAll(list, m.getAnnotationsByType(t));
        Class<?> decl = m.getDeclaringClass();

        for (Class<?> supr = decl; (supr = supr.getSuperclass()) != null; ) {
            addAnnotations(list, m, t, supr);
        }
        for (Class<?> face : getAllInterfaces(decl)) {
            addAnnotations(list, m, t, face);
        }

        return list;
    }

    public static <A extends Annotation> boolean annotationInHierarchy(Method m, Class<A> t) {
        return getAnnotations(m, t).size() > 0;
    }

    private static Set<Class<?>> getAllInterfaces(Class<?> c) {
        Set<Class<?>> set = new LinkedHashSet<>();
        do {
            addAllInterfaces(set, c);
        } while ((c = c.getSuperclass()) != null);
        return set;
    }

    private static void addAllInterfaces(Set<Class<?>> set, Class<?> c) {
        for (Class<?> i : c.getInterfaces()) {
            if (set.add(i)) {
                addAllInterfaces(set, i);
            }
        }
    }

    private static <A extends Annotation> void addAnnotations
            (List<A> list, Method m, Class<A> t, Class<?> decl) {
        try {
            Method n = decl.getDeclaredMethod(m.getName(), m.getParameterTypes());
            if (overrides(m, n)) {
                Collections.addAll(list, n.getAnnotationsByType(t));
            }
        } catch (NoSuchMethodException x) {
        }
    }

    /**
     * @param a the method which may override {@code b}.
     * @param b the method which may be overridden by {@code a}.
     * @return {@code true} if {@code a} probably overrides {@code b}
     * and {@code false} otherwise.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static boolean overrides(Method a, Method b) {
        if (!a.getName().equals(b.getName()))
            return false;
        Class<?> classA = a.getDeclaringClass();
        Class<?> classB = b.getDeclaringClass();
        if (classA.equals(classB))
            return false;
        if (!classB.isAssignableFrom(classA))
            return false;
        int modsA = a.getModifiers();
        int modsB = b.getModifiers();
        if (Modifier.isPrivate(modsA) || Modifier.isPrivate(modsB))
            return false;
        if (Modifier.isStatic(modsA) || Modifier.isStatic(modsB))
            return false;
        if (Modifier.isFinal(modsB))
            return false;
        if (compareAccess(modsA, modsB) < 0)
            return false;
        if ((isPackageAccess(modsA) || isPackageAccess(modsB))
                && !Objects.equals(classA.getPackage(), classB.getPackage()))
            return false;
        if (!b.getReturnType().isAssignableFrom(a.getReturnType()))
            return false;
        Class<?>[] paramsA = a.getParameterTypes();
        Class<?>[] paramsB = b.getParameterTypes();
        if (paramsA.length != paramsB.length)
            return false;
        for (int i = 0; i < paramsA.length; ++i)
            if (!paramsA[i].equals(paramsB[i]))
                return false;
        return true;
    }

    public static boolean isPackageAccess(int mods) {
        return (mods & ACCESS_MODIFIERS) == 0;
    }

    private static final int ACCESS_MODIFIERS =
            Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
    private static final List<Integer> ACCESS_ORDER =
            Arrays.asList(Modifier.PRIVATE,
                    0,
                    Modifier.PROTECTED,
                    Modifier.PUBLIC);

    public static int compareAccess(int lhs, int rhs) {
        return Integer.compare(ACCESS_ORDER.indexOf(lhs & ACCESS_MODIFIERS),
                ACCESS_ORDER.indexOf(rhs & ACCESS_MODIFIERS));
    }
}
