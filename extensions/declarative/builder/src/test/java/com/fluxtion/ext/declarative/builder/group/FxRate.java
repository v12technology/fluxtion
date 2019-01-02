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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.api.event.Event;

/**
 *
 * @author greg
 */
public class FxRate extends Event{
    
    private final String ccyPair;
    private final double rate;

    public FxRate(String ccyPair, double rate) {
        this.ccyPair = ccyPair;
        this.rate = rate;
        this.filterString = ccyPair;
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public double getRate() {
        return rate;
    }
    
}
