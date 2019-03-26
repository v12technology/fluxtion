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

import com.fluxtion.ext.declarative.builder.group.GroupByBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.group.GroupBy;
import com.fluxtion.ext.declarative.api.numeric.MutableNumber;
import com.fluxtion.ext.futext.api.ascii.Ascii2IntTerminator;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readBytesCsv;
import com.fluxtion.ext.futext.builder.test.helpers.TradeDetails;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readInt;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readIntCsv;
import static com.fluxtion.ext.declarative.builder.group.Group.groupBy;
import com.fluxtion.ext.declarative.builder.log.LogBuilder;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.partition.LambdaReflection;
import static com.fluxtion.ext.declarative.builder.log.LogBuilder.Log;
import com.fluxtion.ext.futext.builder.test.helpers.LeaguePosition;
import com.fluxtion.ext.futext.builder.test.helpers.MatchResult;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;

/**
 *
 * @author greg
 */
public class GroupByTest extends BaseSepTest {

    public static final String VAR_TRADE_DEIAILS = "tradeDetails";
    public static final String VAR_TRADE_SUMMARY = "tradeSummary";
    public static final String VAR_SUM_HOMEGOALS = "totalHomeGoals";
    public static final String VAR_SUM_AWAYGOALS = "totalAwayGoals";
    public static final String VAR_AGG_LEAGUEPOSITION = "leaguePositionMap";

    @org.junit.Test
    public void testGroupByNonEvent() {
        EventHandler sep = buildAndInitSep(TradeTextBuilder.class);
        StringDriver.streamChars(
                "trader_id=1 trade size=100\n"
                + "trader_id=2 trade size=50\n"
                + "trader_id=1 trade size=100\n"
                + "trader_id=2 trade size=42\n"
                + "trader_id=1 trade size=100\n",
                sep, false);

        GroupBy<MutableNumber> summary = getField(VAR_TRADE_SUMMARY);
        Wrapper<MutableNumber> trader1 = summary.getMap().entrySet().stream().filter(entry -> ((Number) entry.getKey()).intValue() == 1).map(entry -> entry.getValue()).findAny().get();
        Wrapper<MutableNumber> trader2 = summary.getMap().entrySet().stream().filter(entry -> ((Number) entry.getKey()).intValue() == 2).map(entry -> entry.getValue()).findAny().get();
        Assert.assertThat(300, is(trader1.event().intValue));
        Assert.assertThat(92, is(trader2.event().intValue));
    }

    @org.junit.Test
    public void testGroupByMultipleOutputs() {
        EventHandler sep = buildAndInitSep(MatchResultCsvBuilder.class);
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
        GroupBy<MutableNumber> homeGoals = getField(VAR_SUM_HOMEGOALS);
        Wrapper<MutableNumber> liverpoolHome = homeGoals.getMap().entrySet().stream().filter(entry -> ((String) entry.getKey()).equals("liverpool")).map(entry -> entry.getValue()).findAny().get();
        Assert.assertThat(8, is(liverpoolHome.event().intValue));
    }

    @org.junit.Test
    public void testGroupByJoinToClassTarget() {
        EventHandler sep = buildAndInitSep(LeagueTableFromMatchCsv.class);
        StringDriver.streamChars(
                "wba,1,3,arsenal\n"
                + "wba,2,3,liverpool\n"
                + "arsenal,2,2,liverpool\n"
                + "arsenal,6,1,wba\n"
                + "liverpool,1,1,wba\n"
                + "liverpool,0,1,arsenal\n",
                 sep, false);
        GroupBy<LeaguePosition> league = getField(VAR_AGG_LEAGUEPOSITION);
        league.getMap().values().stream().map(w -> w.event())
                .sorted((l1, l2) -> {
                    if ((l2.totalPoints() - l1.totalPoints()) != 0) {
                        return l2.totalPoints() - l1.totalPoints();
                    } else if ((l2.goalDifference() - l1.goalDifference()) != 0) {
                        return l2.goalDifference() - l1.goalDifference();
                    } else if ((l2.awayPoints() - l1.awayPoints()) != 0) {
                        return l2.awayPoints() - l1.awayPoints();
                    }
                    return 0;
                })
                .forEach(l -> System.out.println(l.toString()));
        Map<String, LeaguePosition> leagueList = league.getMap().values().stream().map(w -> w.event()).collect(Collectors.toMap(LeaguePosition::getTeamName, Function.identity()));
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

    public static class TradeTextBuilder extends SEPConfig {

        {
            Ascii2IntTerminator traderId = readInt("trader_id=");
            Ascii2IntTerminator tradeSize = readInt("trade size=");
            TradeDetails tradeDetails = addPublicNode(new TradeDetails(traderId, tradeSize), VAR_TRADE_DEIAILS);
            //groupby
            GroupByBuilder<TradeDetails, MutableNumber> trades = groupBy(tradeDetails, TradeDetails::getTraderId, MutableNumber.class);
            trades.sum(TradeDetails::getTradeSize, MutableNumber::set);
            GroupBy<MutableNumber> tradesSummary = addPublicNode(trades.build(), VAR_TRADE_SUMMARY);
            //logging
//            Log(tradesSummary);
//            Log(tradeDetails);
        }
    }

    public static class MatchResultCsvBuilder extends SEPConfig {

        {
            MatchResult result = addNode(
                    new MatchResult(readBytesCsv(0), readIntCsv(1), readIntCsv(2), readBytesCsv(3)));
            //group by definition
            GroupByBuilder<MatchResult, MutableNumber> homeGoals = groupBy(result, MatchResult::getHomeTeamAsString, MutableNumber.class);
            GroupByBuilder<MatchResult, MutableNumber> awayGoals = groupBy(result, MatchResult::getHomeTeamAsString, MutableNumber.class);
            //calculate aggregate values
            homeGoals.sum(MatchResult::getHomeGoals, MutableNumber::set);
            awayGoals.sum(MatchResult::getAwayGoals, MutableNumber::set);
            //build
            final GroupBy<MutableNumber> homeGoalsGroup = homeGoals.build();
            final GroupBy<MutableNumber> awayGoalsGroup = awayGoals.build();
            //debugging - make public
            addPublicNode(homeGoalsGroup, VAR_SUM_HOMEGOALS);
            addPublicNode(awayGoalsGroup, VAR_SUM_AWAYGOALS);
            //logging - we need to cast to Function<MutableNumber, ?>  as java type inference is breaking down
            LambdaReflection.SerializableFunction<MutableNumber, ?> f = MutableNumber::intValue;
            Log(result);
            LogBuilder.buildLog("XX Team:'{}' for:{} against:{} XX", result, MatchResult::getHomeTeamAsString)
                    .input(homeGoalsGroup, f)
                    .input(awayGoalsGroup, f)
                    .build();
        }
    }

    public static class LeagueTableFromMatchCsv extends SEPConfig {

        {

            MatchResult result = addNode(
                    new MatchResult(readBytesCsv(0), readIntCsv(1), readIntCsv(2), readBytesCsv(3)));
            //gorupby
            GroupByBuilder<MatchResult, LeaguePosition> home = groupBy(result, MatchResult::getHomeTeam, LeaguePosition.class);
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
            GroupBy<LeaguePosition> league = home.build();
            addPublicNode(league, VAR_AGG_LEAGUEPOSITION);
        }
    }

}
