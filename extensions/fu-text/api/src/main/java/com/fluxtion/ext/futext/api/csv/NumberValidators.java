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
//import com.fluxtion.ext.declarative.builder.util.LambdaReflection;

/**
 *
 * @author V12 Technology Ltd.
 */
public class NumberValidators {

    public final double limit;
    public String id = "";

    @Inject
    @Config(key = "name", value = "validationLog")
    public ValidationLogger logger;

    public NumberValidators(double limit) {
        this.limit = limit;
    }

    public boolean greaterThan(double x) {
        final boolean test = x > limit;
        if (!test) {
            logger.logError(" [" + id + "failed too small, value:" + x + " min allowed:" + limit + "] ");
        }
        return test;
    }

    public boolean lessThan(double x) {
        final boolean test = x < limit;
        if (!test) {
            logger.logError(" [" + id + "failed too great, value:" + x + " max allowed:" + limit+ "] ");
        }
        return test;
    }

    public boolean equal(double x) {
        final boolean test = x == limit;
        if (!test) {
            logger.logError(" [" + id + "failed values NOT equal, value:" + x + " required:" + limit+ "] ");
        }
        return test;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.limit) ^ (Double.doubleToLongBits(this.limit) >>> 32));
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
        final NumberValidators other = (NumberValidators) obj;
        if (Double.doubleToLongBits(this.limit) != Double.doubleToLongBits(other.limit)) {
            return false;
        }
        return true;
    }
    
    

}
