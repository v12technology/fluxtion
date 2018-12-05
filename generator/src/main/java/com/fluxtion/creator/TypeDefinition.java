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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.creator;

import org.apache.commons.lang3.ClassUtils;

/**
 * Base class for a definition of a type.
 * 
 * @author gregp
 */
public class TypeDefinition {
   
    protected String type;
    protected String className;
    protected String packageName;

    public TypeDefinition(){
        
    }
    
    public TypeDefinition(String fqn) {
        this.type = fqn;
        packageName = ClassUtils.getPackageCanonicalName(fqn);
        className = ClassUtils.getShortCanonicalName(fqn);
    }

    public void setType(String type) {
        this.type = type;
        packageName = ClassUtils.getPackageCanonicalName(type);
        className = ClassUtils.getShortCanonicalName(type);
    }

    public String getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    } 
    
    public boolean isFunction(){
        return false;
    }

    @Override
    public String toString() {
        return "TypeDefinition{" + "type=" + type + '}';
    }
    
}
