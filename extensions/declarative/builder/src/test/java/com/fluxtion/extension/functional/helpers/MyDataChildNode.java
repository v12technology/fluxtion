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

import com.fluxtion.api.annotations.OnEvent;

/**
 *
 * @author greg
 */
public class MyDataChildNode extends Number{
    
    public MyDataHandler handler;

    public MyDataChildNode(MyDataHandler handler) {
        this.handler = handler;
    }

    public MyDataChildNode() {
    }
    
    @OnEvent
    public void processUpdate(){
    }
    
    public int getIntVal() {
        return handler.getIntVal();
    }

    public double getDoubleVal() {
        return handler.getDoubleVal();
    }

    public String getStringVal() {
        return handler.getStringVal();
    }

    @Override
    public int intValue() {
        return handler.getIntVal();
    }

    @Override
    public long longValue() {
        return handler.getIntVal();
    }

    @Override
    public float floatValue() {
        return (float) handler.getDoubleVal();
    }

    @Override
    public double doubleValue() {
        return handler.getDoubleVal();
    }
    
}
