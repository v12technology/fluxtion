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

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.streaming.api.SepContext;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.Map;

/**
 * Printing utilities for {@link Number} based {@link GroupBy} maps.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class GroupByPrint {

    /**
     * print sorted frequency map, sorted by value largest to smallest. The
     * value of the map is the count used to sort.
     *
     * @param <K>     key type
     * @param <N>     value type extends Number
     * @param title   Title to print before printing map contents
     * @param groupBy The map holding the frequency data
     * @param trigger Print when this trigger fires
     */
    public static <K, N extends Number> void printFrequencyMap(String title, GroupBy<K, N> groupBy, Object trigger) {
        SepContext.service().add(new PrintFrequencyMap<>(title, groupBy, trigger));
    }

    /**
     * print top N of a sorted frequency map, sorted by value largest to
     * smallest. The value of the map is the count used to sort.
     *
     * @param <K>     key type
     * @param <N>     value type extends Number
     * @param title   Title to print before printing map contents
     * @param n       the limit to print
     * @param groupBy The map holding the frequency data
     * @param trigger Print when this trigger fires
     */
    public static <K, N extends Number> void printTopN(String title, int n, GroupBy<K, N> groupBy, Object trigger) {
        SepContext.service().add(new PrintTopN(title, n, groupBy, trigger));
    }

    public static <K, N> void printValues(String title, GroupBy<K, N> groupBy, Object trigger) {
        SepContext.service().add(new PrintGroupByValues(title, groupBy, trigger));

    }

    public static class PrintGroupByValues {

        private final String title;
        @NoEventReference
        private final GroupBy groupBy;
        private final Object trigger;

        public PrintGroupByValues(String title, GroupBy groupBy, Object trigger) {
            this.title = title == null ? "" : title;
            this.groupBy = groupBy;
            this.trigger = trigger;
        }

        @OnEvent
        public boolean printValues() {
            if (!title.isEmpty()) {
                System.out.println(title);
            }
            Map<?, Wrapper> map = groupBy.getMap();
            ArrayList<Map.Entry<?, Wrapper>> list = new ArrayList<>(map.entrySet());
            for (int i = 0; i < list.size(); i++) {
                Map.Entry<? extends Object, Wrapper> get = list.get(i);
                System.out.println(get.getValue().event().toString());
            }
            System.out.println("");
            return false;
        }

    }

    @AllArgsConstructor
    public static class PrintFrequencyMap<N extends Number> {

        private final String title;
        @NoEventReference
        private final GroupBy<?, N> groupBy;
        private final Object trigger;

        @OnEvent
        public boolean printFreqMap() {
            System.out.println(title);
            Map<?, Wrapper<N>> map = groupBy.getMap();
            ArrayList<Map.Entry<?, Wrapper<N>>> list = new ArrayList<>(map.entrySet());
            list.sort((e1, e2) -> e2.getValue().event().intValue() - e1.getValue().event().intValue());
            for (int i = 0; i < list.size(); i++) {
                Map.Entry<? extends Object, Wrapper<N>> get = list.get(i);
                System.out.println(get.getKey() + ":" + get.getValue().event().intValue());
            }
            System.out.println("");
            return false;
        }

    }

    @AllArgsConstructor
    public static class PrintTopN<N extends Number> {

        private final String title;
        private final int n;
        @NoEventReference
        private final GroupBy<?, N> groupBy;
        private final Object trigger;


        @OnEvent
        public boolean printTopN() {
            System.out.println(title);
            Map<?, Wrapper<N>> map = groupBy.getMap();
            ArrayList<Map.Entry<?, Wrapper<N>>> list = new ArrayList<>(map.entrySet());
            list.sort((e1, e2) -> e2.getValue().event().intValue() - e1.getValue().event().intValue());
            int count = Math.min(n, list.size());
            for (int i = 0; i < count; i++) {
                Map.Entry<? extends Object, Wrapper<N>> get = list.get(i);
                System.out.println(get.getKey() + ":" + get.getValue().event().intValue());
            }
            System.out.println("");
            return false;
        }

    }
}
