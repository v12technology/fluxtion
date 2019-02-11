package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.api.Stateful;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static com.fluxtion.ext.declarative.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.declarative.api.stream.NumericPredicates.lt;
import static com.fluxtion.ext.declarative.api.stream.NumericPredicates.negative;
import static com.fluxtion.ext.declarative.api.stream.NumericPredicates.positive;
import static com.fluxtion.ext.declarative.api.stream.StringPredicates.is;
import org.junit.Test;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import static com.fluxtion.ext.declarative.builder.log.LogBuilder.Log;

import static com.fluxtion.generator.compiler.InprocessSepCompiler.sepTestInstance;
import org.junit.Ignore;

/**
 *
 * @author gregp
 */
public class StreamTest implements Stateful {

    @Test
    public void tempMonitorTest() throws IllegalAccessException, Exception {
        EventHandler handler = sepTestInstance((c) -> {
            //convert to C from F
            Wrapper<Number> tempC = select(TempF.class)
                    .filter(TempF::getSensorId, is("outside"))
                    .filter(TempF::getFahrenheit, StreamTest::gt10)
                    .map(StreamTest::fahrToCentigrade);
            //convert to log temps
//            StreamTest instance = c.addNode(new StreamTest());
            Wrapper<Double> logTemp = select(TempF.class)
                    .map(Math::log, TempF::getFahrenheit)
                    .map(new StreamTest()::cumSum).resetNotifier(select(DataEvent.class))
                    .console("[cum sum] ->");

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
        handler.onEvent(new TempF(32, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        //reset
        handler.onEvent(new DataEvent());
        handler.onEvent(new TempF(100, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
    }

    @Test
    public void consumeTest() throws IllegalAccessException, Exception {
        EventHandler handler = sepTestInstance((c) -> {
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
    }

    @Test
    @Ignore
    public void testCumSum() throws Exception {
        EventHandler handler = (EventHandler) Class.forName("com.fluxtion.ext.declarative.builder.tempsensortest.TempMonitor").newInstance();
        ((Lifecycle) handler).init();
        handler.onEvent(new TempF(10, "outside"));
        handler.onEvent(new TempF(32, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(60, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));
        handler.onEvent(new TempF(100, "outside"));
        handler.onEvent(new TempF(-10, "ignore me"));

    }

    @Test
    public void graphOfStreamsTest() throws Exception {
        EventHandler handler = sepTestInstance((c) -> {
            Wrapper<DataEvent> f = select(DataEvent.class)
                    .filter(DataEvent::getValue, positive())
                    .filter(DataEvent::getValue, lt(20));
            //tee1
            f.filter(StreamTest::validData)
                    .filter(DataEvent::getValue, gt(-20))
                    .filter(DataEvent::getValue, negative());
            //tee 2
            f.filter(StreamTest::validData);
            //tee 3
            f.filter(StreamTest::validData)
                    .filter(DataEvent::getValue, gt(20));
        }, "com.fluxtion.ext.declarative.builder.graphtest", "GraphTempSensor");
    }

    public static class TempF extends Event {

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

    }

    public static boolean gt10(double test) {
        return test > 10;
    }

    public static boolean validData(DataEvent d) {
        return true;
    }

    public static double fahrToCentigrade(TempF tempEvent) {
        double tempF = tempEvent.getFahrenheit();
        return (tempF - 32) * 5 / 9;
    }

    public static SerializableFunction<Number, Double> logS() {
        return StreamTest::log;
    }

    double sum;

    public double cumSum(double value) {
        if (!Double.isNaN(value)) {
            sum += value;
        }
        return sum;
    }

    @Override
    public void reset() {
        sum = 0;
    }

    public static double log(Number a) {
        return StrictMath.log(a.doubleValue()); // default impl. delegates to StrictMath
    }

}
