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
package com.fluxtion.test.event;

import com.fluxtion.api.annotations.OnEvent;

/**
 *
 * @author Greg Higgins
 */
public class DirtyNotifierNode {
   public Object[] parents;
   public String id;
    

    public DirtyNotifierNode(String id) {
        this.id = id;
    }

    public DirtyNotifierNode(String id, Object... parents) {
        this.parents = parents;
        this.id = id;
    }
    

    public DirtyNotifierNode() {
    }
    
    @OnEvent
    public boolean onEvent() {
        return true;
    }


    
    @Override
    public String toString() {
        return "DirtyNotifierNode{" + "id=" + id + '}';
    }
}
