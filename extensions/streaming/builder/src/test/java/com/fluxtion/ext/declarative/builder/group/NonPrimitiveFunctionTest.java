/*
 * Copyright (c) 2021, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.util.Tuple;
import static com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder.groupByCalcComplex;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class NonPrimitiveFunctionTest extends StreamInprocessTest{
    
    
    @Test
    public void nonPrimitive(){
       sep((c) -> {
            groupByCalcComplex(Deal::getCcyPair, NonPrimitiveFunctionTest::calcComplex).id("dealsByCcyPair");//.log("{}");
        }); 
        Deal deal = new Deal();
        deal.setCcyPair("EURUSD");
        deal.setDealtSize(100);
        onEvent(deal);
        onEvent(deal);
        onEvent(deal);
        deal.setCcyPair("EURJPY");
        deal.setDealtSize(50_000);
        onEvent(deal);
        deal.setDealtSize(150_000);
        onEvent(deal);

        GroupBy<Tuple<String, ComplexVal>> dealsByCcyPair = getField("dealsByCcyPair");
        assertThat(dealsByCcyPair.value("EURUSD").getValue().getCount(), is(3));
        assertThat(dealsByCcyPair.value("EURJPY").getValue().getCount(), is(2));
    }
    
    
    public static ComplexVal calcComplex(Deal deal, ComplexVal previousResult){
        if(previousResult == null){
            previousResult = new ComplexVal();
        }
        previousResult.count += 1;
        return previousResult;
    }
    
    @Data
    public static class ComplexVal{
    
        private int count;
    }
}
