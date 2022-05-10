package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.stream.EventStream.IntEventStream;

public interface IntSlidingWindowFunction extends IntEventStream {

    int aggregate(int input);

}
