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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.constructor;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class EscapeStringTest extends BaseSepTest {

    private static String c1 = "\"t\t";
    private static String pubString = "''t\t\n\"\\";
    private static String propString = "''t\t\n\"\"£";
    private static final List<String> STRING_LIST = Arrays.asList("fhfh", "\"", "\t{}@~\\");
    private static final List<String> stringListProp = Arrays.asList("\"\\", "\t%\n");
    private static final String[] arrVals = new String[]{"\"", "e", "'#g\\£"};

    @Test
    public void escapeTest() {
        buildAndInitSep(StringBuilderSep.class);
        SampleData data1 = getField("data");
        SampleData data2 = getField("data2");
        CharData charData = getField("charData");
        Assert.assertEquals(c1, data1.c1);
        Assert.assertEquals(pubString, data1.pubString);
        Assert.assertEquals(propString, data1.getPropString());
        Assert.assertEquals(stringListProp, data1.getStringListProp());
        Assert.assertEquals(STRING_LIST, data2.stringList);
        Assert.assertArrayEquals(arrVals, data2.arrVals);
        Assert.assertEquals('\n', charData.getC());
    }

    
    public static class CharData{
        private final char c;

        public CharData(char c) {
            this.c = c;
        }

        public char getC() {
            return c;
        }
        
    }
    
    public static class SampleData {

        private final String c1;
        private final String[] arrVals;
        public List<String> stringList;
        private List<String> stringListProp;
        public String pubString;
        private String propString;

        public SampleData(String c1, List<String> stringList, String[] arrVals) {
            this.c1 = c1;
            this.stringList = stringList;
            this.arrVals = arrVals;
        }

        public SampleData(String c1, String[] arrVals) {
            this.c1 = c1;
            this.arrVals = arrVals;
            this.stringList = new ArrayList<>();
        }

        public SampleData(String c1) {
            this(c1, null, null);
        }

        public String getPropString() {
            return propString;
        }

        public void setPropString(String propString) {
            this.propString = propString;
        }

        public List<String> getStringListProp() {
            return stringListProp;
        }

        public void setStringListProp(List<String> stringListProp) {
            this.stringListProp = stringListProp;
        }

    }

    public static class StringBuilderSep extends SEPConfig {

        @Override
        public void buildConfig() {
            final SampleData data = new SampleData(c1);
            final SampleData data2 = new SampleData("hhh", STRING_LIST, arrVals);
            data.pubString = pubString;
            data.setPropString(propString);
            data.setStringListProp(stringListProp);
            addPublicNode(data, "data");
            addPublicNode(data2, "data2");
            addPublicNode(new CharData(('\n')), "charData");
        }

    }
}
