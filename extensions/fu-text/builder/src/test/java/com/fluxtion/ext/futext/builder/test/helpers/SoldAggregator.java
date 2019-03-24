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
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author greg
 */
public class SoldAggregator {
    public NumericValue dayId;
    public Number salesVolumeDaily;
    public Wrapper<Number> salesVolumeDailyWrapper;
    public Number salesVolumeTotal;
    public Wrapper<Number> salesVolumeTotalNumber;
    public NumericValue vatRate;
    private Map<Integer, Integer> daySales;
    
    @OnEvent
    public void updated(){
        daySales.put(dayId.intValue(), salesVolumeDaily.intValue());
    }
    
    @Initialise
    public void init(){
        daySales = new HashMap<>();
        salesVolumeTotal = salesVolumeTotalNumber.event();
        salesVolumeDaily = salesVolumeDailyWrapper.event();
    }

    public Map<Integer, Integer> getDaySales() {
        return Collections.unmodifiableMap(daySales);
    }
    
    public int totalSalesVolume(){
        return salesVolumeTotal.intValue();
    }

    @Override
    public String toString() {
        return "SoldAggregator{" + "salesVolumeTotal=" + salesVolumeTotal.intValue() + ", vatRate=" + vatRate.doubleValue() + "%, daySales=" + daySales + '}';
    }
    
}
