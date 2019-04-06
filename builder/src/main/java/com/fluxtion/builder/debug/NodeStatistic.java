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
package com.fluxtion.builder.debug;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>Experimental feature - do not use</h1>
 * Heuristics class holding node meta data referring to call statistics during
 * event processing. 
 * 
 * 
 * @author Greg Higgins
 */
public class NodeStatistic {
    private final String nodeName;
    private final AtomicInteger count;

    public NodeStatistic(String nodeName) {
        this.nodeName = nodeName;
        count = new AtomicInteger(0);
    }
    
    public void resetStatistics(){
        count.set(0);
    }
    
    public int incrementCallCount(){
        return count.incrementAndGet();
    }
    
    public int callCount(){
        return count.get();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.nodeName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeStatistic other = (NodeStatistic) obj;
        if (!Objects.equals(this.nodeName, other.nodeName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  nodeName + "[ count=" + count + ']';
    }
    
    
    
}
