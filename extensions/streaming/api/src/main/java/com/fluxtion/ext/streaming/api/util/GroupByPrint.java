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
package com.fluxtion.ext.streaming.api.util;

import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class GroupByPrint {

    public static void printFrequencyMap(String title, GroupBy<Number> groupBy) {
        System.out.println(title);
        groupBy.getMap().entrySet().stream().forEach(e -> System.out.println(e.getKey().toString() + ":" + e.getValue().event().intValue()));
        System.out.println("");
    }

    public static <K> void printTopN(String title, GroupBy<Number> groupBy, int n) {
        System.out.println(title);
        Map<?, Wrapper<Number>> map = groupBy.getMap();
        ArrayList<Map.Entry<?, Wrapper<Number>>> list = new ArrayList<>(map.entrySet());
        list.sort((e1, e2) -> e2.getValue().event().intValue() - e1.getValue().event().intValue());
        int count = Math.min(n, list.size());
        for (int i = 0; i < count; i++) {
            Map.Entry<? extends Object, Wrapper<Number>> get = list.get(i);
            System.out.println(get.getKey() + ":" + get.getValue().event().intValue());
        }
        System.out.println("");
    }
}
