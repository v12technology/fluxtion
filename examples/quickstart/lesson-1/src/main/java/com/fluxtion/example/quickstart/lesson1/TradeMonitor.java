/*
 * Copyright (C) 2020 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.example.quickstart.lesson1;

import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.ext.streaming.api.Duration.seconds;
import com.fluxtion.ext.streaming.api.util.Tuple;
import static com.fluxtion.ext.streaming.api.util.Tuple.numberValComparator;
import static com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder.groupBySum;
import static com.fluxtion.integration.eventflow.EventFlow.flow;
import com.fluxtion.integration.eventflow.sources.ManualEventSource;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author V12 Technology Ltd.
 */
public class TradeMonitor {
    
    public static void main(String[] args) throws Exception {
        ManualEventSource<Trade> tradeInjector = new ManualEventSource<>("trade-source");
        flow(tradeInjector)
            .sep(TradeMonitor::build)
            .start();
        TradeGenerator.publishTestData(tradeInjector);
    }

    public static void build(SEPConfig cfg) {
        groupBySum(Trade::getSymbol, Trade::getAmount)
            .sliding(seconds(1), 5)
            .comparator(numberValComparator()).reverse()
            .top(3)
            .map(TradeMonitor::formatTradeList)
            .log();
    }
    
    public static String formatTradeList(List<Tuple<String, Number>> trades){
        StringBuilder sb = new StringBuilder("Most active ccy pairs in past 5 seconds:");
        for (int i = 0; i < trades.size(); i++) {
            Tuple<String, Number> result = trades.get(i);
            sb.append(String.format("\n\t%2d. %5s - %d trades", i + 1, result.getKey(), result.getValue().intValue()));
        }
        return sb.toString();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trade {

        private String symbol;
        private double amount;

    }

}
