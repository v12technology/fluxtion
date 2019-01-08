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

import com.fluxtion.api.event.Event;

/**
 *
 * @author Greg Higgins
 */
public class DataEvent extends Event{

    public static final int ID = 1;
    
    public DataEvent() {
        super(ID);
    }
    
    public int value;

    public int getValue() {
        return value;
    }
    
    public double getDoubleVal(){
        return 0;
    }
    
    public void setDataKey(String key){
        this.filterString = key;
    }
    
}
