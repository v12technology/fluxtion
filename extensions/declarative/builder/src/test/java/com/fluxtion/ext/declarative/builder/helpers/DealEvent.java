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
 * Testing event for aggregate functions.
 * 
 * @author Greg Higgins
 */
public class DealEvent extends Event{
    public int dealId;
    public int tradeVolume;
    public int tradeId;

    public DealEvent(int tradeId, int tradeVolume) {
        this.tradeId = tradeId;
        this.tradeVolume = tradeVolume;
    }

    public DealEvent() {
    }
    
    public int getDealId() {
        return dealId;
    }

    public int getTradeVolume() {
        return tradeVolume;
    }

    public int getParentTradeId() {
        return tradeId;
    }

    @Override
    public String toString() {
        return "DealEvent{" + "dealId=" + dealId + ", tradeVolume=" + tradeVolume + ", tradeId=" + tradeId + '}';
    }
    
    
}
