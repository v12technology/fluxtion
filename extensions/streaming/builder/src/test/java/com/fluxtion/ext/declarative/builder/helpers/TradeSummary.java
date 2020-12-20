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

/**
 *
 * @author Greg Higgins
 */
public class TradeSummary {
    
    private String traderId;
    private int TraderId;
    private int totalVolume;
    private int totalConfirmedVolume;
    private int outstandingVoulme;
    private int dealCount;
    private int tradeCount;
    private double averagOrderSize;

    public void setTraderIdString(String traderId) {
        this.traderId = traderId;
    }

    public void setTotalVolume(int totalVolume) {
        outstandingVoulme = totalVolume - totalConfirmedVolume;
        this.totalVolume = totalVolume;
    }

    public void setTraderId(int TraderId) {
        this.TraderId = TraderId;
    }

    public void setTotalConfirmedVolume(int totalConfirmedVolume) {
        outstandingVoulme = totalVolume - totalConfirmedVolume;
        this.totalConfirmedVolume = totalConfirmedVolume;
    }

    public void setOutstandingVoulme(int outstandingVoulme) {
        this.outstandingVoulme = outstandingVoulme;
    }


    public void setDealCount(int dealCount) {
        this.dealCount = dealCount;
    }

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }

    public int getOutstandingVoulme() {
        return outstandingVoulme;
    }

    public void setAveragOrderSize(double averagOrderSize) {
        this.averagOrderSize = averagOrderSize;
    }

    public String getTraderIdString() {
        return traderId;
    }
    
    @Override
    public String toString() {
        return "TradeSummary{" + "traderId=" + traderId + 
                ", TraderId=" + TraderId + 
                ", totalVolume=" + totalVolume + 
                ", outstandingVoulme=" + outstandingVoulme + 
                ", dealCount=" + dealCount + 
                ", tradeCount=" + tradeCount + 
                ", averagOrderSize=" + averagOrderSize + 
                ", totalConfirmedVolume=" + totalConfirmedVolume + '}';
    }
 
}
