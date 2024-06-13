package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.dataflow.FlowSupplier;
import lombok.Data;

import java.util.Collection;

@Data
public class GroupByDeleteByKeyFlowFunction {

    private final FlowSupplier<?> keysToDelete;
    private final boolean remove;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GroupBy deleteByKey(GroupBy groupBy, Collection keysToDelete) {
        if (this.keysToDelete.hasChanged()) {
            groupBy.toMap().keySet().removeAll(keysToDelete);
            if (remove) {
                keysToDelete.clear();
            }
        }
        return groupBy;
    }
}
