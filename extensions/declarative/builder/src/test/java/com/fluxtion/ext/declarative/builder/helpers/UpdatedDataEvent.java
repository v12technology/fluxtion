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
public class UpdatedDataEvent extends Event{

    public static final int ID = 2;
    
    public UpdatedDataEvent() {
        super(ID);
    }
    
    public int value;

    public int getUPdatedValue() {
        return value;
    }
    
}
