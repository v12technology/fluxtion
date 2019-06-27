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

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ConstructorTest extends BaseSepTest {

    @Test
    public void testConstructorSimple() {
        buildAndInitSep(ConstructorBuilder.class);
        //TODO - actually add some asserts!!
    }

    @Test
    public void testConstructorWithCollection() {
        buildAndInitSep(ConstructorCollectionBuilder.class);
        //TODO - actually add some asserts!!
    }
    

    @Test
    public void testConstructorForClass() {
        buildAndInitSep(ClassHolderBuilder.class);
        //TODO - actually add some asserts!!
    }

    @Test
    public void testPrimitiveCollection() {
        buildAndInitSep(PrimitiveCollectionsBuilder.class);
    }

    public static final class ConfigEvent extends Event {
    }

    public static final class NewOrderEvent extends Event {
    }

    public static final class ConfigHandler {

        private final String name;

        public ConfigHandler(String name) {
            this.name = name;
        }

        @EventHandler
        public void configEvent(ConfigEvent configEvent) {

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

        @EventHandler
        public void configEvent(ConfigEvent configEvent) {

        }
    }

    public static final class NameHolder {

        public enum NAMES {
            TEST, WAY
        }
        public final String name;
        private final ConfigPublisher publisher;
        private OrderHandler orderHandler;
        private NAMES id;
        private List<OrderHandler> handlerList;
        private String[] matchingRegex;
        private String[] matchingRegex2;

        public List<OrderHandler> getHandlerList() {
            return handlerList;
        }

        public void setHandlerList(List<OrderHandler> handlerList) {
            this.handlerList = handlerList;
        }

        public NameHolder(String name, ConfigPublisher publisher) {
            this.name = name;
            this.publisher = publisher;
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

        @OnEvent
        public void processEvent() {

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

        @EventHandler
        public void newOrderEvent(NewOrderEvent configEvent) {

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
        public ConfigHandler publicHandler;

        public ConfigPublisher(OrderHandler orderHandler, ConfigHandler configHandler, ConfigHandler configHandler_2) {
            this(null, 0, null, orderHandler, configHandler, configHandler_2);
        }

        public ConfigPublisher(int totalOrders, OrderHandler orderHandler, ConfigHandler configHandler, ConfigHandler configHandler_2) {
            this(null, totalOrders, null, orderHandler, configHandler, configHandler_2);
        }

        public ConfigPublisher(String name, int totalOrders, OrderHandler orderHandler, ConfigHandler configHandler, ConfigHandler configHandler_2) {
            this(name, totalOrders, null, orderHandler, configHandler, configHandler_2);
        }

        public ConfigPublisher(List<OrderHandler> handlers, OrderHandler orderHandler, ConfigHandler configHandler, ConfigHandler configHandler_2) {
            this(null, 0, handlers, orderHandler, configHandler, configHandler_2);
        }

        public ConfigPublisher(int totalOrders) {
            this(null, totalOrders, null, null, null, null);
        }

        public ConfigPublisher(String name, int totalOrders, List<OrderHandler> handlers, OrderHandler orderHandler, ConfigHandler configHandler, ConfigHandler configHandler_2) {
            this.handlers = handlers;
            this.orderHandler = orderHandler;
            this.configHandler = configHandler;
            this.configHandler_2 = configHandler_2;
            this.totalOrders = totalOrders;
            this.name = name == null ? "no name" : name;
        }

        @OnEvent
        public void publishConfig() {
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
    
    
    public static class MyClassHolder{
        private final Class clazz;

        public MyClassHolder(Class clazz) {
            this.clazz = clazz;
        }
        
        
        @EventHandler
        public void newOrderEvent(NewOrderEvent configEvent) {

        }
    }

    
    
    
    public static final class ClassHolderBuilder extends SEPConfig {
        @Override
        public void buildConfig() {
            addNode(new MyClassHolder(String.class));
        }
        
    }
    public static final class PrimitiveCollectionsBuilder extends SEPConfig {

        @Override
        public void buildConfig() {
            addNode(new PrimitiveCollections(
                    new boolean[]{true, true, false},
                    Arrays.asList(1, 2, 3, 4, 5),
                    new String[]{"one", "two"}
            ));
        }

    }

    public static final class ConstructorBuilder extends SEPConfig {

        @Override
        public void buildConfig() {
            ConfigPublisher publisher = addPublicNode(new ConfigPublisher(
                    addNode(new OrderHandler("orderHandler_1", 200_000_000, 1.0567f)),
                    addNode(new ConfigHandler("config_1")),
                    addNode(new ConfigHandler("config_2"))), "publisher");
            publisher.publicHandler = addNode(new ConfigHandler("config_public_1"));
        }

    }

    public static final class ConstructorCollectionBuilder extends SEPConfig {

        @Override
        public void buildConfig() {
            List<OrderHandler> orderhandlerList = Arrays.asList(
                    addNode(new OrderHandler("orderHandler_1", 200_000_000, 1.2f)),
                    addNode(new OrderHandler("orderHandler_2", 400_000_000, 1.4f)),
                    addNode(new OrderHandler("orderHandler_3", 600_000_000, 1.6f))
            );

            ConfigPublisher publisher = addPublicNode(new ConfigPublisher(
                    "MyOrderManager",
                    55,
                    orderhandlerList,
                    addNode(new OrderHandler("orderHandler_11", 200_000_000, 1.0567f)),
                    addNode(new ConfigHandler("config_1")),
                    addNode(new ConfigHandler("config_2"))), "publisher");
            final NameHolder nameHolder = addNode(new NameHolder("nameHolder", publisher));

            nameHolder.setOrderHandler(orderhandlerList.get(1));
            nameHolder.setId(NameHolder.NAMES.WAY);
            nameHolder.setHandlerList(Arrays.asList(orderhandlerList.get(1), orderhandlerList.get(2)));
            nameHolder.setMatchingRegex("reerer", "lkdjf", "ldkljflkj");
        }

    }
}
