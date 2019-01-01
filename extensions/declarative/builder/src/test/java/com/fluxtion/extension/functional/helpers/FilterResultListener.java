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
package com.fluxtion.extension.functional.helpers;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.api.Wrapper;

/**
 *
 * @author Greg Higgins
 */
public class FilterResultListener<T> {
    public Wrapper<T> testResult;
    public T wrappedInstance;
    public boolean receivedNotification;

    public FilterResultListener(Wrapper<T> testResult) {
        this.testResult = testResult;
    }

    public FilterResultListener() {
    }
    
    @OnEvent
    public void onEvent(){
        wrappedInstance = testResult.event();
        receivedNotification = true;
    }
    
    public void reset(){
        wrappedInstance = null;
        receivedNotification = false;
    }
    
    @Initialise
    public void init(){
        wrappedInstance = null;
        receivedNotification = false;
    }
    
}
