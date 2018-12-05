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
package com.fluxtion.test.event;

/**
 *
 * @author Greg Higgins
 */
public class DependencyChild {
    public int value;
    public String id;
    public DependencyChild[] parents;
    
    public DependencyChild(int value, String id, DependencyChild... dependency) {
        this.value = value;
        this.id = id;
        this.parents = dependency;
    }
    public DependencyChild(int value, String id) {
        this.value = value;
        this.id = id;
        this.parents = null;
    }

    public DependencyChild() {
    }

    @Override
    public String toString() {
        return "DependencyChild{" + "value=" + value + ", id=" + id + '}';
    }
    
    
}
