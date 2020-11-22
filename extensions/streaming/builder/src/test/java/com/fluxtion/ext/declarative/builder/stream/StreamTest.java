package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.lt;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.negative;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.num;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.positive;
import static com.fluxtion.ext.streaming.api.stream.StringPredicates.is;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.sepTestInstance;
import java.util.Objects;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class StreamTest implements Stateful {
    
    @Test
    public void tempMonitorTest() throws IllegalAccessException, Exception {
        StaticEventProcessor handler = sepTestInstance((c) -> {
            //convert to C from F
            Wrapper<Number> tempC = select(TempF.class)
//                    .filter(TempF::getSensorId, is("outside"))
                    .filter(TempF::getSensorId, "outside"::equals)
                    .filter(TempF::getFahrenheit, StreamTest::gt10)
                    .filter(TempF::getFahrenheit, num(20, "lt_id1")::lessThan)
                    .map(StreamTest::fahrToCentigrade);
            //convert to log temps
//            StreamTest instance = c.addNode(new StreamTest());
            Wrapper<Double> logTemp = select(TempF.class)
                    .map(Math::log, TempF::getFahrenheit)
                    .map(new StreamTest()::cumSum)
                    .resetAndPublish(select(DataEvent.class).console("[reset] ->"))
                    .console("[cum sum] ->");
            //on change
            select(TempF.class)
                    .map(Math::log, TempF::getFahrenheit)
                    .notifyOnChange(true)
                    .map(new StreamTest()::cumSum).resetAndPublish(select(DataEvent.class))
                    .console("[cum sum notifyOnChange] ->");

            c.addPublicNode(logTemp, "cumLogTemp");
            //control signals depend on temp value
//            Log("reading temp:{}C", tempC, Number::intValue);
//            Log("log(temp):{}C", logTemp);
//            Log("hot aircon on!!!! temp:{} C", tempC.filter(gt(27)), Number::intValue);
//            Log("cold heating on!!!! temp:{} C", tempC.filter(lt(5)), Number::intValue);
//            Log("ice melts!!!! temp:{} C", tempC.filter(positive()), Number::intValue);
        }, "com.fluxtion.ext.declarative.builder.tempsensortest", "TempMonitor");
        //fire some data in
        handler.onEvent(new TempF(10, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        //reset
        handler.onEvent(new DataEvent());
        handler.onEvent(new TempF(100, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        //TODO write tests and move to tutorial
    }

    @Test
    public void testMapToClass() throws Exception {
        StaticEventProcessor handler = sepTestInstance((c) -> {
            //convert to C from F
            Wrapper<TempC> tempC = select(TempF.class).id("tempIn")
                    .console("[f] ->")
                    .map(StreamTest::tempFtoTempC).id("convert_FtoC")
                    .console("[c] ->");
            //control signals depend on temp value
        }, "com.fluxtion.ext.declarative.builder.tempMapToClass", "TempConverter");
        handler.onEvent(new TempF(10, "outside"));
        handler.onEvent(new TempF(32, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        handler.onEvent(new TempF(100, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        //TODO write tests and move to tutorial
    }

    @Test
    public void consumeTest() throws IllegalAccessException, Exception {
        StaticEventProcessor handler = sepTestInstance((c) -> {
            //convert to C from F
            Wrapper<Double> tempC = select(TempF.class)
                    .console("\n[1.TempF] ->")
                    .filter(TempF::getSensorId, is("outside"))
                    .console("[2.sensorId='outside'] ->")
                    .filter(TempF::getFahrenheit, StreamTest::gt10)
                    .console("[3.temp>10] ->")
                    .map(StreamTest::fahrToCentigrade)
                    .console("[4.degC] ->");
            //convert to log temps

        }, "com.fluxtion.ext.declarative.builder.tempsensorconsumer", "TempConsumer");
//        //fire some data in
        handler.onEvent(new TempF(10, "outside"));
        handler.onEvent(new TempF(32, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        handler.onEvent(new TempF(100, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        //TODO write tests and move to tutorial
    }

    @Test
    @Ignore
    public void testCumSum() throws Exception {
        String className = "com.fluxtion.ext.declarative.builder.filterstaeful.StatefulFilter";
        StaticEventProcessor handler = (StaticEventProcessor) Class.forName(className).newInstance();
        ((Lifecycle) handler).init();
        handler.onEvent(new DataEvent(10));
        handler.onEvent(new DataEvent(50));
        handler.onEvent(new DataEvent(15));
        handler.onEvent(new DataEvent(18));
        handler.onEvent(new DataEvent(5));
        handler.onEvent(new TempF(10, "outside"));
        handler.onEvent(new DataEvent(5));
        handler.onEvent(new DataEvent(5));
        handler.onEvent(new DataEvent(5));
        //TODO write tests and move to tutorial
    }

    @Test
    public void graphOfStreamsTest() throws Exception {
        StaticEventProcessor handler = sepTestInstance((c) -> {
            Wrapper<DataEvent> f = select(DataEvent.class)
                    .console("[data in] ->")
                    .filter(DataEvent::getValue, positive()).id("temp_AboveZero")
                    .filter(DataEvent::getValue, lt(20)).id("tempLT20")
                    .console("[val: +ve and <20] ->");
            //tee1
            f.filter(StreamTest::validData).id("validateData1")
                    .filter(DataEvent::getValue, gt(-20)).id("temp_Above_Neg20")
                    .filter(DataEvent::getValue, negative()).id("temp_BelowZero");
            //tee 2
            f.filter(new StreamTest()::ignoreFirsTwo).id("ignoreFirst2Events")
                    .resetAndPublish(select(TempF.class).console("[reset event] ->"))
                    .console("[ignored first two] ->");
            //tee 3
            f.filter(StreamTest::validData).id("validateData2")
                    .filter(DataEvent::getValue, gt(20)).id("temp_Above_20");
        }, "com.fluxtion.ext.declarative.builder.filterstaeful", "StatefulFilter");
        handler.onEvent(new DataEvent(10));
        handler.onEvent(new DataEvent(50));
        handler.onEvent(new DataEvent(15));
        handler.onEvent(new DataEvent(18));
        handler.onEvent(new DataEvent(5));
        handler.onEvent(new TempF(10, "outside"));
        handler.onEvent(new DataEvent(5));
        handler.onEvent(new DataEvent(5));
        handler.onEvent(new DataEvent(5));
        //TODO write tests and move to tutorial
    }

    @Test
    public void notifyOnChangeFilter() throws Exception {
        StaticEventProcessor handler = sepTestInstance((c) -> {
            //notify when > 20 on breach only
            Wrapper tempC = select(TempF.class).id("tempIn")
                    .console("\n[1.TempF] ->")
                    .filter(TempF::getFahrenheit, gt(20)).id("breach20C")
                    .notiferMerge(select(DataEvent.class).id("logTrigger").console("\n[trigger event]"))
                    .console("[2.temp>20] ->")
                    .map(new StreamTest()::max, TempF::getFahrenheit).notifyOnChange(true).id("maxTemp")
                    .console("[3.new max temp] ->");
            //convert to log temps

        }, "com.fluxtion.ext.declarative.builder.filternotify", "FilterNotifyOnChange");
//        //fire some data in
        handler.onEvent(new TempF(10, "outside"));
        handler.onEvent(new TempF(32, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        handler.onEvent(new TempF(100, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        handler.onEvent(new DataEvent(12));
        handler.onEvent(new TempF(40, "outside"));
        //TODO write tests and move to tutorial
    }

    public static class TempF {

        double fahrenheit;
        String sensorId;

        public TempF(double fahrenheit, String sensorId) {
            this.fahrenheit = fahrenheit;
            this.sensorId = sensorId;
        }

        public double getFahrenheit() {
            return fahrenheit;
        }

        public void setFahrenheit(double fahrenheit) {
            this.fahrenheit = fahrenheit;
        }

        public String getSensorId() {
            return sensorId;
        }

        public void setSensorId(String sensorId) {
            this.sensorId = sensorId;
        }

        @Override
        public String toString() {
            return "TempF{" + "fahrenheit=" + fahrenheit + ", sensorId=" + sensorId + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (int) (Double.doubleToLongBits(this.fahrenheit) ^ (Double.doubleToLongBits(this.fahrenheit) >>> 32));
            hash = 53 * hash + Objects.hashCode(this.sensorId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TempF other = (TempF) obj;
            if (Double.doubleToLongBits(this.fahrenheit) != Double.doubleToLongBits(other.fahrenheit)) {
                return false;
            }
            if (!Objects.equals(this.sensorId, other.sensorId)) {
                return false;
            }
            return true;
        }

    }

    public static class TempC {

        double centigrade;
        String sensorId;

        public TempC(double centigrade, String sensorId) {
            this.centigrade = centigrade;
            this.sensorId = sensorId;
        }

        public double getCentigrade() {
            return centigrade;
        }

        public void setCentigrade(double centigrade) {
            this.centigrade = centigrade;
        }

        public String getSensorId() {
            return sensorId;
        }

        public void setSensorId(String sensorId) {
            this.sensorId = sensorId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 43 * hash + (int) (Double.doubleToLongBits(this.centigrade) ^ (Double.doubleToLongBits(this.centigrade) >>> 32));
            hash = 43 * hash + Objects.hashCode(this.sensorId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TempC other = (TempC) obj;
            if (Double.doubleToLongBits(this.centigrade) != Double.doubleToLongBits(other.centigrade)) {
                return false;
            }
            if (!Objects.equals(this.sensorId, other.sensorId)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "TempC{" + "centigrade=" + centigrade + ", sensorId=" + sensorId + '}';
        }

    }

    public static boolean gt10(double test) {
        return test > 10;
    }

    public static boolean validData(DataEvent d) {
        return true;
    }

    
    public double max(double num) {
        if (num > currentMax) {
            currentMax = num;
        }
        return currentMax;
    }

    public boolean ignoreFirsTwo(DataEvent d) {
        sum++;
        return sum > 2;
    }

    public static double fahrToCentigrade(TempF tempEvent) {
        double tempF = tempEvent.getFahrenheit();
        return (tempF - 32) * 5 / 9;
    }

    public static TempC tempFtoTempC(TempF tempEvent) {
        double tempF = tempEvent.getFahrenheit();
        TempC tempc = new TempC((tempF - 32) * 5 / 9, tempEvent.getSensorId());
        return tempc;
    }

    public static SerializableFunction<Number, Double> logS() {
        return StreamTest::log;
    }

    double sum;
    double currentMax = Integer.MIN_VALUE;

    public double cumSum(double value) {
        if (!Double.isNaN(value)) {
            sum += value;
        }
        return sum;
    }

    @Override
    public void reset() {
        System.out.println("---- reset sum");
        sum = 0;
        currentMax = Integer.MIN_VALUE;
    }

    public static double log(Number a) {
        return StrictMath.log(a.doubleValue()); // default impl. delegates to StrictMath
    }

}
