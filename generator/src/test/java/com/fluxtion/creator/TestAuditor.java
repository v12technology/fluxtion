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
package com.fluxtion.creator;

import static org.junit.Assert.assertThat;

import com.fluxtion.api.audit.Auditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;

/**
 *
 * @author gregp
 */
public class TestAuditor implements Auditor {

    private ArrayList<Class> eventStore;
    private ArrayList<CallbackAudit> callBackStore;
    private HashMap<String, Object> nodeMap;
    private boolean processingComplete;

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        nodeMap.put(nodeName, node);
    }

    @Override
    public void eventReceived(Object event) {
        eventStore.add(event.getClass());
        processingComplete = false;
//        System.out.printf("eventReceived:%s%n", event);
    }

    @Override
    public void processingComplete() {
//        System.out.println("processingComplete");
        processingComplete = true;
    }

    @Override
    public void init() {
        nodeMap = new HashMap<>();
        eventStore = new ArrayList<>();
        callBackStore = new ArrayList<>();
        processingComplete = false;
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void nodeInvoked(Object node, String nodeName, String methodName, Object event) {
//        System.out.printf("nodeInvoked:%s method:%s event:%s%n", nodeName, methodName, event);
        callBackStore.add(new CallbackAudit(node, nodeName, methodName, event));
    }

    @Override
    public boolean auditInvocations() {
        return true;
    }

    public void matchRegisteredNodes(String... nodes) {
        assertThat(nodeMap.keySet(), IsIterableContainingInAnyOrder.containsInAnyOrder(nodes));
    }

    public void matchEvents(Class... events) {
        assertThat(eventStore, IsIterableContainingInOrder.contains(events));
    }

    /**
     * Matches a subset of methods from the callback recorded. The callback list
     * must contain the supplied methods and the order they are supplied in.
     *
     * @param methods
     */
    public void matchCallbackMethodOrderPartial(String... methods) {

        List<String> methodList = callBackStore.stream()
                .map(CallbackAudit::getMethodName)
                .collect(Collectors.toList());
        int idx = 0;
        for (String method : methods) {
            int findIdx = methodList.subList(idx, methodList.size()).indexOf(method) + idx;
            if (findIdx < 0) {
                throw new AssertionError("missing items expected:"
                        + method + " in list " + methodList);
            }
            if (findIdx < idx) {
                throw new AssertionError("missing items expected:"
                        + Arrays.toString(methods) + " in list " + methodList
                        + " index:" + idx
                );
            }
            idx++;
        }
    }

    public void matchCallbackMethod(String... methods) {

        List<String> methodList = callBackStore.stream()
                .map(CallbackAudit::getMethodName)
                .collect(Collectors.toList());
        assertThat(methodList, IsIterableContainingInOrder.contains(methods));
    }

    public boolean isProcessingComplete() {
        return processingComplete;
    }
    
    public void clearEventStores() {
        callBackStore.clear();
        eventStore.clear();
    }

    private static class CallbackAudit {

        Object node;
        Class nodeClass;
        String nodeName;
        String methodName;
        Object event;
        Class eventClass;

        public CallbackAudit(Object node, String nodeName, String methodName, Object event) {
            this.node = node;
            this.nodeName = nodeName;
            this.methodName = methodName;
            this.event = event;
            //derived
            this.nodeClass = node.getClass();
            this.eventClass = event.getClass();
        }

        public Object getNode() {
            return node;
        }

        public void setNode(Object node) {
            this.node = node;
        }

        public Class getNodeClass() {
            return nodeClass;
        }

        public void setNodeClass(Class nodeClass) {
            this.nodeClass = nodeClass;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Object getEvent() {
            return event;
        }

        public void setEvent(Object event) {
            this.event = event;
        }

        public Class getEventClass() {
            return eventClass;
        }

        public void setEventClass(Class eventClass) {
            this.eventClass = eventClass;
        }

    }

}
