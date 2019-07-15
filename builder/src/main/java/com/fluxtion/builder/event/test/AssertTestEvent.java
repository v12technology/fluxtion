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
package com.fluxtion.builder.event.test;


/**
 * <h1>Experimental feature - do not use</h1>
 * @author Greg Higgins
 */
public class AssertTestEvent extends com.fluxtion.api.event.Event {

    public static final int ID = 1099;
    public final String nodeName;
    //filter ids for lifecycle events
    public static final int FILTER_ID_INIT = 1;
    public static final int FILTER_ID_BATCH_PAUSE = 2;
    public static final int FILTER_ID_BATCH_END = 3;
    public static final int FILTER_ID_TEARDOWN = 4;
    //lifecycle events
    public static final AssertTestEvent INIT = new AssertTestEvent(FILTER_ID_INIT, "TestEvent.INIT");
    public static final AssertTestEvent BATCH_PAUSE = new AssertTestEvent(FILTER_ID_BATCH_PAUSE, "TestEvent.BATCH_PAUSE");
    public static final AssertTestEvent BATCH_END = new AssertTestEvent(FILTER_ID_BATCH_END, "TestEvent.BATCH_END");
    public static final AssertTestEvent TEARDOWN = new AssertTestEvent(FILTER_ID_TEARDOWN, "TestEvent.TEARDOWN");
    //
    public Object contents;

    public AssertTestEvent(String nodeName) {
        super(ID, nodeName.hashCode());
        this.nodeName = nodeName;
    }
    
    private AssertTestEvent(int filterId, String nodeName){
        super(ID, filterId);
        this.nodeName = nodeName;
    }

}
