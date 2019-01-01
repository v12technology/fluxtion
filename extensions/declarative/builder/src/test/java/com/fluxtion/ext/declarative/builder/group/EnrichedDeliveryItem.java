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

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;

/**
 * Simple filtering fx rate
 * 
 * @author greg
 */
public class EnrichedDeliveryItem {
    
    private DeliveryItem item;
    private transient double gbpusd;
    private transient double eurusd;
    
    @EventHandler
    public boolean handleDeliveryItem(DeliveryItem item){
        this.item = item;
        return true;
    }
    
    @EventHandler(filterString = "GBPUSD")
    public boolean handleRateGBPUSD(FxRate rate){
        gbpusd = rate.getRate();
        return false;
    }
    
    @EventHandler(filterString = "EURUSD")
    public boolean handleRateEURUSD(FxRate rate){
        eurusd = rate.getRate();
        return false;
    }

    public String getCustomerId() {
        return item.getCustomerId();
    }

    public String getProductId() {
        return item.getProductId();
    }

    public double getValueInLocalCcy() {
        return item.getValueInLocalCcy();
    }
    
    public double getValueInDollars(){
        double exchangeRate = 1.0;
        if(item.getCustomerId().startsWith("GB")){
            exchangeRate = gbpusd;
        }else if(item.getCustomerId().startsWith("EU")){
            exchangeRate = eurusd;
        }
        return getValueInLocalCcy()*exchangeRate;
    }

    @Initialise
    public void init(){
        gbpusd = Double.NaN;
        eurusd = Double.NaN;
    }
    
    @Override
    public String toString() {
        return "EnrichedDeliveryItem{" 
                + "customerId=" + getCustomerId() 
                + ", valueInDollars=" + getValueInDollars()
                + '}';
    }
    
    
}
