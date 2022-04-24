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

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.event.Event;

/**
 *
 * @author Greg Higgins
 */
class EventListeners {

    public static class TestEventListener {

        @OnEventHandler
        public void onTestEvent(TestEvent event) {
        }
    }

    public static class ConfigEventListener {

        @OnEventHandler
        public void onConfigEvent(ConfigEvent event) {
        }
    }

    public static class ChildConfigEventListener extends ConfigEventListener{

        @OnEventHandler
        public void onChildConfigEvent(ChildConfigEvent event) {
        }
    }

    public static class UnknownTestEventListener {

        @OnEventHandler
        public void onTestEvent(TestEvent event) {
        }
    }

    public static class TestEvent implements Event {
    }

    public static class ConfigEvent implements Event {
    }

    public static class ChildConfigEvent extends ConfigEvent {
    }
    
    
    public static class NodeChild{
        
        public TestEventListener testEventSource;
        public ConfigEventListener configEventSource;
        public ConfigEventListener configEventSource2;
        public ChildConfigEventListener childConfigEventSource;
        public Object objectEventSource;

        public NodeChild() {
        }

        public NodeChild(TestEventListener testEventSource) {
            this.testEventSource = testEventSource;
        }

        public NodeChild(TestEventListener testEventSource, Object objectEventSource) {
            this.testEventSource = testEventSource;
            this.objectEventSource = objectEventSource;
        }
        
        public NodeChild(TestEventListener testEventSource, ConfigEventListener configEventSource, ConfigEventListener configEventSource2, ChildConfigEventListener childConfigEventSource, Object objectEventSource) {
            this.testEventSource = testEventSource;
            this.configEventSource = configEventSource;
            this.configEventSource2 = configEventSource2;
            this.childConfigEventSource = childConfigEventSource;
            this.objectEventSource = objectEventSource;
        }
        
    }
    
    public static class Node1ParentListener extends NodeChild{
        
        @OnParentUpdate
        public void onTestEventParent(TestEventListener parent){
            
        }

        public Node1ParentListener() {
        }

        public Node1ParentListener(TestEventListener testEventSource) {
            super(testEventSource);
        }

        public Node1ParentListener(TestEventListener testEventSource, Object objectEventSource) {
            super(testEventSource, objectEventSource);
        }

        public Node1ParentListener(TestEventListener testEventSource, ConfigEventListener configEventSource, ConfigEventListener configEventSource2, ChildConfigEventListener childConfigEventSource, Object objectEventSource) {
            super(testEventSource, configEventSource, configEventSource2, childConfigEventSource, objectEventSource);
        }
        
        
    }
    
    public static class Node1Parent1ObjectListener extends NodeChild{
        @OnParentUpdate
        public void onTestEvent_2(TestEventListener parent){
            
        }
        
        @OnParentUpdate
        public void onAnyUpdate(Object anyParent){
            
        }
    }
    
    
    
    public static class Node2ArrayParentListener extends NodeChild{
        public ConfigEventListener[] configEventSources;
        
        @OnParentUpdate
        public void onTestEventParent(TestEventListener parent){
            
        }
        
        @OnParentUpdate
        public void onConfigEvent(ConfigEventListener parent){
            
        }
        
        
    }
    
    public static class NodeNameFilterListener extends NodeChild{

       
        @OnParentUpdate("configEventSource")
        public void onConfigEvent(ConfigEventListener parent){
            
        }
        
       
        @OnParentUpdate("configEventSource2")
        public void onConfigEvent2(ConfigEventListener parent){
            
        }
        
        
    }

}
