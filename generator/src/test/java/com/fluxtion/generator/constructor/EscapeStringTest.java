/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.generator.constructor;

import com.fluxtion.generator.util.BaseSepInprocessTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class EscapeStringTest extends BaseSepInprocessTest {

    private static String c1 = "\"t\t";
    private static String pubString = "''t\t\n\"\\";
    private static String propString = "''t\t\n\"\"£";
    private static final List<String> STRING_LIST = Arrays.asList("fhfh", "\"", "\t{}@~\\");
    private static final List<String> stringListProp = Arrays.asList("\"\\", "\t%\n");
    private static final String[] arrVals = new String[]{"\"", "e", "'#g\\£"};

    @Test
    public void escapeTest() {
        sep(cfg -> {
            final SampleData data = new SampleData(c1);
            final SampleData data2 = new SampleData("hhh", STRING_LIST, arrVals);
            data.pubString = pubString;
            data.setPropString(propString);
            data.setStringListProp(stringListProp);
            cfg.addPublicNode(data, "data");
            cfg.addPublicNode(data2, "data2");
            cfg.addPublicNode(new CharData(('\n')), "charData");
        });
        SampleData data1 = getField("data");
        SampleData data2 = getField("data2");
        CharData charData = getField("charData");
        assertEquals(c1, data1.c1);
        assertEquals(pubString, data1.pubString);
        assertEquals(propString, data1.getPropString());
        assertEquals(stringListProp, data1.getStringListProp());
        assertEquals(STRING_LIST, data2.stringList);
        assertArrayEquals(arrVals, data2.arrVals);
        assertEquals('\n', charData.getC());
    }

    @Test
    public void stringAsNode() {
        sep(cfg -> {
            cfg.addNode("TEST", "str");
            cfg.addNode("\\u001%t", "str2");
        });
        assertEquals("TEST", getField("str"));
        assertEquals("\\u001%t", getField("str2"));
    }

    public static class CharData {

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
}
