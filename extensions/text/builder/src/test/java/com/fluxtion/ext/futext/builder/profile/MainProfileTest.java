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
package com.fluxtion.ext.futext.builder.profile;

//import com.fluxtion.extension.fucntional.test.aggregated.generated.test3.MatchResultCsvProcessor;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.builder.util.StringDriver;

/**
 *
 * @author Greg Higgins
 */
public class MainProfileTest {

    public static final String PACKAGE_NAME_TEST = "com.fluxtion.extension.fucntional.test.aggregated.generated.test";
    public static final String PACKAGE_NAME_TEST_1 = PACKAGE_NAME_TEST + "1";
    public static final String PACKAGE_NAME_TEST_2 = PACKAGE_NAME_TEST + "2";
    public static final String PACKAGE_NAME_TEST_3 = PACKAGE_NAME_TEST + "3";
    public static final String MATCHRESULT_CSV_PROCESSOR = "MatchResultCsvProcessor";
    public static final String FQN_LEAGUEPOSITION_CSV_PROCESSOR = PACKAGE_NAME_TEST_3 + "." + MATCHRESULT_CSV_PROCESSOR;

    public static void main(String[] args) throws InstantiationException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException, InterruptedException {
        System.out.println("Integration test::testTradeDetailsProcessor");
        Class<?> clazz = Class.forName("com.fluxtion.extension.fucntional.test.aggregated.generated.test3.MatchResultCsvProcessor");
        final EventHandler sep = (EventHandler) clazz.newInstance();
//        AggregateInstance< LeaguePosition> leaguPos = (AggregateInstance< LeaguePosition>) clazz.getField(AggregateTest.VAR_AGG_LEAGUEPOSITION).get(sep);

        StringDriver.initSep(sep);
        String testString = "liverpool,2,3,everton\n"
                + "arsenal,1,3,everton\n"
                + "liverpool,6,1,tottenham\n"
                + "everton,0,9,west ham\n"
                + "wba,2,9,arsenal\n"
                + "wba,6,9,arsenal\n"
                + "Manchester United,2,1,Manchester city\n"
                + "Manchester United,4,6,arsenal\n"
                + "west ham,6,1,arsenal\n";

        char[] chars = testString.toCharArray();
        CharEvent charEvent = new CharEvent(' ');

        while (true) {
            for (int i = 0; i < chars.length; i++) {
                char aChar = chars[i];
                charEvent.setCharacter(aChar);
                sep.onEvent(charEvent);
            }
//            Thread.sleep(1);
        }
    }
}
