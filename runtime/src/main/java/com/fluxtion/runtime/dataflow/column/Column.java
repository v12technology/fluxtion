package com.fluxtion.runtime.dataflow.column;

import java.util.List;

public interface Column<T> {
    List<T> values();

}
