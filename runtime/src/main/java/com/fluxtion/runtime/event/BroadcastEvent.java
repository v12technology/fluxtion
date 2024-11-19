package com.fluxtion.runtime.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.lock.qual.NewObject;

@Data
@AllArgsConstructor
@NewObject
public class BroadcastEvent {
    private Object event;
}
