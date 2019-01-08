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

import com.fluxtion.api.annotations.Initialise;

/**
 *
 * @author Greg Higgins
 */
public class NumericConstant implements NumericValue{

    public int initialIntValue;
    public double initialDoubleValue;
    public long initialLongValue;
    
    private int intValue;
    private double doubleValue;
    private long longValue;

    public NumericConstant() {
    }
    
    public NumericConstant(Number value){
        initialIntValue = value.intValue();
        initialDoubleValue = value.doubleValue();
        initialLongValue = value.longValue();
        initConstants();
    }
    
    public void setConstant(Number value){
        initialIntValue = value.intValue();
        initialDoubleValue = value.doubleValue();
        initialLongValue = value.longValue();
        initConstants();
    }
    
    @Initialise
    public final void initConstants(){
        intValue = initialIntValue;
        doubleValue = initialDoubleValue;
        longValue = initialLongValue;
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
        int hash = 7;
        hash = 17 * hash + this.intValue;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.doubleValue) ^ (Double.doubleToLongBits(this.doubleValue) >>> 32));
        hash = 17 * hash + (int) (this.longValue ^ (this.longValue >>> 32));
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
        final NumericConstant other = (NumericConstant) obj;
        if (this.intValue != other.intValue) {
            return false;
        }
        if (Double.doubleToLongBits(this.doubleValue) != Double.doubleToLongBits(other.doubleValue)) {
            return false;
        }
        if (this.longValue != other.longValue) {
            return false;
        }
        return true;
    }

}
