package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.helpers.Aggregates;
import com.fluxtion.runtime.stream.helpers.Predicates;
import com.fluxtion.runtime.time.FixedRateTrigger;
import lombok.Value;
import org.junit.Test;

import java.util.Date;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class TradingMonitorArticleTest extends MultipleSepTargetInProcessTest {

    public TradingMonitorArticleTest(boolean compiledSep) {
        super(compiledSep);
    }


    @Test
    public void mergeTradeData() {
//        writeSourceFile = true;
        sep(c -> {
            EventStreamBuilder<GroupByStreamed<String, Integer>> tradeStatsDaily = subscribe(Trade.class)
                    .groupBy(Trade::getTickerId, Trade::getVolume, Aggregates.intSum()).id("groupedTradeDaily")
                    .publishTriggerOverride(FixedRateTrigger.atMillis(1_000))
                    .resetTrigger(EventFlow.subscribeToSignal("startOfDay"))
//                    .console("triggered cumSum time:%t volume:{}")
                    ;

            EventStreamBuilder<GroupBy<String, Integer>> tradeVolumeEvery20Seconds = subscribe(Trade.class)
                    .groupBySliding(
                            Trade::getTickerId, Trade::getVolume, Aggregates.intSum(), 5_000, 4)
                    .resetTrigger(EventFlow.subscribeToSignal("startOfDay"))
                    .map(GroupByFunction.filterValues(Predicates.gtBoxed(25000)));
//                    .console("max volume in window:{}");

            GroupByFunction.innerJoinStreams(tradeVolumeEvery20Seconds, tradeStatsDaily).id("joinedData")
                    .resetTrigger(EventFlow.subscribeToSignal("startOfDay"))
                    .console("joined time:%t data:{}")
            ;

        });

//        sep(TestSep_mergeTradeData.class);

        setTime(0);

//        for (int i = 0; i < 30; i++) {
//            setTime(i * 100);
//            onEvent(new Trade("IBM", 1.03, 200));
//        }

        for (int i = 0; i < 30_000; i += 500) {
            setTime(i);
            onEvent(new Trade("IBM", 1.03, 200));
            onEvent(new Trade("MSFT", 1.03, 3000));
            onEvent(new Trade("ORCL", 1.03, 1000));
        }
        publishSignal("startOfDay");
        for (int i = 30_500; i < 50_000; i += 500) {
            tick(i);
            break;
        }
    }


    @Value
    public static class MarketTick {
        String tickerId;
        double bidPrice;
        double offerPrice;
    }

    @Value
    public static class Trade {
        String tickerId;
        double price;
        int volume;
    }

    @Value
    public static class CompanyInfo {
        String tickerId;
        String url;
    }

    @Value
    public static class CompanyNews {
        String tickerId;
        String article;
        Date publishTime;
    }
}
