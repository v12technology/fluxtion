/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.creator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author gregp
 */
public class Node extends TypeDefinition {

    private String id;
    private boolean publicAccess;
    //events
    private List<EventMethod> events;
    private List<ReferenceDefinition> nodes;
    //config for factory created nodes
    private Object configBean;
    private String factoryType;

    public Node() {
        this(null, null, false);
    }

    public Node(String fqn, String id, boolean publicAccess) {
        super(fqn);
        this.id = id;
        this.publicAccess = publicAccess;
        packageName = ClassUtils.getPackageCanonicalName(fqn);
        className = ClassUtils.getShortCanonicalName(fqn);
        events = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    public Node(String fqn, String id) {
        this(fqn, id, false);
    }

    public <T> Class<T> getNodeClass() throws ClassNotFoundException {
        return (Class<T>) Class.forName(getType());
    }

    public List<EventMethod> getEvents() {
        return events;
    }

    public void setEvents(List<EventMethod> events) {
        this.events = events;
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ReferenceDefinition> getNodes() {
        return nodes;
    }

    public void setNodes(List<ReferenceDefinition> nodes) {
        this.nodes = nodes;
    }

    public void setRef(String nodeId, String fieldName) {
        ReferenceDefinition rd = new ReferenceDefinition();
        rd.setNode(nodeId);
        rd.setName(fieldName);
        nodes.add(rd);
    }

    public <T> T getConfigBean() {
        return (T) configBean;
    }

    public <T> void setConfigBean(T configBean) {
        this.configBean = configBean;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }
    
    public boolean isFactoryCreated(){
        return factoryType != null;
    }
    
    @Override
    public String toString() {
        return "Node{"
                       + "id=" + id
                       + ", publicAccess=" + publicAccess
                       + ", events=" + events
                       + ", nodes=" + nodes
                       + '}';
    }

}
