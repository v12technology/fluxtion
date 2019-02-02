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

import com.fluxtion.builder.annotation.Disabled;
import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.group.Group.groupBy;
import com.fluxtion.ext.declarative.builder.group.GroupByBuilder;
import com.fluxtion.ext.futext.api.util.CharStreamer;
import com.fluxtion.ext.futext.builder.csv.CharTokenConfig;
import static com.fluxtion.ext.futext.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import static com.fluxtion.ext.futext.builder.math.CountBuilder.count;
import static com.fluxtion.ext.futext.builder.test.GreaterThanHelper.greaterThanFilter;
import com.fluxtion.ext.futext.example.flightdelay.generated.FlightDelayAnalyser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author gregp
 */
public class Main {

    public static void main(String[] args) throws IOException, FileNotFoundException, InterruptedException {
        File dataFile = new File(args[0]);
        FlightDelayAnalyser processor = new FlightDelayAnalyser();
        //start timing
        long delta = System.nanoTime();
        CharStreamer.stream(new BufferedReader(new FileReader(dataFile)), processor).async().stream();
        delta = System.nanoTime() - delta;
        //end timing
        double duration = (delta / 1_000_000) / 1000.0;
        System.out.println("processed file:" + dataFile.getAbsolutePath());
        processor.carrierDelayMap.getMap().values().stream().map(Wrapper::event).forEach(System.out::println);
        System.out.println("row count:" + processor.totalFlights.intValue() + "\nprocessing time:" + duration + " seconds");
    }

    @SepBuilder(name = "FlightDelayAnalyser", packageName = "com.fluxtion.ext.futext.example.flightdelay.generated")
    @Disabled
    public void buildFlightProcessor(SEPConfig cfg) {
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
        cfg.addPublicNode(carrierDelay.build(), "carrierDelayMap");
        //total records processed counts FlightDetails events from csvMarshaller
        cfg.addPublicNode(count(flightDetails), "totalFlights");
        cfg.maxFiltersInline = 25;
    }

}
