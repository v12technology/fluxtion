/*
 * Copyright (c) 2020, 2024 gregory higgins.
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
package com.fluxtion.runtime.event;

/**
 * A notification signal is an event that facilitates publishing control signals to event
 * handlers. Signal remove the need to define bespoke control events by
 * using a named signal and filtering .
 * <br>
 * <p>
 * The {@link  Signal#filterString} filters events a receiver will
 * process. The generated SEP provide all filtering logic within the generated
 * dispatch code. A node marks a method with a <b>filtered EventHandler</b> annotation
 * as shown:
 *
 * <pre>
 * <h2>Sending</h2>
 * StaticEventProcessor processor;<br>
 * processor.onEvent(new Signal{@literal <Queue<String>>}("someKey", new ConcurrentLinkedQueue<>(List.of("1","2","3","4", "5", "6"))));
 *
 * <h2>Receiving</h2>
 * {@literal @}EventHandler(filterString = "filterString")<br>
 * public void controlMethod(Signal publishSignal){<br>
 *     //signal processing logic<br>
 * }<br>
 * </pre>
 * <p>
 * Using the propagate=false will ensure the event is consumed by the signal
 * handler. Swallowing an event prevents a control signal from executing an
 * event chain for any dependent nodes of the event processor:
 * <br>
 *
 * <pre>
 * {@literal @}EventHandler(filterString = "filterString", propagate = false)
 * </pre>
 * <p>
 * The Signal also provides an optional value the receiver can
 * accessed via {@link #getValue() }.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class Signal<T> implements Event {

    private String filterString;
    private T value;

    public Signal() {
    }

    public Signal(String filterString) {
        this(filterString, null);
    }

    public Signal(Enum<?> enumFilter) {
        this(enumFilter.name());
    }

    public Signal(String filterString, T value) {
        this.filterString = filterString;
        this.value = value;
    }

    public Signal(T value) {
        this.filterString = value.getClass().getCanonicalName();
        this.value = value;
    }

    public Signal(Class filterClass, T value) {
        this.filterString = filterClass.getCanonicalName();
        this.value = value;
    }

    public static Signal.IntSignal intSignal(String filter, int value) {
        return new IntSignal(filter, value);
    }

    public static Signal.DoubleSignal doubleSignal(String filter, double value) {
        return new DoubleSignal(filter, value);
    }

    public static Signal.LongSignal longSignal(String filter, long value) {
        return new LongSignal(filter, value);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    @Override
    public String filterString() {
        return filterString;
    }

    @Override
    public String toString() {
        return "Signal: {" + "filterString: " + filterString + ", value: " + value + '}';
    }

    public static class IntSignal extends DefaultEvent {
        private final int value;

        public IntSignal(String filterId, int value) {
            super(filterId);
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public static class DoubleSignal extends DefaultEvent {
        private final double value;

        public DoubleSignal(String filterId, double intValue) {
            super(filterId);
            this.value = intValue;
        }

        public double getValue() {
            return value;
        }
    }

    public static class LongSignal extends DefaultEvent {
        private final long value;

        public LongSignal(String filterId, long intValue) {
            super(filterId);
            this.value = intValue;
        }

        public long getValue() {
            return value;
        }
    }

}
