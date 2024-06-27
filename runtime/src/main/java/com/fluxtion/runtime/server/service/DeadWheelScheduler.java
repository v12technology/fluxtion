package com.fluxtion.runtime.server.service;

import com.fluxtion.runtime.annotations.feature.Experimental;
import org.agrona.DeadlineTimerWheel;
import org.agrona.collections.Long2ObjectHashMap;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.EpochNanoClock;
import org.agrona.concurrent.OffsetEpochNanoClock;

import java.util.concurrent.TimeUnit;

@Experimental
public class DeadWheelScheduler implements SchedulerService, Agent {

    private final DeadlineTimerWheel timerWheel = new DeadlineTimerWheel(TimeUnit.MILLISECONDS, System.currentTimeMillis(), 1024, 1);
    private final Long2ObjectHashMap<Runnable> expiryActions = new Long2ObjectHashMap<>();
    private final EpochNanoClock clock = new OffsetEpochNanoClock();

    @Override
    public long scheduleAtTime(long expireTIme, Runnable expiryAction) {
        long id = timerWheel.scheduleTimer(expireTIme);
        expiryActions.put(id, expiryAction);
        return id;
    }

    @Override
    public long scheduleAfterDelay(long waitTime, Runnable expiryAction) {
        long id = timerWheel.scheduleTimer(milliTime() + waitTime);
        expiryActions.put(id, expiryAction);
        return id;
    }

    @Override
    public int doWork() {
        return timerWheel.poll(milliTime(), this::onTimerExpiry, 100);
    }

    @Override
    public String roleName() {
        return "deadWheelScheduler";
    }

    private boolean onTimerExpiry(TimeUnit timeUnit, long now, long timerId) {
        expiryActions.remove(timerId).run();
        return true;
    }

    @Override
    public long milliTime() {
        return TimeUnit.NANOSECONDS.toMillis(clock.nanoTime());
    }

    @Override
    public long microTime() {
        return TimeUnit.NANOSECONDS.toMicros(clock.nanoTime());
    }

    @Override
    public long nanoTime() {
        return clock.nanoTime();
    }

}
