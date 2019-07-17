/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.ext.declarative.builder.group.Deal.DEAL;
import static com.fluxtion.ext.declarative.builder.group.TraderPosition.TRADER_POSITION;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import static com.fluxtion.ext.streaming.builder.log.LogBuilder.Log;
import static com.fluxtion.ext.declarative.builder.group.MaxCcyTraderPosConfig.TRADER_POS_CFG;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Greg Higgins
 */
public class MultiKeyTest extends BaseSepTest {

    @Test
    public void testMultiKey() {
        EventHandler sep = buildAndInitSep(Builder1.class);
        Deal eu_john = new Deal();
        eu_john.traderId = 1;
        eu_john.traderName = "John Smith";
        eu_john.setCcyPair("EURUSD");
        eu_john.setOrderId(1001);
        eu_john.setDealtSize(200_000);
        eu_john.setContraSize(-224_000);
        eu_john.setDealId(909);

        sep.onEvent(eu_john);

        eu_john.setDealId(910);
        eu_john.setDealtSize(200_000);
        eu_john.setContraSize(-250_000);
        sep.onEvent(eu_john);

        eu_john.setCcyPair("GBPUSD");
        eu_john.setDealId(911);
        eu_john.setDealtSize(1_600_000);
        eu_john.setContraSize(-2_486_000);
        sep.onEvent(eu_john);

        eu_john.traderId = 2;
        eu_john.traderName = "Mike Stevens";
        eu_john.setCcyPair("GBPUSD");
        eu_john.setDealId(912);
        eu_john.setDealtSize(540_000);
        eu_john.setContraSize(-557_000);
        sep.onEvent(eu_john);

        GroupBy<Deal, TraderPosition> traderPositions = getField("traderPositions");
        List<TraderPosition> posList = traderPositions.getMap().values().stream().map(wrapper -> wrapper.event()).collect(Collectors.toList());
        assertThat(3, is(posList.size()));

        List<TraderPosition> eurusdList = posList.stream().filter(pos -> pos.ccyPair.equalsIgnoreCase("EURUSD")).collect(Collectors.toList());
        assertThat(1, is(eurusdList.size()));
        assertThat(400_000, is((int) eurusdList.get(0).getDealtVolume()));
        assertThat("John Smith", is(eurusdList.get(0).getName()));

        List<TraderPosition> gbpusddList = posList.stream().filter(pos -> pos.ccyPair.equalsIgnoreCase("GBPUSD")).collect(Collectors.toList());
        assertThat(2, is(gbpusddList.size()));
    }

//    @Override
//    protected String testPackageID() {
//        return "";
//    }

    @Test
    public void multiKeyJoin() {
        EventHandler sep = buildAndInitSep(Builder_MultiKeyJoin.class);


        Deal eu_john = new Deal();
        eu_john.traderId = 1;
        eu_john.traderName = "John Smith";
        eu_john.setCcyPair("EURUSD");
        eu_john.setOrderId(1001);
        eu_john.setDealtSize(200_000);
        eu_john.setContraSize(-224_000);
        eu_john.setDealId(909);

        sep.onEvent(eu_john);
        GroupBy<Deal, TraderPosition> traderPositions = getField("traderPositions");
        
        sep.onEvent(new MaxCcyTraderPosConfig(1, "EURUSD"));

    }

    public static class Builder1 extends SEPConfig {

        {
            GroupByBuilder<Deal, TraderPosition> traderPos
                    = groupBy(DEAL, TRADER_POSITION, Deal::getCcyPair, Deal::getTraderId);
            traderPos.init(Deal::getTraderName, TraderPosition::setName);
            traderPos.init(Deal::getCcyPair, TraderPosition::setCcyPair);
            traderPos.sum(Deal::getDealtSize, TraderPosition::setDealtVolume);
            traderPos.sum(Deal::getContraSize, TraderPosition::setContraVolume);
            GroupBy<Deal, TraderPosition> traderPositions = traderPos.build();
            //add public node for testing
            addPublicNode(traderPositions, "traderPositions");
            //logging
            Log(DEAL);
            Log(traderPositions);
        }
    }

    public static class Builder_MultiKeyJoin extends SEPConfig {

        {
            GroupByBuilder<Deal, TraderPosition> traderPos
                    = groupBy(DEAL, TRADER_POSITION, Deal::getCcyPair, Deal::getTraderId);
            //join
            GroupByBuilder<MaxCcyTraderPosConfig, TraderPosition> maxPos = traderPos.join(TRADER_POS_CFG,
                    MaxCcyTraderPosConfig::getCcyPairManaged,
                    MaxCcyTraderPosConfig::getTraderId
            );
            //joins again
            GroupByBuilder<TraderPositionAdjustment, TraderPosition> adjustment = maxPos.join(TraderPositionAdjustment.class, TraderPositionAdjustment::getCcyPair, TraderPositionAdjustment::getTraderId);
            //
            adjustment.sum(TraderPositionAdjustment::getDealtSize, TraderPosition::setDealtVolume);
            adjustment.sum(TraderPositionAdjustment::getContraSize, TraderPosition::setContraVolume);
            adjustment.optional(true);
            
            //set cfg for max position
            maxPos.set(MaxCcyTraderPosConfig::getMaxPosition, TraderPosition::setMaxDealtVolume);
            //trader position calcs
            traderPos.init(Deal::getTraderName, TraderPosition::setName);
            traderPos.init(Deal::getCcyPair, TraderPosition::setCcyPair);
            traderPos.sum(Deal::getDealtSize, TraderPosition::setDealtVolume);
            traderPos.sum(Deal::getContraSize, TraderPosition::setContraVolume);
            GroupBy<Deal, TraderPosition> traderPositions = traderPos.build();
            //add public node for testing
            addPublicNode(traderPositions, "traderPositions");
            //logging
            Log(DEAL);
            Log(TRADER_POS_CFG);
            Log(traderPositions);
        }
    }

}
