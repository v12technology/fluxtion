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

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class NodeWithPrivateParentList {

    private ArrayList<EventHandlerCb> parents;
    public int parentUpdateCount = 0;
    public int onEventCount = 0;
    public List<String> stringList;
    public List<Integer> intList;

    public NodeWithPrivateParentList() {
        this(null);
    }
    
    public NodeWithPrivateParentList(EventHandlerCb... cbs) {
        parents = new ArrayList<>();
        if (cbs != null) {
            parents.addAll(Arrays.asList(cbs));
        }
        stringList = new ArrayList<>();
        intList = new ArrayList<>();
    }

    public ArrayList<EventHandlerCb> getParents() {
        return parents;
    }

    @OnParentUpdate
    public void parentChanged(EventHandlerCb updated) {
        parentUpdateCount++;
    }
    
    @OnEvent
    public void onEvent(){
        onEventCount++;
    }
}
