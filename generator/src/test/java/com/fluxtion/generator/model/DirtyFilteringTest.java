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

import com.fluxtion.test.event.AnnotatedEventHandlerDirtyNotifier;
import com.fluxtion.test.event.DirtyNotifierNode;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.InitCB;
import com.fluxtion.test.event.RootCB;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class DirtyFilteringTest {

    public DirtyFilteringTest() {
    }

    @Test
    public void testGetNodeGuardConditions() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        InitCB i3 = new InitCB("i3");
        DirtyNotifierNode dirty_1 = new DirtyNotifierNode("dirty_1");
        DirtyNotifierNode dirty_2 = new DirtyNotifierNode("dirty_2");
        DirtyNotifierNode dirty_3 = new DirtyNotifierNode("dirty_3");
        RootCB eRoot = new RootCB("eRoot");

        dirty_1.parents = new Object[]{e1};
        dirty_2.parents = new Object[]{e2};
        dirty_3.parents = new Object[]{i1, i3};
        i1.parents = new Object[]{e1};
        i2.parents = new Object[]{dirty_1, dirty_2};
        i3.parents = new Object[]{e2};
        eRoot.parents = new Object[]{dirty_3};

        List<Object> nodeList = Arrays.asList(eRoot, e1, e2, dirty_1, dirty_2, dirty_3, i1, i2, i3);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel(true);
        //
        //System.out.println(sep.getDirtyFieldMap());

        sep.getFieldForInstance(dirty_1);

        System.out.println("flags:" + sep.getDirtyFieldMap().keySet());
        sep.getDirtyFieldMap().keySet().stream().map((Field c) -> c.name).forEach(System.out::println);
        assertThat(sep.getDirtyFieldMap().keySet(), containsInAnyOrder(
                sep.getFieldForInstance(e1),
                sep.getFieldForInstance(e2),
                sep.getFieldForInstance(i1),
                sep.getFieldForInstance(i2),
                sep.getFieldForInstance(i3),
                sep.getFieldForInstance(dirty_1),
                sep.getFieldForInstance(dirty_2),
                sep.getFieldForInstance(dirty_3)
        ));

        assertTrue(sep.getNodeGuardConditions(e1).isEmpty());
        assertTrue(sep.getNodeGuardConditions(e2).isEmpty());
        assertThat(sep.getNodeGuardConditions(i1), containsInAnyOrder(
                sep.getDirtyFlagForNode(e1)
        ));
        assertThat(sep.getNodeGuardConditions(i2), containsInAnyOrder(
                sep.getDirtyFlagForNode(dirty_1),
                sep.getDirtyFlagForNode(dirty_2)
        ));
        assertThat(sep.getNodeGuardConditions(i3), containsInAnyOrder(
                sep.getDirtyFlagForNode(e2)
        ));
        assertThat(sep.getNodeGuardConditions(dirty_1), containsInAnyOrder(
                sep.getDirtyFlagForNode(e1)
        ));
        assertThat(sep.getNodeGuardConditions(dirty_2), containsInAnyOrder(
                sep.getDirtyFlagForNode(e2)
        ));
        assertThat(sep.getNodeGuardConditions(dirty_3), containsInAnyOrder(
                sep.getDirtyFlagForNode(i1),
                sep.getDirtyFlagForNode(i3)
        ));
        assertThat(sep.getNodeGuardConditions(eRoot), containsInAnyOrder(
                sep.getDirtyFlagForNode(dirty_3)
        ));
    }

    /**
     * A test for proving guard conditions are transferred through nodes when
     * the interstitial node does not support dirty filtering
     */
    @Test
    public void testGuardConditionDiscontinuous() throws Exception {
        //set up modes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        InitCB i3 = new InitCB("i3");
        InitCB i4 = new InitCB("i4");
        InitCB i5 = new InitCB("i5");
        InitCB i6 = new InitCB("i6");

        DirtyNotifierNode dirty_1 = new DirtyNotifierNode("dirty_1");
        DirtyNotifierNode dirty_2 = new DirtyNotifierNode("dirty_2");

        dirty_1.parents = new Object[]{e1};
        dirty_2.parents = new Object[]{e2};

        i1.parents = new Object[]{e2};
        i2.parents = new Object[]{dirty_1};
        i3.parents = new Object[]{dirty_1, dirty_2};
        i4.parents = new Object[]{i1, i3};
        i5.parents = new Object[]{i2};
        i6.parents = new Object[]{i3};

        List<Object> nodeList = Arrays.asList(e1, e2,
                dirty_1, dirty_2,
                i1, i2, i3, i4, i5, i6);
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(nodeList);
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel(true);

        assertThat(sep.getNodeGuardConditions(i5), containsInAnyOrder(
                sep.getDirtyFlagForNode(i2)
        ));

        assertThat(sep.getNodeGuardConditions(i6), containsInAnyOrder(
                sep.getDirtyFlagForNode(i3)
        ));
    }

    @Test
    public void testEventHandlerWithDirtySupport() throws Exception {
        AnnotatedEventHandlerDirtyNotifier eh = new AnnotatedEventHandlerDirtyNotifier();
        DirtyNotifierNode dirty_1 = new DirtyNotifierNode("dirty_1", eh);
        
        //generate model
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(Arrays.asList(eh, dirty_1));
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel(true);
        
        assertThat(sep.getNodeGuardConditions(dirty_1), containsInAnyOrder(
                sep.getDirtyFlagForNode(eh)
        ));
    }
}
