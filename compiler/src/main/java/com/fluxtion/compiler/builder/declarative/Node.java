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
package com.fluxtion.compiler.builder.declarative;

import com.fluxtion.compiler.builder.generation.GenerationContext;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author gregp
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
       Class<T> forName = null;
        try{
            forName = (Class<T>) Class.forName(getType());
       } catch(Exception e){
          forName = GenerationContext.SINGLETON.forName(getType());
       }
       return forName;
    }

    public void setRef(String nodeId, String fieldName) {
        nodes.add(new ReferenceDefinition(nodeId, fieldName));
    }

    public <T> T getConfigBean() {
        return (T) configBean;
    }

    public boolean isFactoryCreated() {
        return factoryType != null;
    }
}
