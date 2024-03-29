package com.fluxtion.compiler.builder.dataflow.lookup;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.dataflow.lookup.IntLookupPredicate;
import com.fluxtion.runtime.dataflow.lookup.LongLookupPredicate;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class StreamLookupTest extends MultipleSepTargetInProcessTest {

    public StreamLookupTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    private void sendMarketEvents() {
        onEvent(new MarketUpdate(10, "EURUSD"));
        onEvent(new MarketUpdate(11, "EURCHF"));
        onEvent(new MarketUpdate(10, "EURUSD"));
        onEvent(new MarketUpdate(11, "EURCHF"));
        onEvent(new MarketUpdate(11, "EURCHF"));
        onEvent(new MarketUpdate(15, "USDGBP"));
        onEvent(new MarketUpdate(15, "USDGBP"));
        onEvent(new MarketUpdate(11, "EURCHF"));
        onEvent(new MarketUpdate(15, "USDGBP"));
    }

    @Test
    public void testLongLookup() {
        enableInitCheck(false);
        sep(c -> {
            DataFlow.subscribe(MarketUpdate.class)
                    .filterByProperty(
                            MarketUpdate::getIdAsLong,
                            LongLookupPredicate.buildPredicate("EURUSD", "marketRefData"))
                    .mapToInt(Mappers.count())
                    .id("count")
            ;

        });
        sep.injectNamedInstance((ToLongFunction<String>) new MarketReferenceData()::toIdLong, ToLongFunction.class, "marketRefData");
        enableInitCheck(true);
        init();
        sendMarketEvents();
        Assert.assertEquals(2, (int) getStreamed("count"));
    }

    @Test
    public void testIntLookup() {
        enableInitCheck(false);
        sep(c -> {
            DataFlow.subscribe(MarketUpdate.class)
                    .filterByProperty(
                            MarketUpdate::getIdAsInt,
                            IntLookupPredicate.buildPredicate("EURUSD", "marketRefData"))
                    .mapToInt(Mappers.count())
                    .id("count")
            ;

        });
        sep.injectNamedInstance((ToIntFunction<String>) new MarketReferenceData()::toId, ToIntFunction.class, "marketRefData");
        enableInitCheck(true);
        init();
        sendMarketEvents();
        Assert.assertEquals(2, (int) getStreamed("count"));
    }

    public static class MarketReferenceData {

        public long toIdLong(String marketName) {
            switch (marketName) {
                case "EURUSD":
                    return 10;
                case "EURCHF":
                    return 11;
                case "USDGBP":
                    return 15;
                default:
                    return Long.MAX_VALUE;
            }
        }

        public int toId(String marketName) {
            return (int) toIdLong(marketName);
        }
    }

    public static class MarketUpdate {
        private final long id;
        private final String name;


        public MarketUpdate(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getIdAsLong() {
            return id;
        }

        public int getIdAsInt() {
            return (int) id;
        }

        @Override
        public String toString() {
            return "MarketUpdate{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
