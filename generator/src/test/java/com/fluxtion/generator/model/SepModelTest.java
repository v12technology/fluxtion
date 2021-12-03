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

import com.fluxtion.builder.generation.FilterDescription;
import com.fluxtion.generator.targets.SepJavaSourceModelHugeFilter;
import com.fluxtion.test.event.*;
import com.google.common.base.Predicates;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.reflections.ReflectionUtils.*;

/**
 *
 * @author Greg Higgins
 */
public class SepModelTest {

    @Test
    public void initCBTest() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 3);
        RootCB eRoot = new RootCB("eRoot");
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        InitCB i3 = new InitCB("i3");

        i1.parents = new Object[]{i2};
        i2.parents = new Object[]{e1, e2, i3};
        i3.parents = new Object[]{e3};
        eRoot.parents = new Object[]{i1, i3};

        List<Object> nodeList = Arrays.asList(eRoot, e1, i1, i2, e2, e3, i3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();
        //sizes
        assertEquals(4, sep.getBatchEndMethods().size());
        assertEquals(1, sep.getBatchPauseMethods().size());
        assertEquals(3, sep.getInitialiseMethods().size());
        assertEquals(7, sep.getTearDownMethods().size());
        //expected callback
        Set<Method> initMethodSet = getAllMethods(InitCB.class,
                Predicates.and(
                        withModifier(Modifier.PUBLIC),
                        withName("init"),
                        withParametersCount(0)));
        assertEquals(initMethodSet.size(), 1);
        Method expectedCb = initMethodSet.iterator().next();
        //order
        List<Object> expectedCallList = Arrays.asList(i3, i2, i1);
        List<CbMethodHandle> cbList = sep.getInitialiseMethods();
        for (int i = 0; i < cbList.size(); i++) {
            CbMethodHandle cbHandle = cbList.get(i);
            assertSame(cbHandle.instance, expectedCallList.get(i));
        }
        for (int i = 0; i < cbList.size(); i++) {
            CbMethodHandle cbHandle = cbList.get(i);
            assertEquals(cbHandle.method, expectedCb);
        }
    }

    @Test
    public void sharedFilterTest() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 2);
        RootCB eRoot = new RootCB("eRoot");
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        InitCB i3 = new InitCB("i3");

        i1.parents = new Object[]{i2};
        i2.parents = new Object[]{e1, e2, i3};
        i3.parents = new Object[]{e3};
        eRoot.parents = new Object[]{i1, i3};

        List<Object> nodeList = Arrays.asList(eRoot, e1, i1, i2, e2, e3, i3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();
        SepJavaSourceModelHugeFilter srcModel = new SepJavaSourceModelHugeFilter(sep, true);
        srcModel.buildSourceModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(1, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TestEvent.class);
        assertEquals(2, typedMap.size());

        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 1));
        assertEquals(4, extList.size());

        List<CbMethodHandle> implList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 2));
        assertEquals(6, implList.size());

    }

    @Test
    public void overrideFilterIdTest() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 2);
        RootCB eRoot = new RootCB("eRoot");
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        InitCB i3 = new InitCB("i3");

        i1.parents = new Object[]{i2};
        i2.parents = new Object[]{e1, e2, i3};
        i3.parents = new Object[]{e3};
        eRoot.parents = new Object[]{i1, i3};

        HashMap<Object, Integer> filterMap = new HashMap<>();
        filterMap.put(e1, 100);
        filterMap.put(e2, 200);
        filterMap.put(e3, 200);

        List<Object> nodeList = Arrays.asList(eRoot, e1, i1, i2, e2, e3, i3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph, filterMap);
        sep.generateMetaModel();
        SepJavaSourceModelHugeFilter srcModel = new SepJavaSourceModelHugeFilter(sep, true);
        srcModel.buildSourceModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(1, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TestEvent.class);
        assertEquals(2, typedMap.size());
        //should be null, filter (1) removed for e1
        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 1));
        assertNull(extList);
        //should be 4 as filter mapped to 100 for e1
        extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 100));
        assertEquals(4, extList.size());

        //should be null, filter (2) removed, for e1 and e2
        List<CbMethodHandle> implList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 2));
        assertNull(implList);

        //should be 4 as filter mapped to 200, for e1 and e2
        implList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 200));
        assertEquals(6, implList.size());

    }

    @Test
    public void sortCbHandlerTest() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 3);
        RootCB eRoot = new RootCB("eRoot");
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        InitCB i3 = new InitCB("i3");

        i1.parents = new Object[]{i2};
        i2.parents = new Object[]{e1, e2, i3};
        i3.parents = new Object[]{e3};
        eRoot.parents = new Object[]{i1, i3};

        List<Object> nodeList = Arrays.asList(eRoot, e1, i1, i2, e2, e3, i3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();
        //
        List<CbMethodHandle> cbListExpected = sep.getInitialiseMethods();
        List<CbMethodHandle> cbListRandom = new ArrayList<>(sep.getInitialiseMethods());

        assertTrue(cbListRandom.equals(cbListExpected));

        Collections.reverse(cbListRandom);
        assertFalse(cbListRandom.equals(cbListExpected));

        graph.sortNodeList(cbListRandom);
        assertTrue(cbListRandom.equals(cbListExpected));

    }

    @Test
    public void testNoFilterHandlers() throws Exception {
        AnnotatedTimeHandlerNoFilter timeHandler1 = new AnnotatedTimeHandlerNoFilter();
        AnnotatedTestEventHandlerNoFilter testHandler2 = new AnnotatedTestEventHandlerNoFilter();
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        RootCB root = new RootCB("root");
        i1.parents = new Object[]{timeHandler1};
        i2.parents = new Object[]{testHandler2};
        root.parents = new Object[]{i1, i2};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(timeHandler1, testHandler2, i1, i2, root);
        graph.generateDependencyTree();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(2, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> timeCbMap = dispatchMap.get(TimeEvent.class);
        assertEquals(2, timeCbMap.size());

        List<CbMethodHandle> extList = timeCbMap.get(FilterDescription.NO_FILTER);
        assertEquals(extList.size(), 3);
        assertEquals(extList.get(0).instance, timeHandler1);
        assertEquals(extList.get(1).instance, i1);
        assertEquals(extList.get(2).instance, root);

        Map<FilterDescription, List<CbMethodHandle>> testCbMap = dispatchMap.get(TestEvent.class);
        assertEquals(2, testCbMap.size());

        List<CbMethodHandle> testList = testCbMap.get(FilterDescription.NO_FILTER);
        assertEquals(testList.size(), 3);
        assertEquals(testList.get(0).instance, testHandler2);
        assertEquals(testList.get(1).instance, i2);
        assertEquals(testList.get(2).instance, root);
    }

    @Test
    public void testNoFilterAndInverseHandlers() throws Exception {
        AnnotatedTimeHandlerNoFilter noFilterHandler = new AnnotatedTimeHandlerNoFilter();
        AnnotatedTimeHandler filter_10_Handler = new AnnotatedTimeHandler(10);
        AnnotatedTimeHandlerInverseFilter inverseHandler = new AnnotatedTimeHandlerInverseFilter();
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        RootCB root = new RootCB("root");
        i1.parents = new Object[]{noFilterHandler};
        i2.parents = new Object[]{filter_10_Handler, inverseHandler};
        root.parents = new Object[]{i1, i2};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(noFilterHandler, filter_10_Handler, i1, i2, root, inverseHandler);
        graph.generateDependencyTree();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(2, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> timeCbMap = dispatchMap.get(TimeEvent.class);
        assertEquals(4, timeCbMap.size());

        List<?> extList = timeCbMap.get(FilterDescription.NO_FILTER).stream().map((cb) -> cb.instance).collect(Collectors.toList());
        assertThat(extList, IsIterableContainingInAnyOrder.containsInAnyOrder(noFilterHandler, i1, root));
        
        FilterDescription fd = new FilterDescription(TimeEvent.class, 10);
        extList = timeCbMap.get(fd).stream().map((cb) -> cb.instance).collect(Collectors.toList());
        assertThat(extList, IsIterableContainingInAnyOrder.containsInAnyOrder(noFilterHandler, filter_10_Handler, i1, i2, root));
        
        extList = timeCbMap.get(FilterDescription.INVERSE_FILTER).stream().map((cb) -> cb.instance).collect(Collectors.toList());
        assertThat(extList, IsIterableContainingInAnyOrder.containsInAnyOrder(inverseHandler, i2, root));

    }

    @Test
    public void testMixedFilterdAndNoFilterHandlers() throws Exception {
        AnnotatedTestEventHandler filteredTestHandler = new AnnotatedTestEventHandler(10);
        AnnotatedTestEventHandlerNoFilter unFilteredTestHandler = new AnnotatedTestEventHandlerNoFilter();
        InitCB filteredInter = new InitCB("i1");
        InitCB unFilteredInter = new InitCB("i2");
        RootCB root = new RootCB("root");
        filteredInter.parents = new Object[]{filteredTestHandler};
        unFilteredInter.parents = new Object[]{unFilteredTestHandler};
        root.parents = new Object[]{filteredInter, unFilteredInter};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(filteredTestHandler, unFilteredTestHandler, filteredInter, unFilteredInter, root);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(1, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> testCbMap = dispatchMap.get(TestEvent.class);
        assertEquals(3, testCbMap.size());

        List<CbMethodHandle> testList = testCbMap.get(FilterDescription.NO_FILTER);
        assertEquals(testList.size(), 3);
        assertEquals(testList.get(0).instance, unFilteredTestHandler);
        assertEquals(testList.get(1).instance, unFilteredInter);
        assertEquals(testList.get(2).instance, root);

        List<CbMethodHandle> testListFiltered = testCbMap.get(filterProducer.getFilterDescription(TestEvent.class, 10));
        assertEquals(testListFiltered.size(), 5);

        List nodeList = new ArrayList<>(testListFiltered.size());
        for (int i = 0; i < testListFiltered.size(); i++) {
            CbMethodHandle cb = testListFiltered.get(i);
            nodeList.add(i, cb.instance);
        }

        assertTrue(nodeList.indexOf(root) > nodeList.indexOf(filteredInter));
        assertTrue(nodeList.indexOf(filteredInter) > nodeList.indexOf(filteredTestHandler));
        assertTrue(nodeList.indexOf(root) > nodeList.indexOf(unFilteredInter));
        assertTrue(nodeList.indexOf(unFilteredInter) > nodeList.indexOf(unFilteredTestHandler));

    }

    @Test
    public void testMixedHandlers() throws Exception {
        TimeHandlerExtends thExt = new TimeHandlerExtends(1);
        TimeHandlerImpl thImpl = new TimeHandlerImpl(2);
        RootCB root = new RootCB("root");
        root.parents = new Object[]{thExt, thImpl};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(thExt, thImpl, root);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(1, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TimeEvent.class);
        assertEquals(2, typedMap.size());

        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 1));
        assertEquals(extList.get(0).instance, thExt);
        assertEquals(extList.get(1).instance, root);

        List<CbMethodHandle> implList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 2));
        assertEquals(implList.get(0).instance, thImpl);
        assertEquals(implList.get(1).instance, root);

    }

    @Test
    public void testMixedHandlers2() throws Exception {
        TimerHandler2Removed thExt = new TimerHandler2Removed(1);
        TimeHandlerImpl thImpl = new TimeHandlerImpl(2);
        RootCB root = new RootCB("root");
        root.parents = new Object[]{thExt, thImpl};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(thExt, thImpl, root);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(1, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TimeEvent.class);
        assertEquals(2, typedMap.size());

        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 1));
        assertEquals(extList.get(0).instance, thExt);
        assertEquals(extList.get(1).instance, root);

        List<CbMethodHandle> implList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 2));
        assertEquals(implList.get(0).instance, thImpl);
        assertEquals(implList.get(1).instance, root);

    }

    @Test
    public void testAnnotatedHandlers() throws Exception {
        AnnotatedTimeHandler thExt = new AnnotatedTimeHandler(1);
        AnnotatedTimeHandler thImpl = new AnnotatedTimeHandler(2);
        RootCB root = new RootCB("root");
        root.parents = new Object[]{thExt, thImpl};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(thExt, thImpl, root);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(2, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TimeEvent.class);
        assertEquals(2, typedMap.size());

        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 1));
        assertEquals(extList.get(0).instance, thExt);
        assertEquals(extList.get(1).instance, root);

        List<CbMethodHandle> implList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 2));
        assertEquals(implList.get(0).instance, thImpl);
        assertEquals(implList.get(1).instance, root);

        typedMap = dispatchMap.get(TestEvent.class);
        assertEquals(2, typedMap.size());

        extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 1));
        assertEquals(extList.get(0).instance, thExt);
        assertEquals(extList.get(1).instance, root);

        implList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 2));
        assertEquals(implList.get(0).instance, thImpl);
        assertEquals(implList.get(1).instance, root);

    }
    
    @Test
    public void testFilterOverridesOnAnnotation() throws Exception{
        AnnotatedTimeHandler thExt = new AnnotatedTimeHandler(1);
        AnnotatedEventHandlerWithOverrideFilter thImpl = new AnnotatedEventHandlerWithOverrideFilter();
        
        RootCB root = new RootCB("root");
        root.parents = new Object[]{thExt, thImpl};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(thExt, thImpl, root);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();  
        
        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(2, dispatchMap.size());  
        

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TimeEvent.class);
        assertEquals(4, typedMap.size());

        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 1));
        assertEquals(extList.get(0).instance, thImpl);
        assertEquals(extList.get(1).instance, thExt);
        assertEquals(extList.get(2).instance, root);  

        List<CbMethodHandle> implList = typedMap.get(
                filterProducer.getFilterDescription(TimeEvent.class,
                        AnnotatedEventHandlerWithOverrideFilter.FILTER_ID_TIME));
        assertEquals(implList.get(0).instance, thImpl);
        assertEquals(implList.get(1).instance, thImpl);
        assertEquals(implList.get(2).instance, root);

        typedMap = dispatchMap.get(TestEvent.class);
        assertEquals(2, typedMap.size());

        extList = typedMap.get(
                filterProducer.getFilterDescription(TestEvent.class,
                        AnnotatedEventHandlerWithOverrideFilter.FILTER_STRING_TEST));
        assertEquals(extList.get(0).instance, thImpl);
        assertEquals(extList.get(1).instance, root);
        
        extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 1));
        assertEquals(2, extList.size());
        assertEquals(extList.get(0).instance, thExt);
        assertEquals(extList.get(1).instance, root);
    }

    @Test
    public void testNoPropogationAnnotation() throws Exception{
        AnnotatedHandlerNoPropogate thImpl = new AnnotatedHandlerNoPropogate();
        
        RootCB root = new RootCB("root");
        root.parents = new Object[]{ thImpl};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(thImpl, root);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();  
        
        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(2, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TimeEvent.class);
        assertEquals(2, typedMap.size());
        
        List<CbMethodHandle> implList = typedMap.get(FilterDescription.NO_FILTER);
        assertEquals(1, implList.size());
    }
    
    @Test
    public void testOnCompleteOnRootHandler() throws Exception {
        AnnotatedOnCompleteTestEventHandler handler = new AnnotatedOnCompleteTestEventHandler(1);
        OnEventCompleteHandler complete1 = new OnEventCompleteHandler();
        RootCB root = new RootCB("root");
        complete1.parents = new Object[]{handler};
        root.parents = new Object[]{complete1};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(handler, root, complete1);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        //post dispatch map
        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> postDispatchMap = sep.getPostDispatchMap();
        Map<FilterDescription, List<CbMethodHandle>>  typedMap = postDispatchMap.get(TestEvent.class);
        assertEquals(1, typedMap.size());
        
        //post dispatch map
        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 1));
        ArrayList<Object> luu = new ArrayList<>();
        for (CbMethodHandle implList1 : extList) {
            luu.add(implList1.instance);
        }
        assertEquals(2, extList.size());
        assertThat(luu, containsInAnyOrder(complete1, handler));
    }

    @Test
    public void testOnCompleteHandlers() throws Exception {
        AnnotatedTimeHandler thExt = new AnnotatedTimeHandler(1);
        AnnotatedTimeHandler thImpl = new AnnotatedTimeHandler(2);
        AnnotatedHandlerNoFilter noFilterEh = new AnnotatedHandlerNoFilter();
        OnEventCompleteHandler complete1 = new OnEventCompleteHandler();
        OnEventCompleteHandler complete2 = new OnEventCompleteHandler();
        OnEventCompleteHandler complete3 = new OnEventCompleteHandler();
        RootCB root = new RootCB("root");

        complete1.parents = new Object[]{thExt};
        complete2.parents = new Object[]{thImpl};
        complete3.parents = new Object[]{noFilterEh};
        root.parents = new Object[]{complete1, complete2, complete3};

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(thExt, thImpl, noFilterEh, root, complete1, complete2, complete3);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(2, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TimeEvent.class);
        assertEquals(4, typedMap.size());

        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 1));
        List<CbMethodHandle> implList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 2));
        List<Object> luu = new ArrayList<>();
        for (CbMethodHandle implList1 : implList) {
            luu.add(implList1.instance);
        }
        assertThat(luu, containsInAnyOrder(thImpl, noFilterEh, root));

        //post dispatch map
        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> postDispatchMap = sep.getPostDispatchMap();
        typedMap = postDispatchMap.get(TimeEvent.class);
        assertEquals(4, typedMap.size());

        extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 1));
        luu = new ArrayList<>();
        for (CbMethodHandle implList1 : extList) {
            luu.add(implList1.instance);
        }
        assertEquals(2, extList.size());
        assertThat(luu, containsInAnyOrder(complete1, complete3));

        implList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 2));
        luu = new ArrayList<>();
        for (CbMethodHandle implList1 : implList) {
            luu.add(implList1.instance);
        }
        assertEquals(2, implList.size());
        assertThat(luu, containsInAnyOrder(complete2, complete3));

    }

    @Test
    public void testOverrideFilterIdAnnotatedHandlers() throws Exception {
        AnnotatedTimeHandler thExt = new AnnotatedTimeHandler(1);
        AnnotatedTimeHandler thImpl = new AnnotatedTimeHandler(2);
        RootCB root = new RootCB("root");
        root.parents = new Object[]{thExt, thImpl};

        HashMap<Object, Integer> filterMap = new HashMap<>();
        filterMap.put(thExt, 100);
        filterMap.put(thImpl, 200);

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(thExt, thImpl, root);
        graph.generateDependencyTree();
        DefaultFilterDescriptionProducer filterProducer = new DefaultFilterDescriptionProducer();
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph, filterMap, null);
        sep.generateMetaModel();

        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = sep.getDispatchMap();
        assertEquals(2, dispatchMap.size());

        Map<FilterDescription, List<CbMethodHandle>> typedMap = dispatchMap.get(TimeEvent.class);
        assertEquals(2, typedMap.size());

        List<CbMethodHandle> extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 1));
        assertNull(extList);
        extList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 100));
        assertEquals(2, extList.size());
        assertEquals(extList.get(0).instance, thExt);
        assertEquals(extList.get(1).instance, root);

        List<CbMethodHandle> implList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 2));
        assertNull(implList);
        implList = typedMap.get(filterProducer.getFilterDescription(TimeEvent.class, 200));
        assertEquals(2, implList.size());
        assertEquals(implList.get(0).instance, thImpl);
        assertEquals(implList.get(1).instance, root);

        typedMap = dispatchMap.get(TestEvent.class);
        assertEquals(2, typedMap.size());

        extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 1));
        assertNull(extList);
        extList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 100));
        assertEquals(2, extList.size());
        assertEquals(extList.get(0).instance, thExt);
        assertEquals(extList.get(1).instance, root);

        implList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 2));
        assertNull(implList);
        implList = typedMap.get(filterProducer.getFilterDescription(TestEvent.class, 200));
        assertEquals(2, implList.size());
        assertEquals(implList.get(0).instance, thImpl);
        assertEquals(implList.get(1).instance, root);

    }

    @Test
    public void testParentUpdateCbHandler() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 3);
        RootCB eRoot = new RootCB("eRoot");
        ParentUpdateListener pl_1 = new ParentUpdateListener("pl_1");
        ParentUpdateListener pl_2 = new ParentUpdateListener("pl_2");
        InitCB i3 = new InitCB("i3");

        pl_1.parents = new Object[]{pl_2, e2};
        pl_2.parents = new Object[]{e1, e2, i3};
        i3.parents = new Object[]{e3};
        eRoot.parents = new Object[]{pl_1, i3};

        List<Object> nodeList = Arrays.asList(eRoot, e1, pl_1, pl_2, e2, e3, i3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = sep.getParentUpdateListenerMethodMap();
        //test
        CbMethodHandle cb_pl_1 = new CbMethodHandle(ParentUpdateListener.class.getMethod("parentChanged", Object.class), pl_1, graph.variableName(pl_1));
        CbMethodHandle cb_pl_2 = new CbMethodHandle(ParentUpdateListener.class.getMethod("parentChanged", Object.class), pl_2, graph.variableName(pl_2));

        assertThat(listenerMethodMap.get(pl_2),
                containsInAnyOrder(cb_pl_1));

        assertThat(listenerMethodMap.get(e1),
                containsInAnyOrder(cb_pl_2));

        assertThat(listenerMethodMap.get(e2),
                containsInAnyOrder(cb_pl_1, cb_pl_2));

        assertThat(listenerMethodMap.get(i3),
                containsInAnyOrder(cb_pl_2));
    }

    @Test
    public void testParentListUpdateCbHandler() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 3);
        NodeWithParentList root = new NodeWithParentList(e1, e2, e3);
//        RootCB eRoot = new RootCB("eRoot");
//        ParentUpdateListener pl_1 = new ParentUpdateListener("pl_1");
//        ParentUpdateListener pl_2 = new ParentUpdateListener("pl_2");
//        InitCB i3 = new InitCB("i3");
//
//        pl_1.parents = new Object[]{pl_2, e2};
//        pl_2.parents = new Object[]{e1, e2, i3};
//        i3.parents = new Object[]{e3};
//        eRoot.parents = new Object[]{pl_1, i3};

        List<Object> nodeList = Arrays.asList(root, e1, e2, e3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = sep.getParentUpdateListenerMethodMap();
        //test
        CbMethodHandle cb_pl_1 = new CbMethodHandle(NodeWithParentList.class.getMethod("parentChanged", EventHandlerCb.class), root, graph.variableName(root));

        assertThat(listenerMethodMap.get(e1),
                containsInAnyOrder(cb_pl_1));
        assertThat(listenerMethodMap.get(e2),
                containsInAnyOrder(cb_pl_1));
        assertThat(listenerMethodMap.get(e3),
                containsInAnyOrder(cb_pl_1));
    }

    @Test
    public void testCbDirtyMethodFlag() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 3);
        RootCB eRoot = new RootCB("eRoot");
        ParentUpdateListener pl_1 = new ParentUpdateListener("pl_1");
        DirtyNotifierNode dirty_2 = new DirtyNotifierNode("dirty_2");
        InitCB i3 = new InitCB("i3");

        pl_1.parents = new Object[]{dirty_2, e2};
        dirty_2.parents = new Object[]{e1, e2, i3};
        i3.parents = new Object[]{e3};
        eRoot.parents = new Object[]{pl_1, i3};

        List<Object> nodeList = Arrays.asList(eRoot, e1, pl_1, dirty_2, e2, e3, i3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel(true);
        //
        Method method = DirtyNotifierNode.class.getMethod("onEvent");
        CbMethodHandle handle = new CbMethodHandle(method, dirty_2, "who_knows");
        DirtyFlag dirtyFlag = sep.getDirtyFlagForUpdateCb(handle);
        assertEquals(dirty_2, dirtyFlag.node.instance);

        method = ParentUpdateListener.class.getMethod("onEvent");
        handle = new CbMethodHandle(method, pl_1, "who_knows");
        dirtyFlag = sep.getDirtyFlagForUpdateCb(handle);
        assertEquals(pl_1, dirtyFlag.node.instance);
    }

}
