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

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class NodeWithPrivateParentList {

    private ArrayList<EventHandlerCbNode> parents;
    public int parentUpdateCount = 0;
    public int onEventCount = 0;
    public List<String> stringList;
    public List<Integer> intList;

    public NodeWithPrivateParentList() {
        parents = new ArrayList<>();
        stringList = new ArrayList<>();
        intList = new ArrayList<>();
    }

    public NodeWithPrivateParentList(EventHandlerCbNode... cbs) {
        parents = new ArrayList<>();
        if (cbs != null) {
            parents.addAll(Arrays.asList(cbs));
        }
        stringList = new ArrayList<>();
        intList = new ArrayList<>();
    }

    public ArrayList<EventHandlerCbNode> getParents() {
        return parents;
    }

    @OnParentUpdate
    public void parentChanged(EventHandlerCbNode updated) {
        parentUpdateCount++;
    }

    @OnTrigger
    public boolean onEvent() {
        onEventCount++;
        return true;
    }
}
