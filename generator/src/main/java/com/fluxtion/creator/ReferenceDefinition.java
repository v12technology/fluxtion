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

/**
 *
 * @author gregp
 */
public class ReferenceDefinition {

    private String name;
    private String node;
    private TypeDefinition type;
    
    public ReferenceDefinition() {
    }

    public ReferenceDefinition(String fieldName, TypeDefinition type) {
        this(fieldName, type.getType());
    }
    
    public ReferenceDefinition(String fieldName, String typeFqn) {
        this.name = fieldName;
        this.type = new TypeDefinition(typeFqn);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return "ReferenceDefinition{" + "name=" + name + ", node=" + node + ", type=" + type + '}';
    }

}
