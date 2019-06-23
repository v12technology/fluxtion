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
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.ext.streaming.api.Stateful;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ListCollector implements Stateful {
        
    private List list;

    public ListCollector() {
        list = new ArrayList();
    }

    public List addItem(Object o) {
        list.add(o);
        return list;
    }

    public <T> List<T> getList() {
        return list;
    }

    @Override
    public void reset() {
        list.clear();
    }
    
}
