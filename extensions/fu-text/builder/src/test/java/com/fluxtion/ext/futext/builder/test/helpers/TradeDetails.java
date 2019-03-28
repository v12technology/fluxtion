/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.futext.builder.test.helpers;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;

/**
 * receives
 *
 * @author Greg Higgins
 */
public class TradeDetails {

    public Number traderId;
    public Number tradeSize;

    private boolean traderIdUpdated;
    private boolean tradeSizeUpdated;

    public TradeDetails(Number traderId, Number tradeSize) {
        this.traderId = traderId;
        this.tradeSize = tradeSize;
    }

    public TradeDetails() {
    }

    @OnParentUpdate("traderId")
    public void tradeIdUpdated(Number traderId){
        traderIdUpdated = true;
    }
    
    @OnParentUpdate("tradeSize")
    public void tradeSizeUpdated(Number traderId){
        tradeSizeUpdated = true;
    }
    
    @OnEvent
    public boolean onEvent() {
        boolean ret = false;
        if (traderIdUpdated & tradeSizeUpdated ) {
            ret = true;
            tradeSizeUpdated = false;
            traderIdUpdated = false;
        }
        return ret;
    }

    public int getTraderId() {
        return traderId.intValue();
    }

    public int getTradeSize() {
        return tradeSize.intValue();
    }

    @Initialise
    public void init(){
        traderIdUpdated = false;
        tradeSizeUpdated = false;
    }
    
    @Override
    public String toString() {
        return "TradeDetails{" + "traderId=" + traderId.intValue() + ", tradeSize=" + tradeSize.intValue() + '}';
    }

    
}
