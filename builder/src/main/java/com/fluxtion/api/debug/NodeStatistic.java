/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.api.debug;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Heuristics class holding node meta data referring to call statistics during
 * event processing. 
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
