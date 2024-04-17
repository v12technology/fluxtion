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
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.NoPropagateFunction;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.node.NamedNode;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Greg Higgins
 */
public class ClassUtilsTest {

    @Test
    public void testCbLocate() throws NoSuchMethodException {
        Handler1 h = new Handler1();
        CbMethodHandle cbA = new CbMethodHandle(h.getClass().getMethod("handleA", A.class), h, null);
        CbMethodHandle cbB = new CbMethodHandle(h.getClass().getMethod("handleB", B.class), h, null);
        CbMethodHandle cbB1 = new CbMethodHandle(h.getClass().getMethod("handleB1", B1.class), h, null);
        CbMethodHandle cbB2 = new CbMethodHandle(h.getClass().getMethod("handleB2", B2.class), h, null);
        CbMethodHandle cbObj = new CbMethodHandle(h.getClass().getMethod("handleObject", Object.class), h, null);
        List<CbMethodHandle> cbList = Arrays.asList(cbA, cbB, cbB1, cbB2);
//        findBestParentCB
        CbMethodHandle findBestParentCB = ClassUtils.findBestParentCB(new B1(), cbList);
        assertEquals(cbB1, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B2(), cbList);
        assertEquals(cbB2, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B(), cbList);
        assertEquals(cbB, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new A(), cbList);
        assertEquals(cbA, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB("", cbList);
        assertEquals(null, findBestParentCB);
        //add Object CB
        cbList = Arrays.asList(cbA, cbB, cbB1, cbB2, cbObj);
        findBestParentCB = ClassUtils.findBestParentCB(new B1(), cbList);
        assertEquals(cbB1, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B2(), cbList);
        assertEquals(cbB2, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B(), cbList);
        assertEquals(cbB, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new A(), cbList);
        assertEquals(cbA, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB("", cbList);
        assertEquals(cbObj, findBestParentCB);
    }

    @Test
    public void testClassHierarchySort() {
        HashSet<Class<?>> classSet = new HashSet<>(Arrays.asList(
                Object.class, NumberFormat.class, A.class, B.class, B1.class, B2.class, DecimalFormat.class,
                String.class, CharSequence.class));
        List<Class<?>> sortClassHierarchy = ClassUtils.sortClassHierarchy(classSet);
        Assert.assertEquals(sortClassHierarchy.size(), sortClassHierarchy.indexOf(Object.class) + 1);
        Assert.assertTrue(sortClassHierarchy.indexOf(B.class) > sortClassHierarchy.indexOf(B1.class));
        Assert.assertTrue(sortClassHierarchy.indexOf(B.class) > sortClassHierarchy.indexOf(B2.class));

        Assert.assertTrue(sortClassHierarchy.indexOf(NumberFormat.class) > sortClassHierarchy.indexOf(DecimalFormat.class));
        Assert.assertTrue(sortClassHierarchy.indexOf(CharSequence.class) > sortClassHierarchy.indexOf(String.class));

        Assert.assertTrue(sortClassHierarchy.indexOf(Object.class) > sortClassHierarchy.indexOf(A.class));
    }

    public void crazyMethod(String a, List<String> x, Map<List<List<String>>, ?> map) {

    }

    @Test
    public void printSignature() {
        String generated = Arrays.stream(ClassUtilsTest.class.getDeclaredMethods())
                .filter(m -> m.getName().contains("crazyMethod"))
                .findAny()
                .map(m -> ClassUtils.wrapExportedFunctionCall(m, "wrappedCrazy", "instanceA"))
                .get();
        String expected = "" +
                "public void wrappedCrazy(java.lang.String arg0, java.util.List<java.lang.String> arg1, java.util.Map<java.util.List<java.util.List<java.lang.String>>, ?> arg2, String identifer){" +
                "    try {\n" +
                "        ExportingNode instance = getNodeById(identifer);\n" +
                "        instance.crazyMethod(arg0, arg1, arg2);\n" +
                "    } catch (NoSuchFieldException e) {\n" +
                "        throw new RuntimeException(e);\n" +
                "    }" +
                "}";
        assertEquals(StringUtils.deleteWhitespace(expected), StringUtils.deleteWhitespace(generated));
    }

    @Test
    public void testExportAnnotationOnSuperClass() {
        List<Type> list = ClassUtils.getAllAnnotatedAnnotationTypes(B_Export.class, ExportService.class).stream().map(AnnotatedType::getType).collect(Collectors.toList());
        MatcherAssert.assertThat(
                list,
                IsIterableContainingInAnyOrder.containsInAnyOrder(NamedNode.class, BatchHandler.class));

        MatcherAssert.assertThat(
                ClassUtils.getAllAnnotatedTypes(B_Export.class, ExportService.class),
                IsIterableContainingInAnyOrder.containsInAnyOrder(NamedNode.class, BatchHandler.class));


        list = ClassUtils.getAllAnnotatedAnnotationTypes(A_Export.class, ExportService.class).stream().map(AnnotatedType::getType).collect(Collectors.toList());
        MatcherAssert.assertThat(
                list,
                IsIterableContainingInAnyOrder.containsInAnyOrder(NamedNode.class));

        MatcherAssert.assertThat(
                ClassUtils.getAllAnnotatedTypes(A_Export.class, ExportService.class),
                IsIterableContainingInAnyOrder.containsInAnyOrder(NamedNode.class));

        Assert.assertTrue(ClassUtils.getAllAnnotatedAnnotationTypes(A.class, ExportService.class).isEmpty());
        Assert.assertTrue(ClassUtils.getAllAnnotatedTypes(A.class, ExportService.class).isEmpty());
    }

    @Test
    public void testExportNoPropagate() {
        boolean export1 = ClassUtils.isPropagatingExportService(C_Export.class, NamedNode.class);
        boolean export2 = ClassUtils.isPropagatingExportService(C_Export.class, BatchHandler.class);
        boolean export3 = ClassUtils.isPropagatingExportService(C_Export.class, Date.class);
        Assert.assertFalse(export1);
        Assert.assertTrue(export2);
        Assert.assertFalse(export3);
    }

    @Test
    public void testAtLeastOneExportedMethodPropagates() {
        Assert.assertTrue(ClassUtils.isPropagatingExportService(A_Export.class));
        Assert.assertTrue(ClassUtils.isPropagatingExportService(B_Export.class));
        Assert.assertTrue(ClassUtils.isPropagatingExportService(C_Export.class));

        Assert.assertFalse(ClassUtils.isPropagatingExportService(No_Export_Service.class));
        Assert.assertFalse(ClassUtils.isPropagatingExportService(No_ExportMethods_Service.class));
    }

    public static class A_Export implements @ExportService NamedNode, BatchHandler {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public void batchPause() {

        }

        @Override
        public void batchEnd() {

        }
    }

    public static class B_Export extends A_Export implements @ExportService BatchHandler {

        @Override
        public void batchPause() {

        }

        @Override
        public void batchEnd() {

        }
    }

    public static class C_Export
            implements
            @ExportService(propagate = false) NamedNode,
            @ExportService BatchHandler {

        @Override
        public void batchPause() {

        }

        @Override
        public void batchEnd() {

        }

        @Override
        public String getName() {
            return null;
        }
    }

    public static class No_Export_Service implements @ExportService(propagate = false) NamedNode {

        @Override
        public String getName() {
            return "";
        }
    }

    public static class No_ExportMethods_Service implements @ExportService NamedNode {

        @Override
        @NoPropagateFunction
        public String getName() {
            return "";
        }
    }

    public static class A {
    }

    public static class B {
    }

    public static class B1 extends B {
    }

    public static class B2 extends B1 {
    }

    public static class Handler1 {
        public void handleA(A a) {
        }

        public void handleB(B a) {
        }

        public void handleB1(B1 a) {
        }

        public void handleB2(B2 a) {
        }

        public void handleObject(Object o) {
        }
    }
}
