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
package com.fluxtion.ext.declarative.builder.window;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.group.AggregateFunctions.AggregateSum;
import com.fluxtion.ext.streaming.api.util.Tuple;
import com.fluxtion.ext.streaming.builder.factory.GroupFunctionsBuilder;
import static com.fluxtion.ext.streaming.builder.factory.WindowBuilder.tumble;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class PairTest extends StreamInprocessTest {

//    @Test
//    @Ignore
//    public void testpair() {
//        sep((c) -> {
//            select(Pair.class).map(cumSum(), Pair::getValue).log("pair sum: '{}'", Number::intValue);
//        });
//        onEvent(new Pair<>("test", 10));
//        onEvent(new Pair<>("test", 10));
//        onEvent(new Pair<>("test", 10));
//    }

    @Test
    public void tumbleGroupBy() {
        fixedPkg = true;
        reuseSep = true;
        sep(PairTest::topTrades);
        for (int i = 0; i < 10; i++) {
            onEvent(new Trade("ORCL", i));
            onEvent(new Trade("APPL", i * 6));
        }
        for (int i = 0; i < 10; i++) {
            onEvent(new Trade("ORCL", 10));
            onEvent(new Trade("IBM", 1 * 7));
            onEvent(new Trade("MSOFT", 1 * 3));
            onEvent(new Trade("AMZN", 1 * 2));
        }
        for (int i = 0; i < 10; i++) {
            onEvent(new Trade("AMZN", i + 6));
        }
    }

    public static void topTrades(SEPConfig cfg) {
        cfg.setGenerateLogging(true);
        tumble(GroupFunctionsBuilder.groupBy(Trade::getSymbol, Trade::getVolume, AggregateSum::calcCumSum), 6)
            //            .comparing(Pair::getValue)
            .comparator(new PairValueNumberCompare<String>())
            .top(2)
            .map(PairTest::formatMessage)
            .log();
    }

    public static String formatMessage(List<Tuple<String, Number>> t) {
        String ret = "Trade summary:\n";
        for (int i = 0; i < t.size(); i++) {
            Tuple<String, Number> pair = t.get(i);
            ret += "\tpos[" + (1 + i) + "] symbol:" + pair.getKey() + " cumulative volume:" + pair.getValue() + "\n";
        }
        return ret;
    }

    public static class PairValueNumberCompare<K> implements Comparator<Tuple<K, Number>> {

        @Override
        public int compare(Tuple<K, Number> o1, Tuple<K, Number> o2) {
            return (int) (o2.getValue().doubleValue() - o1.getValue().doubleValue());
        }

    }

//    private static <S, K, V extends Number> GroupBy<Pair<K, V>> groupBy(
//        SerializableFunction<S, K> keySupplier,
//        SerializableFunction<S, V> valueSupplier,
//        LambdaReflection.SerializableBiFunction<? super V, ? super V, ? extends V> func
//    ) {
//        GroupBy<Pair<K, V>> build = Group.groupBy(keySupplier, Pair.class)
//            .init(keySupplier, Pair::setKey)
//            .mapPrimitiveNoType(valueSupplier, Pair::setValue, func)
//            .build();
//        return build;
//
//    }

//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class Pair<K, V> {
//
//        K key;
//        V value;
//
//        public static String hello() {
//            return "hello";
//        }
//
//    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trade {

        String symbol;
        double volume;
    }
}
