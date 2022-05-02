package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.event.Event;
import lombok.ToString;

@ToString
public class CallbackEvent<R> implements Event {
    protected int filterId;
    protected R data;

    @Override
    public int filterId() {
        return filterId;
    }

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }
}
