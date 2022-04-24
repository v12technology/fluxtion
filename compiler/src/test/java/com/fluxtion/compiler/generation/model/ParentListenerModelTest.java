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
package com.fluxtion.compiler.generation.model;

import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.generation.model.EventListeners.ChildConfigEventListener;
import com.fluxtion.compiler.generation.model.EventListeners.ConfigEventListener;
import com.fluxtion.compiler.generation.model.EventListeners.Node1Parent1ObjectListener;
import com.fluxtion.compiler.generation.model.EventListeners.Node1ParentListener;
import com.fluxtion.compiler.generation.model.EventListeners.Node2ArrayParentListener;
import com.fluxtion.compiler.generation.model.EventListeners.NodeNameFilterListener;
import com.fluxtion.compiler.generation.model.EventListeners.TestEventListener;
import com.fluxtion.compiler.generation.model.EventListeners.UnknownTestEventListener;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Greg Higgins
 */
public class ParentListenerModelTest {

    private SimpleEventProcessorModel sep;
    private TopologicallySortedDependencyGraph graph;

    @Before
    public void before() {
        sep = null;
        graph = null;
    }

    @Test
    public void testParent_1() throws Exception {
        //System.out.println("testParent_1");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        node1.testEventSource = test1Listener;
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_pl_1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_pl_1));

    }

    @Test
    public void testParent_1a() throws Exception {
        //System.out.println("testParent_1a");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        ConfigEventListener configListener = config.addNode(new ConfigEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        node1.testEventSource = test1Listener;
        node1.configEventSource = configListener;
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        assertEquals(0, listenerMethodMap.get(configListener).size());
        CbMethodHandle cb_pl_1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_pl_1));

    }

    @Test
    public void testParent_2() throws Exception {
        //System.out.println("testParent_2");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        Node1Parent1ObjectListener node2 = config.addNode(new Node1Parent1ObjectListener());
        node1.testEventSource = test1Listener;
        node2.testEventSource = test1Listener;
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onTestEvent_2", TestEventListener.class, node2);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_node1, cb_node2));

    }

    @Test
    public void testParent_3() throws Exception {
        //System.out.println("testParent_3");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        Node1Parent1ObjectListener node2 = config.addNode(new Node1Parent1ObjectListener());
        node1.testEventSource = test1Listener;
        node2.objectEventSource = test1Listener;
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onTestEvent_2", TestEventListener.class, node2);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_node1, cb_node2));

    }

    @Test
    public void testParent_4() throws Exception {
        //System.out.println("testParent_4");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        UnknownTestEventListener unknownListener = config.addNode(new UnknownTestEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        Node1Parent1ObjectListener node2 = config.addNode(new Node1Parent1ObjectListener());
        node1.testEventSource = test1Listener;
        node2.testEventSource = test1Listener;
        node2.objectEventSource = unknownListener;
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onTestEvent_2", TestEventListener.class, node2);
        CbMethodHandle cb_node2_any = getCbHandle("onAnyUpdate", Object.class, node2);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_node1, cb_node2));
        assertThat(listenerMethodMap.get(unknownListener),
                containsInAnyOrder(cb_node2_any));

    }

    @Test
    public void testParent_5() throws Exception {
        //System.out.println("testParent_5");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        Node1Parent1ObjectListener node2 = config.addNode(new Node1Parent1ObjectListener());
        node1.testEventSource = test1Listener;
        node2.testEventSource = test1Listener;
        node2.objectEventSource = test1Listener;
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onTestEvent_2", TestEventListener.class, node2);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_node1, cb_node2));

    }

    @Test
    public void testParent_6() throws Exception {
        //System.out.println("testParent_6");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        ConfigEventListener configListener = config.addNode(new ConfigEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        Node2ArrayParentListener node2 = config.addNode(new Node2ArrayParentListener());

        node1.testEventSource = test1Listener;
        node2.configEventSources = new ConfigEventListener[]{configListener};
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onConfigEvent", ConfigEventListener.class, node2);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_node1));
        assertThat(listenerMethodMap.get(configListener),
                containsInAnyOrder(cb_node2));

    }

    @Test
    public void testParent_7() throws Exception {
        //System.out.println("testParent_7");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        ConfigEventListener configListener = config.addNode(new ConfigEventListener());
        ConfigEventListener configListener2 = config.addNode(new ConfigEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        Node2ArrayParentListener node2 = config.addNode(new Node2ArrayParentListener());

        node1.testEventSource = test1Listener;
        node2.configEventSource = configListener2;
        node2.configEventSources = new ConfigEventListener[]{configListener};
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onConfigEvent", ConfigEventListener.class, node2);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_node1));
        assertThat(listenerMethodMap.get(configListener),
                containsInAnyOrder(cb_node2));
        assertThat(listenerMethodMap.get(configListener2),
                containsInAnyOrder(cb_node2));

    }

    @Test
    public void testParent_8() throws Exception {
        //System.out.println("testParent_8");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        ConfigEventListener configListener = config.addNode(new ConfigEventListener());
        ChildConfigEventListener childConfigListener = config.addNode(new ChildConfigEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener());
        Node2ArrayParentListener node2 = config.addNode(new Node2ArrayParentListener());

        node1.testEventSource = test1Listener;
        node2.configEventSource = childConfigListener;
        node2.configEventSources = new ConfigEventListener[]{configListener};
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onTestEventParent", TestEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onConfigEvent", ConfigEventListener.class, node2);
        assertThat(listenerMethodMap.get(test1Listener),
                containsInAnyOrder(cb_node1));
        assertThat(listenerMethodMap.get(configListener),
                containsInAnyOrder(cb_node2));
        assertThat(listenerMethodMap.get(childConfigListener),
                containsInAnyOrder(cb_node2));

    }

    @Test
    public void testParent_9() throws Exception {
        //System.out.println("testParent_9");
        //set up modes
        SEPConfig config = new SEPConfig();
        ConfigEventListener configListener = config.addNode(new ConfigEventListener());
        ConfigEventListener configListener2 = config.addNode(new ConfigEventListener());
        NodeNameFilterListener node1 = config.addNode(new NodeNameFilterListener());

        node1.configEventSource = configListener;
        node1.configEventSource2 = configListener2;
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        CbMethodHandle cb_node1 = getCbHandle("onConfigEvent", ConfigEventListener.class, node1);
        CbMethodHandle cb_node2 = getCbHandle("onConfigEvent2", ConfigEventListener.class, node1);
        assertThat(listenerMethodMap.get(configListener),
                containsInAnyOrder(cb_node1));
        assertThat(listenerMethodMap.get(configListener2),
                containsInAnyOrder(cb_node2));

    }

    @Test
    public void testParent_10() throws Exception {
        //System.out.println("testParent_10");
        //set up modes
        SEPConfig config = new SEPConfig();
        TestEventListener test1Listener = config.addNode(new TestEventListener());
        Node1ParentListener node1 = config.addNode(new Node1ParentListener(test1Listener));
        Node1ParentListener node2 = config.addNode(new Node1ParentListener(test1Listener, node1));
        Node1ParentListener node3 = config.addNode(new Node1ParentListener(test1Listener, node2));
        //test
        final Map<Object, List<CbMethodHandle>> listenerMethodMap = generateListnerMap(config);
        assertThat(listenerMethodMap.get(test1Listener),
                contains(
                        getCbHandle("onTestEventParent", TestEventListener.class, node1),
                        getCbHandle("onTestEventParent", TestEventListener.class, node2),
                        getCbHandle("onTestEventParent", TestEventListener.class, node3)
                ));
    }

    private Map<Object, List<CbMethodHandle>> generateListnerMap(SEPConfig config) throws Exception {
        graph = new TopologicallySortedDependencyGraph(config);
        sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();
        return sep.getParentUpdateListenerMethodMap();
    }

    private CbMethodHandle getCbHandle(String methodName, Class parentClass, Object listener) throws NoSuchMethodException {
        return new CbMethodHandle(listener.getClass().getMethod(methodName, parentClass), listener, graph.variableName(listener));
    }
}
