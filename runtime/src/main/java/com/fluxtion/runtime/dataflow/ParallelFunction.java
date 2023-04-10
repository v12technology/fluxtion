package com.fluxtion.runtime.dataflow;

public interface ParallelFunction {

    void parallel();

    boolean parallelCandidate();
}
