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
package com.fluxtion.ext.futext.builder.test.helpers;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.ext.streaming.api.numeric.BufferValue;
import com.fluxtion.ext.text.api.event.CharEvent;

/**
 *
 * @author Greg Higgins
 */
public class MatchResult {

    public Number homeGoals;
    public Number awayGoals;
    public BufferValue homeTeam;
    public BufferValue awayTeam;
    private int homeWin;
    private int homeLoss;
    private int draw;

    public MatchResult(BufferValue homeTeam, Number homeGoals, Number awayGoals, BufferValue awayTeam) {
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public MatchResult() {
    }

    @EventHandler(filterId = '\n')
    public boolean onEol(CharEvent event) {
        homeWin = getHomeGoals() > getAwayGoals() ? 1 : 0;
        homeLoss = getAwayGoals() > getHomeGoals()  ? 1 : 0;
        draw = getHomeGoals() == getAwayGoals() ? 1 : 0;
        return true;
    }

//    @OnEvent
    public boolean afterUpdate() {
        return true;
    }

    public int getHomeGoals() {
        return homeGoals.intValue();
    }

    public int getAwayGoals() {
        return awayGoals.intValue();
    }

    public BufferValue getHomeTeam() {
        return homeTeam;
    }

    public BufferValue getAwayTeam() {
        return awayTeam;
    }

    public String getHomeTeamAsString() {
        return homeTeam.asString();
    }

    public String getAwayTeamAsString() {
        return awayTeam.asString();
    }

    public int getHomeWin() {
        return homeWin;
    }

    public int getHomeLoss() {
        return homeLoss;
    }

    public int getAwayWin() {
        return getHomeLoss();
    }

    public int getAwayLoss() {
        return getHomeWin();
    }

    public int getDraw() {
        return draw;
//        return getHomeGoals() == getAwayGoals() ? 1 : 0;
    }

    @Override
    public String toString() {
        return "MatchResult{" + "homeGoals=" + homeGoals.intValue()
                + ", awayGoals=" + awayGoals.intValue()
                + ", homeTeam=" + homeTeam.asString()
                + ", awayTeam=" + awayTeam.asString()
                + '}';
    }

}
