/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.ext.declarative.builder.helpers;

import com.fluxtion.runtime.event.Event;

/**
 *
 * @author Greg Higgins
 */
public class MyData extends Event {
    public static final Class<MyData> MyDataEvent = MyData.class;    
    private final int intVal;
    private final double doubleVal;
    private final String stringVal;

    public MyData(int intVal, double doubleVal, String stringVal) {
        super(Integer.MAX_VALUE, intVal, stringVal);
        this.intVal = intVal;
        this.doubleVal = doubleVal;
        this.stringVal = stringVal;
    }

    public int getIntVal() {
        return intVal;
    }

    public double getDoubleVal() {
        return doubleVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public String getFilterString() {
        return filterString.toString();
    }
}
