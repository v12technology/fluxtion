package com.fluxtion.runtime;

import com.fluxtion.runtime.node.SingletonNode;

import java.util.HashMap;
import java.util.Map;

public interface EventProcessorContext {

    <K, V> V get(K key);
}
