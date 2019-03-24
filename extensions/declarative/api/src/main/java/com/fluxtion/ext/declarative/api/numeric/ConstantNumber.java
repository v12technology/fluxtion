/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.api.numeric;

import com.fluxtion.ext.declarative.api.Constant;

/**
 * Mutable numeric value
 * 
 * @author Greg Higgins
 */
public class ConstantNumber extends Number implements Constant{

    private int intValue;
    private long longValue;
    private final double doubleValue;

    public ConstantNumber(double doubleValue) {
        this.doubleValue = doubleValue;
        this.intValue = (int) doubleValue;
        this.longValue = (long) doubleValue;
    }

    @Override
    public float floatValue(){
        return (float)doubleValue();
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
