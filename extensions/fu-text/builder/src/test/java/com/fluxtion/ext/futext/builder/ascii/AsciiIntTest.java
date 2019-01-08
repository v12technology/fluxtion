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
package com.fluxtion.ext.futext.builder.ascii;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.futext.api.ascii.Ascii2DoubleFixedLength;
import com.fluxtion.ext.futext.api.ascii.Ascii2IntFixedLength;
import com.fluxtion.ext.futext.api.ascii.EolNotifier;
import com.fluxtion.ext.futext.api.filter.DefaultAsciiMatchFilter;
import com.fluxtion.ext.futext.builder.ascii.AnyCharMatchFilterFactory;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readDouble;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readDoubleCsv;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readInt;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readIntCsv;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readIntDelimited;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readIntFixedLength;
import com.fluxtion.ext.futext.builder.ascii.AsciiMatchFilterFactory;
import static com.fluxtion.ext.futext.builder.math.CumSumFunctions.cumSum;
import com.fluxtion.ext.futext.builder.math.MultiplyFunctions;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.ext.futext.builder.test.helpers.SoldAggregator;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class AsciiIntTest extends BaseSepTest {

    public static final String INT_VARIABLE_NAME = "intParser";
    public static final String INT_CUMSUM_VARIABLE_NAME = "intCumSum";
    public static final String VAR_TOTAL_SALES = "totalSales";
    public static final String VAR_VAT_RATE = "vatRate";
    public static final String VAR_DAILY_SALES = "dailySales";
    public static final String VAR_AGGREGATED_SALES = "salesSummary";
    public static final String DOUBLE_VARIABLE_NAME = "doubleParser";
    public static final String PARSE_THIS_LINE = "parse this line";

    @Test
    public void testMatch() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        System.out.println("Integration test::testMatch");
        final EventHandler sep = buildAndInitSep(AsciiMatcherTestSep.class);
        Ascii2IntFixedLength intVal = getField(INT_VARIABLE_NAME);
        Ascii2DoubleFixedLength doubleVal = getField(DOUBLE_VARIABLE_NAME);
        NumericValue cumSum = getField(INT_CUMSUM_VARIABLE_NAME);

        StringDriver.initSep(sep);

        StringDriver.streamChars("junk number=2568 stuff", sep, false);
        assertEquals(0, intVal.intValue());
        assertEquals(0, doubleVal.doubleValue(), 0.0001);

        StringDriver.streamChars(PARSE_THIS_LINE, sep, false);
        StringDriver.streamChars("number=2568 stuff", sep, false);
        assertEquals(2568, intVal.intValue());
        assertEquals(0, doubleVal.doubleValue(), 0.0001);
        assertEquals(0, cumSum.intValue());

        StringDriver.streamChars(PARSE_THIS_LINE, sep, false);
        StringDriver.streamChars("d=32.63 stuff", sep, false);
        assertEquals(2568, intVal.intValue());
        assertEquals(32.63, doubleVal.doubleValue(), 0.0001);
        assertEquals(32, cumSum.intValue());

        StringDriver.streamChars(PARSE_THIS_LINE, sep, false);
        StringDriver.streamChars("d=14.00 stuff", sep, false);
        assertEquals(2568, intVal.intValue());
        assertEquals(14, doubleVal.doubleValue(), 0.0001);
        assertEquals(46, cumSum.intValue());

    }

    @Test
    public void testMatch_NoFilter() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        System.out.println("Integration test::testMatch_NoFilter");

        final EventHandler sep = buildAndInitSep(AsciiMatcherNoNotifierTestSep.class);
        Ascii2IntFixedLength intVal = getField(INT_VARIABLE_NAME);

        StringDriver.initSep(sep);
        StringDriver.streamChars("junk number=2568 stuff", sep, false);
        assertEquals(2568, intVal.intValue());

        StringDriver.streamChars(PARSE_THIS_LINE, sep, false);
        StringDriver.streamChars("number=6582 stuff", sep, false);
        assertEquals(6582, intVal.intValue());
    }

    @Test
    /**
     * Multi-line test, data spans multiple lines and "eod day_id=" is reset
     * notifier for daily sales.
     */
    public void testSalesLogProcessor() throws Exception {
        System.out.println("Integration test::testSalesLogProcessor");
        final EventHandler sep = buildAndInitSep(SalesLogProcessor.class);
        SoldAggregator aggSales = getField(VAR_AGGREGATED_SALES);
        StringDriver.streamChars("     eod day_id=-01 \n"
                + "     sold volume=250\n"
                + "     sold volume=1500\n"
                + "     sold volume=1500\n"
                + "     total day 001 3250 \n"
                + "     eod day_id=002 \n"
                + "     sold volume=3000 \n"
                + "     sold volume=-2000 \n"
                + "     sold volume=7000 \n"
                + "     sold volume=4000 \n"
                + "     total day 002 12000 \n"
                + "     eod day_id=003 \n"
                + "     sold volume=4000 \n"
                + "     sold volume=4000 \n"
                + "     total day 003 8000 \n"
                + "     eod day_id=004 \n"
                + "     sold volume=2000 \n"
                + "     sold volume=4000 \n"
                + "     sold volume=2000 \n"
                + "     total day 004 8000 \n"
                + "VAT:12.5%"
                + "     grand total = 31250 ", sep, false);

        System.out.println(aggSales);
        assertEquals(12.5, aggSales.vatRate.doubleValue(), 0.0001);
        assertEquals(3250, aggSales.getDaySales().get(-1).intValue());
        assertEquals(12000, aggSales.getDaySales().get(2).intValue());
        assertEquals(8000, aggSales.getDaySales().get(3).intValue());
        assertEquals(8000, aggSales.getDaySales().get(4).intValue());
        assertEquals(31250, aggSales.salesVolumeTotal.intValue());

    }

    @Test
    public void testCsvDoubleField() {
        final EventHandler sep = buildAndInitSep(CsvProcessor1.class);
        NumericValue vatRate = getField(VAR_VAT_RATE);
        NumericValue aggSales = getField(VAR_TOTAL_SALES);

        StringDriver.streamChars("12.25,10, 25 ,3,45", sep, false);
        assertEquals(250, aggSales.intValue());
        assertEquals(12.25, vatRate.doubleValue(), 0.0001);
        StringDriver.streamChars("11.5,12,3\n", sep, false);
        assertEquals(250, aggSales.intValue());
        assertEquals(12.25, vatRate.doubleValue(), 0.0001);
        StringDriver.streamChars("6.5,10,35\n", sep, false);
        assertEquals(350, aggSales.intValue());
        assertEquals(6.5, vatRate.doubleValue(), 0.0001);
    }

    @Test
    public void testCsvDoubleField_0() {
        final EventHandler sep = buildAndInitSep(CsvProcessor1.class);
        NumericValue vatRate = getField(VAR_VAT_RATE);
        StringDriver.streamChars("1.1\n", sep, false);
        assertEquals(1.1, vatRate.doubleValue(), 0.0001);
        StringDriver.streamChars("2.2\n", sep, false);
        assertEquals(2.2, vatRate.doubleValue(), 0.0001);
        StringDriver.streamChars("3.3,", sep, false);
        assertEquals(3.3, vatRate.doubleValue(), 0.0001);
    }

    @Test
    public void testTabsSeparated0() {
        final EventHandler sep = buildAndInitSep(TabsvProcessor1.class);
        NumericValue aggSales = getField(VAR_TOTAL_SALES);
        StringDriver.streamChars("300\t10\t25\t3\t45", sep, false);
        assertEquals(250, aggSales.intValue());
    }

    @Test
    public void testCsvMultiplicationWithHeader() throws Exception {
        System.out.println("Integration test::testCsvMultiplication");
        final EventHandler sep = buildAndInitSep(CsvProcessorWithHeader.class);
        NumericValue aggSales = getField(VAR_TOTAL_SALES);

        assertEquals(0, aggSales.intValue());
        StringDriver.streamChars("300,10, 25 ,3,45\n", sep, false);
        assertEquals(0, aggSales.intValue());
        StringDriver.streamChars("300,10, 25 ,3,45\n", sep, false);
        assertEquals(0, aggSales.intValue());
        StringDriver.streamChars("300,10, 25 ,3,45", sep, false);
        assertEquals(250, aggSales.intValue());
        StringDriver.streamChars("300,12,3\n", sep, false);
        assertEquals(250, aggSales.intValue());
        StringDriver.streamChars("300,10,35\n", sep, false);
        assertEquals(350, aggSales.intValue());
    }

    @Test
    public void testCsvWithEol() throws Exception {
        System.out.println("Integration test::testCsvWithEol");
        final EventHandler sep = buildAndInitSep(CsvWithLineProcessor.class);
        Ascii2IntFixedLength intVal = getField(INT_VARIABLE_NAME);
        NumericValue cumSum = getField(INT_CUMSUM_VARIABLE_NAME);

        StringDriver.streamChars("\njunk number=2568 stuff", sep, false);
        assertEquals(2568, intVal.intValue());
        assertEquals(2568, cumSum.intValue(), 0.0001);

        StringDriver.streamChars("\njunk number=1000 stuff", sep, false);
        assertEquals(1000, intVal.intValue());
        assertEquals(3568, cumSum.intValue(), 0.0001);

        //should ignore as no line break
        StringDriver.streamChars("junk number=1000 stuff", sep, false);
        assertEquals(1000, intVal.intValue());
        assertEquals(3568, cumSum.intValue(), 0.0001);

        //add a few lines in one go
        StringDriver.streamChars("\njunk number=2000 stuff\n\njunk number=3000 stuff", sep, false);
        assertEquals(3000, intVal.intValue());
        assertEquals(8568, cumSum.intValue(), 0.0001);

    }

    public static class AsciiMatcherTestSep extends SEPConfig {

        {
            declarativeConfig = BaseSepTest.factorySet(AsciiMatchFilterFactory.class, AnyCharMatchFilterFactory.class);
            DefaultAsciiMatchFilter notifier = addNode(new DefaultAsciiMatchFilter(PARSE_THIS_LINE));
            Ascii2IntFixedLength a2Int = addPublicNode(new Ascii2IntFixedLength(notifier, (byte) 4, "number="), INT_VARIABLE_NAME);
            Ascii2DoubleFixedLength a2Double = addPublicNode(new Ascii2DoubleFixedLength(notifier, (byte) 4, "d="), DOUBLE_VARIABLE_NAME);
            addPublicNode(cumSum(a2Double), INT_CUMSUM_VARIABLE_NAME);
        }
    }

    public static class AsciiMatcherNoNotifierTestSep extends SEPConfig {

        {
            declarativeConfig = BaseSepTest.factorySet(AsciiMatchFilterFactory.class, AnyCharMatchFilterFactory.class);
            Ascii2IntFixedLength instance = addPublicNode(new Ascii2IntFixedLength(null, (byte) 4, "number="), INT_VARIABLE_NAME);
        }
    }

    public static class SalesLogProcessor extends SEPConfig {

        {
            SoldAggregator aggregator = addPublicNode(new SoldAggregator(), VAR_AGGREGATED_SALES);
            aggregator.dayId = readIntFixedLength("eod day_id=", 3);
            aggregator.salesVolumeDaily = cumSum(readInt("sold volume="), readIntFixedLength("eod day_id=", 3));
            aggregator.salesVolumeTotal = cumSum(readInt("sold volume="));
            aggregator.vatRate = readDouble("VAT:", " \n\t%");
        }
    }

    public static class CsvProcessor1 extends SEPConfig {

        {
            try {
                addPublicNode(readDoubleCsv(0), VAR_VAT_RATE);
                addPublicNode(MultiplyFunctions.multiply(readIntCsv(2), readIntCsv(1)), VAR_TOTAL_SALES);
            } catch (Exception ex) {
                throw new RuntimeException();
            }
        }
    }

    public static class TabsvProcessor1 extends SEPConfig {

        {
            try {
                addPublicNode(MultiplyFunctions.multiply(readIntDelimited(2, "\t"), readIntDelimited(1, "\t")), VAR_TOTAL_SALES);
            } catch (Exception ex) {
                throw new RuntimeException();
            }
        }
    }

    public static class CsvProcessorWithHeader extends SEPConfig {

        {
            try {
                addPublicNode(MultiplyFunctions.multiply(readIntCsv(2, 2), readIntCsv(1, 2)), VAR_TOTAL_SALES);
            } catch (Exception ex) {
                throw new RuntimeException();
            }
        }
    }

    public static class CsvWithLineProcessor extends SEPConfig {

        {
            try {
                EolNotifier notifier = addNode(new EolNotifier());
                Ascii2IntFixedLength a2Int = addPublicNode(new Ascii2IntFixedLength(notifier, (byte) 4, "number="), INT_VARIABLE_NAME);
                addPublicNode(cumSum(a2Int), INT_CUMSUM_VARIABLE_NAME);
            } catch (Exception ex) {
                throw new RuntimeException();
            }
        }
    }

}
