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

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.test.event.AnnotatedTimeHandler;
import com.fluxtion.test.event.AnnotatedTimeHandlerNoFilter;
import com.fluxtion.test.event.DependencyChild;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.InitCB;
import com.fluxtion.test.event.NodeWithParentList;
import com.fluxtion.test.event.RootCB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class TopologicallySortedDependencyGraphTest {

    /**
     * Test of getSortedDependents method, of class
     * TopologicallySortedDependecyGraph.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testOrderedCallGraph() throws Exception {
        //System.out.println("orderedCallGraph");
        DependencyChild[] allNumbers = new DependencyChild[11];
        List<Object> nodeList = new ArrayList<>();

        DependencyChild prevChild = new DependencyChild(0, "0");
        nodeList.add(prevChild);

        for (int i = 1; i < allNumbers.length; i++) {
            final DependencyChild child = new DependencyChild(i, "" + i, prevChild);
            allNumbers[i] = child;
            nodeList.add(child);
            prevChild = child;
        }

        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(nodeList);
        instance.generateDependencyTree();

        for (int i = 1; i < allNumbers.length; i++) {
            assertThat(instance.getSortedDependents(allNumbers[i]),
                    IsIterableContainingInOrder.contains(Arrays.copyOfRange(allNumbers, i, allNumbers.length)));
        }

    }
    
    /**
     * test inserting nodes via a Map.
     * 
     * @throws Exception 
     */
    @Test
    public void testOrderedDefinedVarNames() throws Exception {
        //System.out.println("testOrderedDefinedVarNames");
        DependencyChild[] allNumbers = new DependencyChild[11];
        Map<Object, String> nodeMap = new HashMap<>();

        DependencyChild prevChild = new DependencyChild(0, "0");
        nodeMap.put(prevChild, "child_" + prevChild.id);

        for (int i = 1; i < allNumbers.length; i++) {
            final DependencyChild child = new DependencyChild(i, "" + i, prevChild);
            allNumbers[i] = child;
            nodeMap.put(child, "child_" + child.id);
            prevChild = child;
        }

        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeMap);
        graph.generateDependencyTree();

        for (int i = 1; i < allNumbers.length; i++) {
            assertThat(graph.getSortedDependents(allNumbers[i]),
                    IsIterableContainingInOrder.contains(Arrays.copyOfRange(allNumbers, i, allNumbers.length)));
        }
        
        Set nodes = nodeMap.keySet();
        for (Map.Entry<Object, String> entry: nodeMap.entrySet()) {
            Object inst = entry.getKey();
            String expectedName = entry.getValue();
            assertEquals(expectedName, graph.variableName(inst));
        }
        
    }

    /**
     * Test of generateDependencyTree method, of class
     * TopologicallySortedDependecyGraph.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubTreeFiltering() throws Exception {
        //System.out.println("subTreeFiltering");

        DependencyChild[] allNumbers = new DependencyChild[11];
        List<Object> nodeList = new ArrayList<>();

        for (int i = 0; i < allNumbers.length; i++) {
            final DependencyChild child = new DependencyChild(i, "" + i);
            allNumbers[i] = child;
            nodeList.add(child);
        }

        DependencyChild[] primeNums = new DependencyChild[]{allNumbers[1], allNumbers[2], allNumbers[3], allNumbers[5], allNumbers[7]};
        DependencyChild[] evenNums = new DependencyChild[]{allNumbers[2], allNumbers[4], allNumbers[6], allNumbers[8], allNumbers[10]};
        DependencyChild[] oddNums = new DependencyChild[]{allNumbers[1], allNumbers[3], allNumbers[5], allNumbers[7], allNumbers[9]};

        DependencyChild primes = new DependencyChild(0, "primes", primeNums);
        DependencyChild evens = new DependencyChild(0, "evens", evenNums);
        DependencyChild odds = new DependencyChild(0, "odds", oddNums);

        DependencyChild root = new DependencyChild(0, "root", new DependencyChild[]{primes, evens, odds, allNumbers[0]});

        nodeList.add(primes);
        nodeList.add(evens);
        nodeList.add(odds);
        nodeList.add(root);

        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(nodeList);
        instance.generateDependencyTree();

        assertThat(instance.getSortedDependents(allNumbers[0]),
                containsInAnyOrder(allNumbers[0], root));

        assertThat(instance.getSortedDependents(allNumbers[1]),
                containsInAnyOrder(allNumbers[1], primes, odds, root));

        assertThat(instance.getSortedDependents(allNumbers[2]),
                containsInAnyOrder(allNumbers[2], primes, evens, root));

        assertThat(instance.getSortedDependents(allNumbers[3]),
                containsInAnyOrder(allNumbers[3], primes, odds, root));

        assertThat(instance.getSortedDependents(allNumbers[4]),
                containsInAnyOrder(allNumbers[4], evens, root));

        assertThat(instance.getSortedDependents(allNumbers[5]),
                containsInAnyOrder(allNumbers[5], primes, odds, root));

        assertThat(instance.getSortedDependents(allNumbers[6]),
                containsInAnyOrder(allNumbers[6], evens, root));

        assertThat(instance.getSortedDependents(allNumbers[7]),
                containsInAnyOrder(allNumbers[7], primes, odds, root));

        assertThat(instance.getSortedDependents(allNumbers[8]),
                containsInAnyOrder(allNumbers[8], evens, root));

        assertThat(instance.getSortedDependents(allNumbers[9]),
                containsInAnyOrder(allNumbers[9], odds, root));

        assertThat(instance.getSortedDependents(allNumbers[10]),
                containsInAnyOrder(allNumbers[10], evens, root));

    }

    @Test
    public void testDirectChildren() throws Exception {
        //System.out.println("directChildren");
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
        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(nodeList);
        instance.generateDependencyTree();
        //
        assertThat(instance.getDirectChildren(i3),
                containsInAnyOrder(eRoot, i2));
        //
        assertThat(instance.getDirectChildren(i1),
                containsInAnyOrder(eRoot));
    }
    
    @Test
    public void testDisconnectedNodes() throws Exception{
        //System.out.println("testDisconnectedNodes");
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        AnnotatedTimeHandlerNoFilter noFilterEh = new AnnotatedTimeHandlerNoFilter();
        AnnotatedTimeHandler th = new AnnotatedTimeHandler(200);
        RootCB eRoot = new RootCB("eRoot", noFilterEh);
        List<Object> nodeList = Arrays.asList(eRoot, e1, noFilterEh, th, eRoot);
        //generate model
        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(nodeList);
        instance.generateDependencyTree();
        //
        assertEquals(4, instance.getSortedDependents().size());
        
    }

    @Test
    public void testDirectParents() throws Exception {
        //System.out.println("directParents");
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
        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(nodeList);
        instance.generateDependencyTree();
        //
        assertThat(instance.getDirectParents(i2),
                containsInAnyOrder(e1, e2, i3));
        //
        assertThat(instance.getDirectParents(eRoot),
                containsInAnyOrder(i1, i3));
    }
    
    @Test
    public void testDirectParentsShared() throws Exception {
        //System.out.println("directParents");
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb eshared = new EventHandlerCb("eshared", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 3);
        RootCB eRoot = new RootCB("eRoot");
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");

        i1.parents = new Object[]{e1, eshared};
        i2.parents = new Object[]{eshared, e3};
        eRoot.parents = new Object[]{i1, i2};

        List<Object> nodeList = Arrays.asList(eRoot, e1, i1, i2, e3, eshared);
        //generate model
        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(nodeList);
        instance.generateDependencyTree();
        //
        assertThat(instance.getDirectParents(i1),
                containsInAnyOrder(e1, eshared));
        //
        assertThat(instance.getDirectParents(i2),
                containsInAnyOrder(e3, eshared));
    }
    
    @Test
    public void testDirectParentsSharedFluentApi() throws Exception {
        //System.out.println("testDirectParentsSharedFluentApi");
        //
        SEPConfig config = new SEPConfig();
        
        //set up modes
        EventHandlerCb e1 = config.addNode(new EventHandlerCb("e1", 1));
        EventHandlerCb eshared = config.addNode(new EventHandlerCb("eshared", 2));
        EventHandlerCb e3 = config.addNode(new EventHandlerCb("e3", 3));
        RootCB eRoot = config.addNode(new RootCB("eRoot"));
        InitCB i1 = config.addNode(new InitCB("i1"));
        InitCB i2 = config.addNode(new InitCB("i2"));

        i1.parents = new Object[]{e1, eshared};
        i2.parents = new Object[]{eshared, e3};
        eRoot.parents = new Object[]{i1, i2};

        
        
//        GenerationContext context = new GenerationContext(config);
        //generate model
        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(config);
        instance.generateDependencyTree();
        //
        assertThat(instance.getDirectParents(i1),
                containsInAnyOrder(e1, eshared));
        //
        assertThat(instance.getDirectParents(i2),
                containsInAnyOrder(e3, eshared));
    }
    
    
    @Test
    public void testArrayListParents() throws Exception{
        //System.out.println("testArrayListParents");
        //
        SEPConfig config = new SEPConfig();
        //set up modes
        EventHandlerCb e1 = config.addNode(new EventHandlerCb("e1", 1));
        EventHandlerCb eshared = config.addNode(new EventHandlerCb("eshared", 2));
        EventHandlerCb e3 = config.addNode(new EventHandlerCb("e3", 3));
        NodeWithParentList eRoot = config.addNode(new NodeWithParentList(e1,eshared, e3));
        
        TopologicallySortedDependencyGraph instance = new TopologicallySortedDependencyGraph(config);
        instance.generateDependencyTree();
        assertThat(instance.getDirectParents(eRoot),
                containsInAnyOrder(e1, eshared, e3));
        assertThat(instance.getEventSortedDependents(e3),
                contains(e3, eRoot));
    }
}
