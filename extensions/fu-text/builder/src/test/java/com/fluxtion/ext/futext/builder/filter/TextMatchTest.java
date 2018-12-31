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

import com.fluxtion.api.node.DeclarativeNodeConiguration;
import com.fluxtion.api.node.NodeFactory;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.futext.builder.ascii.AsciiMatchFilterFactory;
import com.fluxtion.ext.futext.builder.math.AddFunctions;
import com.fluxtion.ext.futext.builder.math.MultiplyFunctions;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.ext.futext.builder.test.helpers.DataEvent;
import com.fluxtion.ext.futext.builder.test.helpers.TextMatchPrinter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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

    @Test
    public void mathFunctions() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        System.out.println("Integration test::mathFunctions");
        EventHandler sep = buildAndInitSep(NumericBuilder.class);
        NumericValue textPrinter = getField(RESULT_VARIABLE_NAME);

        DataEvent event = new DataEvent();
        event.value = 10;
        sep.onEvent(event);

        assertThat(textPrinter.doubleValue(), is(17.5));
    }

    public static class Builder extends SEPConfig {

        {
            addPublicNode(new TextMatchPrinter("ccypair=\"eurusd\""), MATCHER_VARIABLE_NAME);
            Set<Class<? extends NodeFactory>> factoryList = new HashSet<>();
            factoryList.add(AsciiMatchFilterFactory.class);
            declarativeConfig = new DeclarativeNodeConiguration(null, factoryList, null);
        }
    }

    public static class NumericBuilder extends SEPConfig {

        {
            NumericValue addNode;
            try {
                addNode = addNode(AddFunctions.add(25, DataEvent.class, DataEvent::getValue));
                addPublicNode(MultiplyFunctions.multiply(0.5, addNode), RESULT_VARIABLE_NAME);
            } catch (Exception ex) {
                Logger.getLogger(TextMatchTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
