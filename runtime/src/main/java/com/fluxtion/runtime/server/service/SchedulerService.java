package com.fluxtion.runtime.server.service;

import com.fluxtion.runtime.annotations.feature.Experimental;

@Experimental
public interface SchedulerService {

    long scheduleAtTime(long expireTIme, Runnable expiryAction);

    long scheduleAfterDelay(long waitTime, Runnable expiryAction);

    long milliTime();

    long microTime();

    long nanoTime();
}
