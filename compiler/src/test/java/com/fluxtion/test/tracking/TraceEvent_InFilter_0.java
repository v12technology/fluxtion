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
package com.fluxtion.test.tracking;

import com.fluxtion.runtime.event.DefaultEvent;

import java.util.ArrayList;

/**
 * @author Greg Higgins
 */
public class TraceEvent_InFilter_0 extends DefaultEvent implements TraceEvent {


    public static final int ID = 1;

    public TraceEvent_InFilter_0(int filterId) {
        super(filterId);
    }

    protected TraceEvent_InFilter_0(int id, int filterId) {
        super(filterId);
    }


    public int intValue;
    public String strValue;
    public Object objValue;
    public ArrayList traceList = new ArrayList();
    public ArrayList<String> traceIdList = new ArrayList();
    public ArrayList<String> traceEventCompleteIdList = new ArrayList();
    public ArrayList<String> traceAfterEventIdList = new ArrayList();

    @Override
    public void reset() {
        intValue = 0;
        strValue = null;
        objValue = null;
        traceList.clear();
        traceIdList.clear();
        traceEventCompleteIdList.clear();
        traceAfterEventIdList.clear();
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    @Override
    public String getStrValue() {
        return strValue;
    }

    @Override
    public Object getObjValue() {
        return objValue;
    }

    @Override
    public ArrayList getTraceList() {
        return traceList;
    }

    @Override
    public ArrayList<String> getTraceIdList() {
        return traceIdList;
    }

    @Override
    public ArrayList<String> getTraceEventCompleteIdList() {
        return traceEventCompleteIdList;
    }

    @Override
    public ArrayList<String> getTraceAfterEventIdList() {
        return traceAfterEventIdList;
    }

}
