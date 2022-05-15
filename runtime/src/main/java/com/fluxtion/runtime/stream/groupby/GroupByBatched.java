package com.fluxtion.runtime.stream.groupby;

import java.util.Collection;
import java.util.Map;

public interface GroupByBatched<K, V>  {

    Map<K, V> map();

    Collection<V> values();
}
