/*
 * Copyright (C) 2018 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.util;

/**
 * Mutable numeric value
 *
 * @author Greg Higgins
 */
public class TestMutableNumber extends Number {

    public int intValue;
    public long longValue;
    public double doubleValue;

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public int intValue() {
        return intValue;
    }

    @Override
    public long longValue() {
        return longValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }

    public Number get() {
        return doubleValue;
    }

    public TestMutableNumber set(Number number) {
        intValue = number.intValue();
        longValue = number.longValue();
        doubleValue = number.doubleValue();
        return this;
    }

    public void set(int value) {
        setIntValue(value);
    }

    public void set(long value) {
        setLongValue(value);
    }

    public void set(double value) {
        setDoubleValue(value);
    }

    public void setCharValue(char charValue) {
        setIntValue(charValue);
    }

    public void setByteValue(byte byteValue) {
        setIntValue(byteValue);
    }

    public void setShortValue(short shortValue) {
        setIntValue(shortValue);
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
        this.longValue = intValue;
        this.doubleValue = intValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
        this.intValue = (int) longValue;
        this.doubleValue = longValue;
    }

    public void setfloatValue(float floatValue) {
        setDoubleValue(floatValue);
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
        this.longValue = (long) doubleValue;
        this.intValue = (int) doubleValue;
    }

    public void copyFrom(Number source) {
        intValue = source.intValue();
        longValue = source.longValue();
        doubleValue = source.doubleValue();
    }

    @Override
    public int hashCode() {
        return this.intValue;
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
        final Number other = (Number) obj;
        if (this.intValue != other.intValue()) {
            return false;
        }
        if (this.longValue != other.longValue()) {
            return false;
        }
        if (Double.doubleToLongBits(this.doubleValue) != Double.doubleToLongBits(other.doubleValue())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MutableNumber{" + "intValue=" + intValue + ", longValue=" + longValue + ", doubleValue=" + doubleValue + '}';
    }

}
