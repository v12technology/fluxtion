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

import com.fluxtion.compiler.generation.targets.JavaSourceGenerator;
import com.fluxtion.test.event.EventHandlerCbNode;
import com.fluxtion.test.event.InitCB;
import com.fluxtion.test.event.RootCB;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Greg Higgins
 */
public class JavaSourceModelTest {

    @Test
    public void sortCbHandlerTest() throws Exception {
        //set up modes
        EventHandlerCbNode e1 = new EventHandlerCbNode("e1", 1);
        EventHandlerCbNode e2 = new EventHandlerCbNode("e2", 2);
        EventHandlerCbNode e3 = new EventHandlerCbNode("e3", 3);
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
        JavaSourceGenerator srcModel = new JavaSourceGenerator(sep);
        srcModel.buildSourceModel();

    }

}
