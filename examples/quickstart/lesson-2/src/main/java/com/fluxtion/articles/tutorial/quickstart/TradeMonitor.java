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
package com.fluxtion.articles.tutorial.quickstart;

import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.ext.streaming.api.Duration.seconds;
import static com.fluxtion.ext.streaming.api.util.Tuple.numberValComparator;
import static com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder.groupBySum;
import static com.fluxtion.integration.eventflow.EventFlow.flow;
import com.fluxtion.integration.eventflow.filters.SepStage;
import com.fluxtion.integration.eventflow.sources.ManualEventSource;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author V12 Technology Ltd.
 */
public class TradeMonitor {

    public static void build(SEPConfig cfg) {
        groupBySum(Trade::getSymbol, Trade::getAmount)
                .sliding(seconds(1), 5)
                .comparator(numberValComparator()).reverse()
                .top(3)
                .log();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trade {

        private String symbol;
        private double amount;

    }

    private static final String[] ccyPairs = new String[]{
        "EURUSD", "EURCHF", "EURGBP", "GBPUSD", "USDCHF", "EURJPY", "USDJPY", "USDMXN", "GBPCHF", "EURNOK", "EURSEK"
    };

    public static void main(String[] args) throws Exception {
        //build event flow pipeline
        ManualEventSource<Trade> tradeSource = new ManualEventSource<>("trade-source");
        flow(tradeSource)
                .first(SepStage.generate(TradeMonitor::build))
                .start();
        //send test data forever
        Random random = new Random();
        while (true) {
            tradeSource.publishToFlow(new Trade(ccyPairs[random.nextInt(ccyPairs.length)], random.nextInt(100) + 10));
            Thread.sleep(random.nextInt(10) + 10);
        }
    }
}
