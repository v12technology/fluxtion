package com.fluxtion.ext.futext.builder.ascii;

import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.stream.StreamBuilder.stream;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.multiply;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.cumSum;
import com.fluxtion.ext.futext.api.ascii.Ascii2DoubleFixedLength;
import com.fluxtion.ext.futext.api.ascii.Ascii2IntFixedLength;
import com.fluxtion.ext.futext.api.ascii.EolNotifier;
import com.fluxtion.ext.futext.api.filter.DefaultAsciiMatchFilter;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readDouble;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readDoubleCsv;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readInt;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readIntCsv;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readIntDelimited;
import static com.fluxtion.ext.futext.builder.ascii.AsciiHelper.readIntFixedLength;
import com.fluxtion.ext.futext.builder.test.helpers.SoldAggregator;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class AsciiMathTest extends BaseSepInprocessTest {

    public static final String INT_VARIABLE_NAME = "intParser";
    public static final String INT_CUMSUM_VARIABLE_NAME = "intCumSum";
    public static final String VAR_TOTAL_SALES = "totalSales";
    public static final String VAR_VAT_RATE = "vatRate";
    public static final String VAR_DAILY_SALES = "dailySales";
    public static final String VAR_AGGREGATED_SALES = "salesSummary";
    public static final String DOUBLE_VARIABLE_NAME = "doubleParser";
    public static final String PARSE_THIS_LINE = "parse this line";

    @Test
    public void testCsvField() {
        sep((cfg) -> {
            stream(readDoubleCsv(0)).id("vatRate");
            multiply(readIntCsv(2)::doubleValue, readIntCsv(1)::doubleValue).id("multiply");
        });
        Wrapper<Number> vatRate = getField("vatRate");
        Wrapper<Number> multiply = getField("multiply");
        StringDriver.streamChars("1.1\n", sep, false);
        assertEquals(1.1, vatRate.event().doubleValue(), 0.0001);
        StringDriver.streamChars("2.2\n", sep, false);
        assertEquals(2.2, vatRate.event().doubleValue(), 0.0001);
        StringDriver.streamChars("3.3,", sep, false);
        assertEquals(3.3, vatRate.event().doubleValue(), 0.0001);
        //multiply
        StringDriver.streamChars("10,10\n", sep, false);
        assertEquals(100, multiply.event().doubleValue(), 0.0001);

    }

    @Test
    public void testWithHeader() {
        sep((cfg) -> {
            multiply(readIntCsv(2, 2)::doubleValue, readIntCsv(1, 2)::doubleValue).id("totalSales");
        });

        Number aggSales = ((Wrapper<Number>) getField("totalSales")).event();
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
    public void testTabsSeparated0() {
        sep((cfg) -> {
            multiply(readIntDelimited(2, "\t")::doubleValue, readIntDelimited(1, "\t")::doubleValue).id("totalSales");
        });

        Number aggSales = ((Wrapper<Number>) getField("totalSales")).event();
        StringDriver.streamChars("300\t10\t25\t3\t45", sep, false);
        assertEquals(250, aggSales.intValue());
    }

    @Test
    public void testCsvWithEol() throws Exception {
        System.out.println("Integration test::testCsvWithEol");

        sep((cfg) -> {
            EolNotifier notifier = cfg.addNode(new EolNotifier());
            Ascii2IntFixedLength a2Int = cfg.addPublicNode(new Ascii2IntFixedLength(notifier, (byte) 4, "number="), "intParser");
            cumSum(a2Int::intValue).id("intCumSum");
        });

        Ascii2IntFixedLength intVal = getField("intParser");
        Number cumSum = ((Wrapper<Number>) getField("intCumSum")).event();

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

    @Test
    public void testMatch() {
        sep((cfg) -> {
            DefaultAsciiMatchFilter notifier = cfg.addNode(new DefaultAsciiMatchFilter(PARSE_THIS_LINE));
            Ascii2IntFixedLength a2Int = cfg.addPublicNode(new Ascii2IntFixedLength(notifier, (byte) 4, "number="), INT_VARIABLE_NAME);
            Ascii2DoubleFixedLength a2Double = cfg.addPublicNode(new Ascii2DoubleFixedLength(notifier, (byte) 4, "d="), DOUBLE_VARIABLE_NAME);
            cumSum(a2Double::doubleValue).id(INT_CUMSUM_VARIABLE_NAME);
        });

        Ascii2IntFixedLength intVal = getField(INT_VARIABLE_NAME);
        Ascii2DoubleFixedLength doubleVal = getField(DOUBLE_VARIABLE_NAME);
        Number cumSum = ((Wrapper<Number>) getField(INT_CUMSUM_VARIABLE_NAME)).event();

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
    public void testMatch_NoFilter() {
        sep((cfg) -> {
            cfg.addPublicNode(new Ascii2IntFixedLength(null, (byte) 4, "number="), INT_VARIABLE_NAME);
        });

        Ascii2IntFixedLength intVal = getField(INT_VARIABLE_NAME);

        StringDriver.streamChars("junk number=2568 stuff", sep, false);
        assertEquals(2568, intVal.intValue());

        StringDriver.streamChars(PARSE_THIS_LINE, sep, false);
        StringDriver.streamChars("number=6582 stuff", sep, false);
        assertEquals(6582, intVal.intValue());
    }

    @Test
    public void testSalesLogProcessor() {
        sep((cfg) -> {
            SoldAggregator aggregator = cfg.addPublicNode(new SoldAggregator(), VAR_AGGREGATED_SALES);
            aggregator.dayId = readIntFixedLength("eod day_id=", 3);
            aggregator.salesVolumeDailyWrapper =  cumSum(readInt("sold volume=")::intValue).resetNotifier(aggregator.dayId);
            aggregator.salesVolumeTotalNumber = cumSum(readInt("sold volume=")::intValue);
            aggregator.vatRate = readDouble("VAT:", " \n\t%");
        });
        
//        fixedPkg = true;
//        sep(com.fluxtion.ext.futext.builder.ascii.asciimathtest_testsaleslogprocessor.TestSep_testSalesLogProcessor.class);
        
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

}
