package com.fluxtion.runtime.time;

import com.fluxtion.runtime.annotations.EventHandler;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoEventReference;
import com.fluxtion.runtime.annotations.builder.Inject;
import lombok.EqualsAndHashCode;

//@EqualsAndHashCode
public class FixedRateTrigger {

    @Inject
    @NoEventReference
    private final Clock clock;
    private final int rate;
    private long previousTime;

    public FixedRateTrigger(int rate){
        this(null, rate);
    }

    public FixedRateTrigger(Clock clock, int rate) {
        this.clock = clock;
        this.rate = rate;
    }

    @EventHandler
    public boolean hasExpired(Object input) {
        long newTime = clock.getWallClockTime();
        boolean expired = rate < (newTime - previousTime);
        if (expired)
            previousTime = newTime;
        return expired;
    }

    @EventHandler(propagate = false)
    public boolean setClockStrategy(ClockStrategy.ClockStrategyEvent event) {
        init();
        return false;
    }

    @Initialise
    public void init() {
        previousTime = clock.getWallClockTime();
    }
}
