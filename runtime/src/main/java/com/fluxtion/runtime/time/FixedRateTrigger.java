package com.fluxtion.runtime.time;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;

//@EqualsAndHashCode
public class FixedRateTrigger {

    @Inject
    @NoTriggerReference
    private final Clock clock;
    private final int rate;
    private long previousTime;
    private int triggerCount;

    public static FixedRateTrigger atMillis(int millis) {
        return new FixedRateTrigger(millis);
    }

    public FixedRateTrigger(int rate) {
        this(null, rate);
    }

    public FixedRateTrigger(Clock clock, int rate) {
        this.clock = clock;
        this.rate = rate;
    }

    @OnEventHandler
    public boolean hasExpired(Object input) {
        long newTime = clock.getWallClockTime();
        boolean expired = rate <= (newTime - previousTime);
        if (expired) {
            triggerCount = (int) ((newTime - previousTime) / rate);
            previousTime += (long) triggerCount * rate;
        }
        return expired;
    }

    /**
     * number of triggers that should have fired since the last time update to now
     *
     * @return number of triggers between previous time and now
     */
    public int getTriggerCount() {
        return triggerCount;
    }

    @OnEventHandler(propagate = false)
    public boolean setClockStrategy(ClockStrategy.ClockStrategyEvent event) {
        init();
        return false;
    }

    @Initialise
    public void init() {
        previousTime = clock.getWallClockTime();
        triggerCount = 0;
    }
}
