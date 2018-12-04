/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.api.event.test;


/**
 *
 * @author Greg Higgins
 */
public class AssertTestEvent extends com.fluxtion.runtime.event.Event {

    public static final int ID = 1099;
    public final String nodeName;
    //filter ids for lifecyle events
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
