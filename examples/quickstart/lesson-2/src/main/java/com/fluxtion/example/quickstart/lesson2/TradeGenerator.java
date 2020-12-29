/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.example.quickstart.lesson2;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.integration.eventflow.sources.ManualEventSource;
import java.util.Random;

/**
 * Simple ccy pair trade generator. Publishes events into a {@link ManualEventSource}.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TradeGenerator {

    private static final String[] ccyPairs = new String[]{"EURUSD", "EURCHF", "EURGBP", "GBPUSD",
        "USDCHF", "EURJPY", "USDJPY", "USDMXN", "GBPCHF", "EURNOK", "EURSEK"};

    static void publishTestData(StaticEventProcessor processor) throws InterruptedException {
        Random random = new Random();
        while (true) {
            processor.onEvent(new TradeMonitor.Trade(ccyPairs[random.nextInt(ccyPairs.length)], random.nextInt(100) + 10));
            Thread.sleep(random.nextInt(10) + 10);
        }
    }

}
