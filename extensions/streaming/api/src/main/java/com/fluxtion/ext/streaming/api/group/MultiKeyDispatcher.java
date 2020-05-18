/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api.group;

import com.fluxtion.api.StaticEventProcessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Groups together StaticEventProcessor that have a shared MultiKey in a GroupBy
 * node. If there are multiple StaticEventProcessor of the same type in
 * different partitions this will index the StaticEventProcessor's and only
 * dispatch events that match the multikey to the relevant instances.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
public class MultiKeyDispatcher<T extends StaticEventProcessor> implements StaticEventProcessor {

    private final List<T> processorList;
    private final Function<T, GroupBy> groupbyAccessor;
    private MultiKey key;
    private Map<MultiKey, Collection<StaticEventProcessor>> multiMap = new HashMap<>();

    public MultiKeyDispatcher(Function<T, GroupBy> groupbyAccessor) {
        processorList = new ArrayList<>();
        this.groupbyAccessor = groupbyAccessor;
    }

    public void index() {
        processorList.forEach((T r) -> {
            GroupBy group = groupbyAccessor.apply(r);
            Map<MultiKey, ?> x = group.getMap();
            x.keySet().forEach((MultiKey k) -> {
                Collection<StaticEventProcessor> list = multiMap.get(k);
                if(list==null){
                    list = new ArrayList<>();
                    multiMap.put(k, list);
                }
                list.add(r);
                if (key == null) {
                    key = (MultiKey) k.copyKey();
                    key.reset();
                }
            });
        });
    }

    public void addToIndex(T newCalc) {
        processorList.add(newCalc);
    }

    @Override
    public void onEvent(Object event) {
        if (key == null) {
            return;
        }
        key.calculateKey(event);
        final Collection<StaticEventProcessor> matchingProcessors = multiMap.getOrDefault(key, Collections.EMPTY_SET);
        matchingProcessors.forEach(s -> s.onEvent(event));
    }

    public void publish(Object event) {
        processorList.forEach(s -> s.onEvent(event));
    }
}
