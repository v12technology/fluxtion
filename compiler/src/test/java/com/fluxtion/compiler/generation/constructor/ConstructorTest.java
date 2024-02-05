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
package com.fluxtion.compiler.generation.constructor;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.event.Event;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ConstructorTest extends MultipleSepTargetInProcessTest {

    public ConstructorTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testConstructorSimple() {
        sep(c -> {
            c.addNode(
                    new ConfigPublisher(
                            new OrderHandler("orderHandler_1", 200_000_000, 1.0567f),
                            new ConfigHandler("config_1"),
                            new ConfigHandler("config_2"))
            );
            c.addNode(new ConfigHandler("config_public_1"));
        });
    }

    @Test
    public void testConstructorWithCollection() {
        sep(c -> {
            List<OrderHandler> orderhandlerList = Arrays.asList(
                    c.addNode(new OrderHandler("orderHandler_1", 200_000_000, 1.2f)),
                    c.addNode(new OrderHandler("orderHandler_2", 400_000_000, 1.4f)),
                    c.addNode(new OrderHandler("orderHandler_3", 600_000_000, 1.6f))
            );

            ConfigPublisher publisher = c.addPublicNode(new ConfigPublisher(
                    "MyOrderManager",
                    55,
                    orderhandlerList,
                    c.addNode(new OrderHandler("orderHandler_11", 200_000_000, 1.0567f)),
                    c.addNode(new ConfigHandler("config_1")),
                    c.addNode(new ConfigHandler("config_2"))), "publisher");
            final NameHolder nameHolder = c.addNode(new NameHolder("nameHolder", publisher));

            nameHolder.setOrderHandler(orderhandlerList.get(1));
            nameHolder.setId(NameHolder.NAMES.WAY);
            nameHolder.setHandlerList(Arrays.asList(orderhandlerList.get(1), orderhandlerList.get(2)));
            nameHolder.setMatchingRegex("reerer", "lkdjf", "ldkljflkj");
        });
    }

    @Test
    public void testConstructorForClass() {
        sep(c -> {
            c.addNode(new MyClassHolder(String.class));
        });
    }

    @Test
    public void testPrimitiveCollection() {
        sep(c -> {
            c.addNode(new PrimitiveCollections(
                    new boolean[]{true, true, false},
                    Arrays.asList(1, 2, 3, 4, 5),
                    new String[]{"one", "two"}
            ));
        });
    }

    @Test
    public void setConstructorTest() {
        sep(c -> {
            Set<String> stringSet = new HashSet<>();
            stringSet.add("TEST");
            stringSet.add("bill");
            stringSet.add("TEST");
            stringSet.add("greedy");
            c.addNode(new StringSet(stringSet), "stringSetHolder");
        });
        Assert.assertEquals(
                getField("stringSetHolder", StringSet.class).getMySet(),
                new HashSet<>(Arrays.asList("TEST", "bill", "greedy")));
    }

    @Test
    public void setPropertyTest() {
        sep(c -> {
            Set<String> stringSet = new HashSet<>();
            stringSet.add("TEST");
            stringSet.add("bill");
            stringSet.add("TEST");
            stringSet.add("greedy");
            StringSetProperty stringSetProperty = new StringSetProperty();
            stringSetProperty.setMySet(stringSet);
            c.addNode(stringSetProperty, "stringSetHolder");
        });
        Assert.assertEquals(
                getField("stringSetHolder", StringSetProperty.class).getMySet(),
                new HashSet<>(Arrays.asList("TEST", "bill", "greedy")));
    }

    public static final class ConfigEvent implements Event {
    }

    public static final class NewOrderEvent implements Event {
    }

    public static final class ConfigHandler {

        private final String name;

        public ConfigHandler(String name) {
            this.name = name;
        }

        @OnEventHandler
        public boolean configEvent(ConfigEvent configEvent) {
            return true;
        }
    }

    public static class PrimitiveCollections {

        private final boolean[] booleanFinalProp;
        private final List<Integer> intFinalProp;
        private final String[] stringFinalProp;

        public PrimitiveCollections(boolean[] booleanFinalProp, List<Integer> intFinalProp, String[] stringFinalProp) {
            this.booleanFinalProp = booleanFinalProp;
            this.intFinalProp = intFinalProp;
            this.stringFinalProp = stringFinalProp;
        }

        @OnEventHandler
        public boolean configEvent(ConfigEvent configEvent) {
            return true;
        }
    }

    public static class StringSet {

        private final Set<String> mySet;

        public StringSet(@AssignToField("mySet") Set<String> mySet) {
            this.mySet = mySet;
        }

        public Set<String> getMySet() {
            return mySet;
        }
    }

    @Data
    public static class StringSetProperty {

        private Set<String> mySet;

    }

    public static final class NameHolder {

        public final String name;
        private final ConfigPublisher publisher;
        private OrderHandler orderHandler;
        private NAMES id;
        private List<OrderHandler> handlerList;
        private String[] matchingRegex;
        private String[] matchingRegex2;

        public NameHolder(String name, ConfigPublisher publisher) {
            this.name = name;
            this.publisher = publisher;
        }

        public List<OrderHandler> getHandlerList() {
            return handlerList;
        }

        public void setHandlerList(List<OrderHandler> handlerList) {
            this.handlerList = handlerList;
        }

        public OrderHandler getOrderHandler() {
            return orderHandler;
        }

        public void setOrderHandler(OrderHandler orderHandler) {
            this.orderHandler = orderHandler;
        }

        public NAMES getId() {
            return id;
        }

        public void setId(NAMES id) {
            this.id = id;
        }

        @OnTrigger
        public boolean processEvent() {
            return true;
        }

        public String[] getMatchingRegex() {
            return matchingRegex;
        }

        public void setMatchingRegex(String... matchingRegex) {
            this.matchingRegex = matchingRegex;
        }

        public String[] getMatchingRegex2() {
            return matchingRegex2;
        }

        public void setMatchingRegex2(String[] matchingRegex2) {
            this.matchingRegex2 = matchingRegex2;
        }

        public enum NAMES {
            TEST, WAY
        }

    }

    public static final class OrderHandler {

        private final String name;
        private final long maxOrderSize;
        private final float minRate;
        //bean property
        private long time = 200;
        private char c = 'd';
        private char nn;
        private int intVal;
        private transient byte bVal = 9;
        private double myDouble = Double.NaN;

        public OrderHandler(String name, long maxOrderSize, float minRate) {
            this.name = name;
            this.maxOrderSize = maxOrderSize;
            this.minRate = minRate;
        }

        @OnEventHandler
        public boolean newOrderEvent(NewOrderEvent configEvent) {
            return true;
        }

        public String getName() {
            return name;
        }

        public long getMaxOrderSize() {
            return maxOrderSize;
        }

        public float getMinRate() {
            return minRate;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public char getC() {
            return c;
        }

        public void setC(char c) {
            this.c = c;
        }

        public char getNn() {
            return nn;
        }

        public void setNn(char nn) {
            this.nn = nn;
        }

        public int getIntVal() {
            return intVal;
        }

        public void setIntVal(int intVal) {
            this.intVal = intVal;
        }

        public byte getbVal() {
            return bVal;
        }

        public void setbVal(byte bVal) {
            this.bVal = bVal;
        }

        public double getMyDouble() {
            return myDouble;
        }

        public void setMyDouble(double myDouble) {
            this.myDouble = myDouble;
        }

    }

    public static final class ConfigPublisher {

        private final ConfigHandler configHandler;
        private final ConfigHandler configHandler_2;
        private final OrderHandler orderHandler;
        private final List<OrderHandler> handlers;
        private final int totalOrders;
        private final String name;

        public ConfigPublisher(
                @AssignToField("orderHandler") OrderHandler orderHandler,
                @AssignToField("configHandler") ConfigHandler configHandler,
                @AssignToField("configHandler_2") ConfigHandler configHandler_2) {
            this("no name", 0, Collections.emptyList(), orderHandler, configHandler, configHandler_2);
        }

        public ConfigPublisher(
                @AssignToField("name") String name,
                @AssignToField("totalOrders") int totalOrders,
                @AssignToField("handlers") List<OrderHandler> handlers,
                @AssignToField("orderHandler") OrderHandler orderHandler,
                @AssignToField("configHandler") ConfigHandler configHandler,
                @AssignToField("configHandler_2") ConfigHandler configHandler_2) {
            this.handlers = handlers;
            this.orderHandler = orderHandler;
            this.configHandler = configHandler;
            this.configHandler_2 = configHandler_2;
            this.totalOrders = totalOrders;
            this.name = name;
        }

        @OnTrigger
        public boolean publishConfig() {
            return true;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public String getName() {
            return name;
        }

        public OrderHandler getHandlerByName(String name) {
            return handlers.stream().filter(o -> o.name.equals(name)).findAny().get();
        }
    }

    public static class MyClassHolder {

        private final Class clazz;

        public MyClassHolder(Class clazz) {
            this.clazz = clazz;
        }

        @OnEventHandler
        public boolean newOrderEvent(NewOrderEvent configEvent) {
            return true;
        }
    }

}
