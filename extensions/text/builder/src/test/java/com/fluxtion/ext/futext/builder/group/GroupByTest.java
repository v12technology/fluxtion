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
package com.fluxtion.ext.futext.builder.group;

import com.fluxtion.ext.futext.builder.test.helpers.LeaguePosition;
import com.fluxtion.ext.futext.builder.test.helpers.MatchResult;
import com.fluxtion.ext.futext.builder.test.helpers.TradeDetails;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.builder.group.GroupByBuilder;
import com.fluxtion.ext.text.api.ascii.Ascii2IntTerminator;
import com.fluxtion.ext.text.api.util.StringDriver;
import com.fluxtion.generator.util.BaseSepInprocessTest;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import static com.fluxtion.ext.text.builder.ascii.AsciiHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author greg
 */
public class GroupByTest extends BaseSepInprocessTest {

    public static final String VAR_TRADE_DEIAILS = "tradeDetails";
    public static final String VAR_TRADE_SUMMARY = "tradeSummary";
    public static final String VAR_SUM_HOMEGOALS = "totalHomeGoals";
    public static final String VAR_SUM_AWAYGOALS = "totalAwayGoals";
    public static final String VAR_AGG_LEAGUEPOSITION = "leaguePositionMap";

    @org.junit.Test
    public void testGroupByNonEvent() {
        sep((c) -> {
            //input trade record from log file
            Ascii2IntTerminator traderId = readInt("trader_id=");
            Ascii2IntTerminator tradeSize = readInt("trade size=");
            TradeDetails tradeDetails = c.addPublicNode(new TradeDetails(traderId, tradeSize), VAR_TRADE_DEIAILS);
            //groupby
            GroupByBuilder<TradeDetails, MutableNumber> trades = groupBy(tradeDetails::getTraderId, MutableNumber.class);
            trades.sum(TradeDetails::getTradeSize, MutableNumber::set);
            trades.build().id(VAR_TRADE_SUMMARY);
        });

        StringDriver.streamChars(
            "trader_id=1 trade size=120\n"
            + "trader_id=2 trade size=50\n"
            + "trader_id=1 trade size=100\n"
            + "trader_id=2 trade size=42\n"
            + "trader_id=1 trade size=80\n",
            sep, false);

        GroupBy< MutableNumber> summary = getField(VAR_TRADE_SUMMARY);
        
        int value1 = summary.value(1).intValue();
        int value2 = summary.value(2).intValue();
        assertThat(300, is(value1));
        assertThat(92, is(value2));
    }

    @org.junit.Test
    public void testGroupByMultipleOutputs() {
        sep((c) -> {
            MatchResult result = new MatchResult(readBytesCsv(0), readIntCsv(1), readIntCsv(2), readBytesCsv(3));
            //group by definition
            GroupByBuilder<MatchResult, MutableNumber> homeGoals = groupBy(result::getHomeTeamAsString, MutableNumber.class);
            GroupByBuilder<MatchResult, MutableNumber> awayGoals = groupBy(result::getHomeTeamAsString, MutableNumber.class);
            //calculate aggregate values
            homeGoals.sum(MatchResult::getHomeGoals, MutableNumber::set).build().id(VAR_SUM_HOMEGOALS);
            awayGoals.sum(MatchResult::getAwayGoals, MutableNumber::set).build().id(VAR_SUM_AWAYGOALS);
        });
        StringDriver.streamChars(
            "liverpool,2,3,everton\n"
            + "arsenal,1,3,everton\n"
            + "liverpool,6,1,arsenal\n"
            + "everton,0,9,arsenal\n"
            + "wba,2,9,liverpool\n"
            + "wba,6,9,wba\n"
            + "Manchester United,2,1,arsenal\n"
            + "Manchester United,4,6,everton\n"
            + "yefiuregi,1114,6,arsenal\n"
            + "everton,6,1,arsenal\n",
            sep, false);
        GroupBy< MutableNumber> homeGoals = getField(VAR_SUM_HOMEGOALS);
        assertThat(8, is(homeGoals.value("liverpool").intValue()));
    }

    @org.junit.Test
    public void testGroupByJoinToClassTarget() {
        sep((c) -> {
            MatchResult result = c.addNode(
                new MatchResult(readBytesCsv(0), readIntCsv(1), readIntCsv(2), readBytesCsv(3)));
            //gorupby
            GroupByBuilder<MatchResult, LeaguePosition> home = groupBy(result::getHomeTeam, LeaguePosition.class);
            GroupByBuilder<MatchResult, LeaguePosition> away = home.join(result, MatchResult::getAwayTeam);
            //home init
            home.init(MatchResult::getHomeTeamAsString, LeaguePosition::setTeamName);
            away.init(MatchResult::getAwayTeamAsString, LeaguePosition::setTeamName);
            //calculate aggregate values for away games
            away.count(LeaguePosition::setAwayGamesPlayed);
            away.sum(MatchResult::getAwayGoals, LeaguePosition::setAwayGoalsFor);
            away.sum(MatchResult::getHomeGoals, LeaguePosition::setAwayGoalsAgainst);
            away.sum(MatchResult::getAwayWin, LeaguePosition::setAwayWins);
            away.sum(MatchResult::getAwayLoss, LeaguePosition::setAwayLosses);
            away.sum(MatchResult::getDraw, LeaguePosition::setAwayDraws);
            //calculate aggregate values for home games
            home.count(LeaguePosition::setHomeGamesPlayed);
            home.sum(MatchResult::getHomeGoals, LeaguePosition::setHomeGoalsFor);
            home.sum(MatchResult::getHomeWin, LeaguePosition::setHomeWins);
            home.sum(MatchResult::getHomeLoss, LeaguePosition::setHomeLosses);
            home.sum(MatchResult::getDraw, LeaguePosition::setHomeDraws);
            home.sum(MatchResult::getAwayGoals, LeaguePosition::setHomeGoalsAgainst);
            //
            home.build().id(VAR_AGG_LEAGUEPOSITION);
        });
        
        StringDriver.streamChars(
            "wba,1,3,arsenal\n"
            + "wba,2,3,liverpool\n"
            + "arsenal,2,2,liverpool\n"
            + "arsenal,6,1,wba\n"
            + "liverpool,1,1,wba\n"
            + "liverpool,0,1,arsenal\n",
            sep, false);
        GroupBy< LeaguePosition> league = getField(VAR_AGG_LEAGUEPOSITION);
        
        
        Map<String, LeaguePosition> leagueList = league.stream().collect(Collectors.toMap(LeaguePosition::getTeamName, Function.identity()));
        
        LeaguePosition arsenal = leagueList.get("arsenal");
        LeaguePosition wba = leagueList.get("wba");
        LeaguePosition liverpool = leagueList.get("liverpool");

        assertThat(4, is(arsenal.homePoints()));
        assertThat(0, is(wba.homePoints()));
        assertThat(1, is(liverpool.homePoints()));

        assertThat(6, is(arsenal.awayPoints()));
        assertThat(1, is(wba.awayPoints()));
        assertThat(4, is(liverpool.awayPoints()));

        assertThat(10, is(arsenal.totalPoints()));
        assertThat(1, is(wba.totalPoints()));
        assertThat(5, is(liverpool.totalPoints()));
    }

}
