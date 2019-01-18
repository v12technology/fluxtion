/*
 * Copyright (C) 2019 gregp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.ext.futext.example.flightdelay;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterType;
import com.fluxtion.api.annotations.TearDown;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.group.Group.groupBy;
import com.fluxtion.ext.declarative.builder.group.GroupByBuilder;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.util.CharEventStreamer;
import com.fluxtion.ext.futext.builder.csv.CharTokenConfig;
import static com.fluxtion.ext.futext.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import static com.fluxtion.ext.futext.builder.math.CountFunction.count;
import static com.fluxtion.ext.futext.builder.test.GreaterThanHelper.greaterThanFilter;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.DirOptions.JAVA_SRCDIR_OUTPUT;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.InitOptions.INIT;
import com.fluxtion.generator.util.ClassUtils;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class FlightDelayTest {

    @Test
    @Ignore
    public void buildFlightAnalyser() throws IllegalAccessException, Exception {
        InprocessSepCompiler.sepInstance((c) -> {
            Wrapper<FlightDetails> flightDetails = csvMarshaller(FlightDetails.class, 1)
                    .map(14, FlightDetails::setDelay)
                    .map(8, FlightDetails::setCarrier).tokenConfig(CharTokenConfig.WINDOWS).build();
            //filter for positive delays
            Wrapper<FlightDetails> delayedFlight = greaterThanFilter(flightDetails, FlightDetails::getDelay, 0);
            //group by carrier name
            GroupByBuilder<FlightDetails, CarrierDelay> carrierDelay = groupBy(delayedFlight, FlightDetails::getCarrier, CarrierDelay.class);
            //init each group record with human readable name
            carrierDelay.init(FlightDetails::getCarrier, CarrierDelay::setCarrierId);
            //aggregate calculations
            carrierDelay.avg(FlightDetails::getDelay, CarrierDelay::setAvgDelay);
            carrierDelay.count(CarrierDelay::setTotalFlights);
            carrierDelay.sum(FlightDetails::getDelay, CarrierDelay::setTotalDelayMins);
            //add public node for debug
            c.addPublicNode(carrierDelay.build(), "carrierDelayMap");
            //total records processed counts FlightDetails events from csvMarshaller
            c.addPublicNode(count(flightDetails), "totalFlights");
            c.maxFiltersInline = 25;
        },
                "com.fluxtion.ext.futext.example.flightdelay.generated", "FlightDelayAnalyser", JAVA_SRCDIR_OUTPUT, INIT);
    }

    @Test
    @Ignore
    public void runTest() throws IOException, FileNotFoundException, InterruptedException {
//        CharEventStreamer streamer = new CharEventStreamer();
//        String dataPathString = "2008_clean.csv";
////        String dataPathString = "C:\\Users\\gregp\\development\\projects\\fluxtion\\open-source\\fluxtion-examples\\case-studies\\flight-delay\\dist\\data\\2008.csv";
//        File dataFile = Paths.get(dataPathString).toFile();
//        FlightDelayAnalyser processor = new FlightDelayAnalyser();
//        long delta = System.nanoTime();
//        streamer.streamFromFile(dataFile, processor);
//        delta = System.nanoTime() - delta;
//        double duration = (delta / 1_000_000) / 1000.0;
//        System.out.println("processed file:" + dataFile.getAbsolutePath());
//        processor.carrierDelayMap.getMap().values().stream().map(Wrapper::event).forEach(System.out::println);
//
//        System.out.println("row count:" + processor.totalFlights.intValue() + "\nprocessing time:" + duration + " seconds");
    }

    @Test
    @Ignore
    public void removeNA() throws IllegalAccessException, Exception {
        com.fluxtion.api.lifecycle.EventHandler sep = InprocessSepCompiler.sepTestInstance((c) -> {
            RemoveNA na = c.addNode(new RemoveNA(), "remover");
            c.maxFiltersInline = 15;
        }, "com.gh.removeNa", "RemoveNaSep");
//        RemoveNaSep sep = new RemoveNaSep();

        RemoveNA remover = ClassUtils.getField("remover", sep);
        remover.out = new BufferedOutputStream(new FileOutputStream("2008_clean.csv"));
        CharEventStreamer streamer = new CharEventStreamer();
        String dataPathString = "C:\\Users\\gregp\\development\\projects\\fluxtion\\open-source\\fluxtion-examples\\case-studies\\flight-delay\\dist\\data\\2008.csv";
        streamer.streamFromFile(Paths.get(dataPathString).toFile(), sep);

    }

    public static class RemoveNA {

        boolean previousN = false;
        public OutputStream out;

        @EventHandler(filterId = 'N')
        public void charN(CharEvent event) {
            previousN = true;
        }

        @EventHandler(filterId = 'A')
        public void charA(CharEvent event) {
            try {
                if (previousN) {
                    out.write('0');
                }else{
                    out.write('A');
                }
            } catch (IOException ex) {
                Logger.getLogger(FlightDelayTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            previousN = false;
        }

        @EventHandler(FilterType.unmatched)
        public void charOther(CharEvent event) {
            try {
                if(previousN){
                    out.write('N');
                }
                out.write((byte) event.getCharacter());
            } catch (IOException ex) {
                Logger.getLogger(FlightDelayTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            previousN = false;
        }

        @TearDown
        public void tearDown() {
            try {
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(FlightDelayTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
