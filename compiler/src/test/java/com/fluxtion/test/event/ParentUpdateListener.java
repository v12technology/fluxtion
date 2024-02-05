/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.test.event;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.OnParentUpdate;

/**
 * @author Greg Higgins
 */
public class ParentUpdateListener {
    public Object[] parents;
    public String id;
    public Object parentUpdated;


    public ParentUpdateListener(String id) {
        this.id = id;
    }

    public ParentUpdateListener() {
    }

    @OnTrigger
    public boolean onEvent() {
        parentUpdated = null;
        return true;
    }

    @OnParentUpdate
    public void parentChanged(Object parent) {
        this.parentUpdated = parent;
        //System.out.println("parent changed:" + parent);
    }

    @Override
    public String toString() {
        return "ParentUpdateListener{" + "id=" + id + '}';
    }
}
