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

import java.util.Objects;

/**
 * A filter class for a parent class, a match will occur if both instances are
 * equal or both instances are null and the class types are equal.
 * 
 * @author Greg Higgins
 */
public class ParentFilter {
    
    public Class parentType;
    public String parentName;
    public CbMethodHandle callBack;

    public ParentFilter(Class parentType, String parentName, CbMethodHandle callBack) {
        this.parentType = parentType;
        this.parentName = parentName;
        this.callBack = callBack;
    }
   
    public boolean match(ParentFilter other){
        if(other.parentName==null || parentName==null || other.parentName.length()==0 || parentName.length()==0){
            return parentType.isAssignableFrom(other.parentType);
        }
        
        if(other.parentName!=null & parentName!=null){
            return other.parentName.equals(parentName) && other.parentType.isAssignableFrom(parentType);
        }
        return false;
    }
    public boolean exactmatch(ParentFilter other){
        if(other.parentName==null & parentName==null){
            return parentType == (other.parentType);
        }
        
        if(other.parentName!=null & parentName!=null){
            return other.parentName.equals(parentName) && other.parentType == parentType;
        }
        return false;
    }
    
}
