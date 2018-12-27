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
package com.fluxtion.extension.functional.group;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;

/**
 *
 * @author Greg Higgins
 */
public class OrderSummary {

    private int orderId;
    private int orderSize;
    private int firstDealId;
    private int dealCount;
    private int lastDealSize;
    private double volumeDealt;
    private int avgDealSize;
    private String ccyPair;
    private boolean event = false;
    private boolean eventComplete = false;

    public String getCcyPair() {
        return ccyPair;
    }

    public void setCcyPair(String ccyPair) {
        this.ccyPair = ccyPair;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(int orderSize) {
        this.orderSize = orderSize;
    }

    public int getDealCount() {
        return dealCount;
    }

    public void setDealCount(int dealCount) {
        this.dealCount = dealCount;
    }

    public double getVolumeDealt() {
        return volumeDealt;
    }

    public void setVolumeDealt(double volumeDealt) {
        this.volumeDealt = volumeDealt;
    }

    public int getAvgDealSize() {
        return avgDealSize;
    }

    public void setAvgDealSize(int avgDealSize) {
        this.avgDealSize = avgDealSize;
    }

    public int getLastDealSize() {
        return lastDealSize;
    }

    public void setLastDealSize(int lastDealSize) {
        this.lastDealSize = lastDealSize;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public boolean isEventComplete() {
        return eventComplete;
    }

    public void setEventComplete(boolean eventComplete) {
        this.eventComplete = eventComplete;
    }

    public int getFirstDealId() {
        return firstDealId;
    }

    public void setFirstDealId(int firstDealId) {
        this.firstDealId = firstDealId;
    }
    
    @OnEventComplete
    public void onComplete(){
        this.eventComplete = true;
    }
    
    @OnEvent
    public void onEvent(){
        this.event = true;
    }

    @Override
    public String toString() {
        return "OrderSummary{" + "orderId=" + orderId + ", orderSize=" + orderSize + ", firstDealId=" + firstDealId + ", dealCount=" + dealCount + ", lastDealSize=" + lastDealSize + ", volumeDealt=" + volumeDealt + ", avgDealSize=" + avgDealSize + ", ccyPair=" + ccyPair + ", event=" + event + ", eventComplete=" + eventComplete + '}';
    }
    
}
