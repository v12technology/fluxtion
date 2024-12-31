/*
 * Copyright (c) 2019, 2024 gregory higgins.
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

package com.fluxtion.compiler.generation.eventfeed;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.event.NamedFeedEventImpl;
import com.fluxtion.runtime.node.NamedFeedTableNode;
import com.fluxtion.runtime.node.TableNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TableNodeTest extends MultipleSepTargetInProcessTest {

    public TableNodeTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void addToTableNode() {
        sep(c -> {
            NamedFeedTableNode<String, City> tableNode = new NamedFeedTableNode<>(
                    "feed1",
                    "com.fluxtion.compiler.generation.eventfeed.TableNodeTest$City::getName");
            DataAggregator dataAggregator = new DataAggregator();
            dataAggregator.setCityTable(tableNode);
            c.addNode(dataAggregator, "dataAggregator");
        });

        onEvent(new NamedFeedEventImpl<>("feed1")
                .setData(new City("LONDON", 200))
                .setSequenceNumber(0)
        );
        //ignore different feed
        onEvent(new NamedFeedEventImpl<>("feed2")
                .setData(new City("LONDON", 99))
                .setSequenceNumber(1));
        DataAggregator dataAggregator = getField("dataAggregator");

        Map<String, City> expectedCityTable = new HashMap<>();
        expectedCityTable.put("LONDON", new City("LONDON", 200));
        Map<String, City> tableMap = dataAggregator.getCityTable().getTableMap();
        Assert.assertEquals(expectedCityTable, tableMap);

        onEvent(new NamedFeedEventImpl<>("feed1")
                .setData(new City("LONDON", 8888))
                .setSequenceNumber(2));
        expectedCityTable.put("LONDON", new City("LONDON", 8888));
        tableMap = dataAggregator.getCityTable().getTableMap();
        Assert.assertEquals(expectedCityTable, tableMap);

        NamedFeedEventImpl<City> namedFeedEvent = new NamedFeedEventImpl<City>("feed1")
                .setData(new City("LONDON", 8888))
                .setSequenceNumber(100);
        namedFeedEvent.setDelete(true);
        onEvent(namedFeedEvent);
        Assert.assertTrue(tableMap.isEmpty());

        namedFeedEvent = new NamedFeedEventImpl<City>("feed1")
                .setData(new City("LONDON", 10))
                .setSequenceNumber(4);
        namedFeedEvent.setDelete(true);
        onEvent(namedFeedEvent);
        Assert.assertTrue(tableMap.isEmpty());
    }


    @Test
    public void addToTopicFilteredTableNode() {
        sep(c -> {
            NamedFeedTableNode<String, City> tableNode = new NamedFeedTableNode<>(
                    "feed1",
                    "topic1",
                    "com.fluxtion.compiler.generation.eventfeed.TableNodeTest$City::getName");
            DataAggregator dataAggregator = new DataAggregator();
            dataAggregator.setCityTable(tableNode);
            c.addNode(dataAggregator, "dataAggregator");
        });

        onEvent(new NamedFeedEventImpl<>("feed1")
                .setData(new City("LONDON", 200))
                .setSequenceNumber(0));
        //ignore different feed
        //ignore different feed
        onEvent(new NamedFeedEventImpl<>("feed2")
                .setData(new City("LONDON", 99))
                .setSequenceNumber(1));
        DataAggregator dataAggregator = getField("dataAggregator");
        Map<String, City> tableMap = dataAggregator.getCityTable().getTableMap();

        Assert.assertTrue(tableMap.isEmpty());

        onEvent(new NamedFeedEventImpl<>("feed1", "topic1")
                .setData(new City("LONDON", 200))
                .setSequenceNumber(2));
        Map<String, City> expectedCityTable = new HashMap<>();
        expectedCityTable.put("LONDON", new City("LONDON", 200));
        Assert.assertEquals(expectedCityTable, tableMap);

        onEvent(new NamedFeedEventImpl<>("feed1", "topic1")
                .setData(new City("LONDON", 8888))
                .setSequenceNumber(3));
        expectedCityTable.put("LONDON", new City("LONDON", 8888));
        Assert.assertEquals(expectedCityTable, tableMap);
    }

    @Data
    public static class DataAggregator {
        private TableNode<String, City> cityTable;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class City {
        String name;
        int population;
    }
}
