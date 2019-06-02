/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.time.Clock;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TimerFilter implements Stateful{
    
    
    public static<S> SerializableFunction<S, Boolean> throttle(long period){
        return new TimerFilter(null, period)::alarmNotify;
    }
    
    @Inject
    @NoEventReference
    private final Clock clock;
    private final long throttlePeriod;
    private long lastPublishTime;

    public TimerFilter(Clock clock, long throttlePeriod) {
        this.clock = clock;
        this.throttlePeriod = throttlePeriod;
    }

    @Initialise
    public void reset() {
        lastPublishTime = -1;
    }
    
    public boolean alarmNotify(Object o){
        if(lastPublishTime<0){
            lastPublishTime = clock.getWallClockTime();
            return false;
        }
        long currentTime = clock.getWallClockTime();
        boolean expired = false;
        if((currentTime - lastPublishTime) > throttlePeriod){
            expired = true;
            lastPublishTime = currentTime;
        }
        return expired;
    } 
    
}
