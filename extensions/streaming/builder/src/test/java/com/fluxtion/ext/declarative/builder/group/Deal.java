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
 * @author Greg Higgins
 */
public class Deal implements Event{

    public static final Class<Deal> DEAL = Deal.class;
    
    public Deal(int dealId, int orderId, int size) {
        this.dealId = dealId;
        this.orderId = orderId;
        this.dealtSize = size;
    }

    public Deal() {
    }

    private int dealId;
    private int orderId;
    public int traderId;
    public String traderName;
    private String ccyPair;
    private int dealtSize;
    private int contraSize;

    public int getDealId() {
        return dealId;
    }

    public int getTraderId() {
        return traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public int getDealtSize() {
        return dealtSize;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setTraderId(int traderId) {
        this.traderId = traderId;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public void setCcyPair(String ccyPair) {
        this.ccyPair = ccyPair;
    }

    public void setDealtSize(int dealtSize) {
        this.dealtSize = dealtSize;
    }

    public int getContraSize() {
        return contraSize;
    }

    public void setContraSize(int contraSize) {
        this.contraSize = contraSize;
    }
    
    @Override
    public String toString() {
        return "Deal{" + "dealId=" + dealId + ", orderId=" + orderId + ", traderId=" + traderId + ", ccyPair=" + ccyPair + ", size=" + dealtSize + '}';
    }
    
}
