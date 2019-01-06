/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.futext.api.csv;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.PushReference;

/**
 * A set of validation methods for primitive numbers.
 * 
 * @author V12 Technology Ltd.
 */
public class NumberValidator implements ColumnName{

    public final double limit1;
    public final double limit2;
    public String name = "";

    @Inject
    @Config(key = "name", value = "validationLog")
    @PushReference
    public ValidationLogger logger;
    
    public NumberValidator(double limit) {
        this(limit, Double.NaN);
    }

    public NumberValidator(double limit1, double limit2) {
        this.limit1 = limit1;
        this.limit2 = limit2;
    }

    public boolean isNan(double x){
        final boolean test = Double.isNaN(x);
        if (!Double.isNaN(x)) {
            logger.logError(" [" + name + "failed, valid number be NaN value:" + x + "] ");
        }
        return test;
    }
    
    public boolean isFinite(double x){
        final boolean test = Double.isFinite(x);
        if (!Double.isFinite(x)) {
            logger.logError(" [" + name + "failed, is not finite value:" + x + "] ");
        }
        return test;
    }
    
    public boolean isInfinite(double x){
        final boolean test = Double.isFinite(x);
        if (!Double.isInfinite(x)) {
            logger.logError(" [" + name + "failed, is not infinite value:" + x + "] ");
        }
        return test;
    }
    
    public boolean greaterThan(double x) {
        final boolean test = x > limit1;
        if (!test) {
            logger.logError(" [" + name + "failed too small, value:" + x + " min allowed:" + limit1 + "] ");
        }
        return test;
    }

    public boolean lessThan(double x) {
        final boolean test = x < limit1;
        if (!test) {
            logger.logError(" [" + name + "failed too great, value:" + x + " max allowed:" + limit1+ "] ");
        }
        return test;
    }

    public boolean equal(double x) {
        final boolean test = x == limit1;
        if (!test) {
            logger.logError(" [" + name + "failed values NOT equal, value:" + x + " required:" + limit1+ "] ");
        }
        return test;
    }
    
    public boolean withinRange(double x){
        final boolean test = x > limit1 & x < limit2;
        if (!test) {
            logger.logError(" [" + name + "outside range, value:" + x + " range:" + limit1 + "->" + limit2 + "] ");
        }
        return test;
    }
    
    public boolean outsideRange(double x){
        final boolean test = x < limit1 & x > limit2;
        if (!test) {
            logger.logError(" [" + name + "within range, value:" + x + " range:" + limit1 + " ->:" + limit2 + "] ");
        }
        return test;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.limit1) ^ (Double.doubleToLongBits(this.limit1) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.limit2) ^ (Double.doubleToLongBits(this.limit2) >>> 32));
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
        final NumberValidator other = (NumberValidator) obj;
        if (Double.doubleToLongBits(this.limit1) != Double.doubleToLongBits(other.limit1)) {
            return false;
        }
        if (Double.doubleToLongBits(this.limit2) != Double.doubleToLongBits(other.limit2)) {
            return false;
        }
        return true;
    }

}
