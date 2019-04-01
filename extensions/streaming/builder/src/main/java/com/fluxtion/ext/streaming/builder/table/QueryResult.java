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
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.builder.table;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author gregp
 */
public abstract class QueryResult {

    protected List table0;
    protected MultiMap join0Map;
    protected MultiMap join1Map;
    protected MultiMap join2Map;
    protected MultiMap join3Map;
    protected MultiMap join4Map;
    protected MultiMap join5Map;
    protected MultiMap[] maps;
    protected List<JoinedRow> resultList;

    @EventHandler
    public void onLoadComplete(LoadCompleteEvent loadComplete) {
        join1();
        join(1);
        join(2);
        join(3);
        join(4);
        join(5);
//        return true;
    }

    private void join1() {
        for (int i = 0; i < table0.size(); i++) {
            Object src = table0.get(i);
            List targetList = join0Map.get(getKey0(src, false) );
            for (int j = 0; j < targetList.size(); j++) {
                Object target = targetList.get(j);
                JoinedRow row = new JoinedRow();
                row.tables[0] = src;
                row.tables[1] = target;
                resultList.add(row);
            }
        }
    }

    public void join(int joinNum) {
        MultiMap joinMap = maps[joinNum];
        for (int i = 0; i < resultList.size(); i++) {
            JoinedRow src = resultList.get(i);

            List targetList = joinMap.get(getKey(joinNum, src, false));
            for (int j = 0; j < targetList.size(); j++) {
                Object target = targetList.get(j);
                JoinedRow clonedRow = src.cloneRow();
                clonedRow.tables[joinNum + 1] = target;
                resultList.add(clonedRow);
            }
        }

    }

    protected Object getKey0(Object details, boolean inverted) {
        return null;
    }
    
    protected Object getKey1(Object details, boolean inverted) {
        return null;
    }

    protected Object getKey2(Object details, boolean inverted) {
        return null;
    }

    protected Object getKey3(Object details, boolean inverted) {
        return null;
    }

    protected Object getKey4(Object details, boolean inverted) {
        return null;
    }

    protected Object getKey5(Object details, boolean inverted) {
        return null;
    }

    private Object getKey(int joinNum, JoinedRow src, boolean inverted) {
        switch (joinNum) {
            case 1:
                return getKey1(src, inverted);
            case 2:
                return getKey2(src, inverted);
            case 3:
                return getKey3(src, inverted);
            case 4:
                return getKey4(src, inverted);
            case 5:
                return getKey5(src, inverted);
        }
        return null;
    }

    @Initialise
    public void init() {
        table0 = new ArrayList<>();
        join0Map = new MultiMap<>();
        join1Map = new MultiMap<>();
        join2Map = new MultiMap<>();
        join3Map = new MultiMap<>();
        join4Map = new MultiMap<>();
        join5Map = new MultiMap<>();
        maps = new MultiMap[]{join0Map, join1Map, join2Map, join3Map, join4Map, join5Map};
        resultList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "QueryResult{\n" + resultList.stream().map(JoinedRow::toString).collect(Collectors.joining("\n"))+ "\n}";
    }

}
