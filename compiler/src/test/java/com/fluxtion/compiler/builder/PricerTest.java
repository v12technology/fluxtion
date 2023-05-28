package com.fluxtion.compiler.builder;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.time.FixedRateTrigger;
import lombok.Data;
import org.junit.Test;

public class PricerTest extends CompiledOnlySepTest {
    public PricerTest(SepTestConfig compile) {
        super(compile);
    }

    @Test
    public void pricer() {
        writeOutputsToFile(true);
        sep(c -> {
            ParamsChecker checker = new ParamsChecker();
            PriceCalc priceCalc = new PriceCalc();
            StepAway stepAway = new StepAway();


            DataFlow.subscribe(MktData.class)
                    .filter(checker::paramsValid)
                    .map(priceCalc::marketUpdate)
                    .merge(
                            DataFlow.subscribe(Position.class)
                                    .filter(checker::paramsValid)
                                    .map(priceCalc::posUpdate)
                    )
                    .filter(PricerTest::sanityCheck)
                    .map(stepAway::applyStepaway)
                    .filter(new ThrottlePublisher()::canPublish);


        });

    }

    public static boolean sanityCheck(PriceLadder priceLadder) {
        return true;
    }

    public static class StepAway {

        public FixedRateTrigger rollTrigger;

        public PriceLadder applyStepaway(PriceLadder priceLadder) {
            return priceLadder;
        }

    }

    public static class ThrottlePublisher {
        public boolean canPublish(Object o) {
            return true;
        }
    }

    @Data
    public static class PriceCalc {

        MktData mktData;
        Position position;

        public PriceLadder marketUpdate(MktData mktData) {
            return null;
        }

        public PriceLadder posUpdate(Position position) {
            return null;
        }

        public PriceLadder calc() {
            return null;
        }

        @OnTrigger
        public boolean calcTrigger() {
            return true;
        }

    }

    @Data
    public static class PriceLadder {

    }

    @Data
    public static class MktData {
        String symbol;
        double mid;
    }

    @Data
    public static class Position {
        String book;
        double value;
    }

    public static class ParamsChecker {

        public boolean paramsValid(Object o) {
            return true;
        }
    }
}
