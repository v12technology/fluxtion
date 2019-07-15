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
package com.fluxtion.ext.futext.builder.filter;

import com.fluxtion.builder.node.DeclarativeNodeConfiguration;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.text.builder.ascii.AsciiMatchFilterFactory;
import com.fluxtion.ext.text.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.ext.futext.builder.test.helpers.TextMatchPrinter;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class TextMatchTest extends BaseSepTest {

    public static final String MATCHER_VARIABLE_NAME = "matcher";
    public static final String RESULT_VARIABLE_NAME = "result";

    @Test
    public void countTest1() {
        System.out.println("testSelectAndAlwaysNotify");
        EventHandler sep = buildAndInitSep(Builder.class);
        TextMatchPrinter textPrinter = getField(MATCHER_VARIABLE_NAME);

        String testString = "no matches";
        StringDriver.streamChars(testString, sep);
        assertFalse(textPrinter.isMatch());

        testString = "start ccypair=\"eurusd\" end";
        StringDriver.streamChars(testString, sep);
        assertTrue(textPrinter.isMatch());

    }

    public static class Builder extends SEPConfig {

        {
            addPublicNode(new TextMatchPrinter("ccypair=\"eurusd\""), MATCHER_VARIABLE_NAME);
            Set<Class<? extends NodeFactory>> factoryList = new HashSet<>();
            factoryList.add(AsciiMatchFilterFactory.class);
            declarativeConfig = new DeclarativeNodeConfiguration(null, factoryList, null);
        }
    }


}
