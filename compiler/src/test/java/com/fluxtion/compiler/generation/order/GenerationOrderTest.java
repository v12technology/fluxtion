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
package com.fluxtion.compiler.generation.order;

import com.fluxtion.compiler.builder.factory.NodeNameProducer;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Event;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sort siblings alphabetically so generation order is deterministic.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class GenerationOrderTest extends MultipleSepTargetInProcessTest {

    public GenerationOrderTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testOrder() {
        sep((c) -> {
            Node root = c.addNode(new Node("root"));
            c.addNode(new Node(root, "X"));
            c.addNode(new Node(root, "A2"));
            c.addNode(new Node(root, "Y"));
            c.addNode(new Node(root, "A1"));
        });
        OrderEvent oe = new OrderEvent();
        onEvent(oe);
        List<String> expected = Arrays.asList("root", "A1", "A2", "X", "Y");
        Assert.assertEquals(expected, oe.list);
    }

    public static class OrderEvent implements Event {

        public List<String> list = new ArrayList<>();
    }

    public static class Node {

        private final Node parent;
        private final String name;

        public Node(Node parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public Node(String name) {
            this(null, name);
        }

        @OnEventHandler
        public boolean update(OrderEvent e) {
            e.list.add(name);
            return true;
        }

    }

    public static class NodeNamingStrategy implements NodeNameProducer {

        @Override
        public String mappedNodeName(Object nodeToMap) {
            if (nodeToMap instanceof Node) {
                return ((Node) nodeToMap).name;
            }
            return null;
        }
    }

}
