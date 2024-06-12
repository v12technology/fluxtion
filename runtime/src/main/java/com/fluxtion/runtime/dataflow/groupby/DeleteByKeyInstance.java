package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import lombok.Data;

import java.util.Collection;

@Data
public class DeleteByKeyInstance {

    private final FlowSupplier<?> keysToDelete;
    private boolean remove = false;

    @OnParentUpdate
    public void keysUpdated(FlowSupplier<?> keysToDelete) {
        remove = true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GroupBy deleteByKey(GroupBy groupBy, Collection keysToDelete) {
        if (remove) {
            groupBy.toMap().keySet().removeAll(keysToDelete);
        }
        remove = false;
        return groupBy;
    }
}
