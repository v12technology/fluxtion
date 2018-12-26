/*
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.example.core.dependencyinjection.reflection;

import com.fluxtion.api.annotations.OnEvent;

/**
 *
 * @author V12 Technology Ltd.
 */
public class FactoryNode {
    
    private final Object parent;
    private int limit;
    private transient double transientValue;

    public FactoryNode(Object parent) {
        this.parent = parent;
    }
    
    public static final FactoryNode build(Object parent, int limit){
        FactoryNode node = new FactoryNode(parent);
        node.limit = limit;
        node.transientValue = limit * 100;
        return node;
    }

    public Object getParent() {
        return parent;
    }

    public int getLimit() {
        return limit;
    }
    
    @OnEvent
    public void onEvent(){
        
    }
    
}
