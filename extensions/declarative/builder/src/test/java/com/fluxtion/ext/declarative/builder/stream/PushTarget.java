package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.annotations.OnEvent;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PushTarget {

    public int count;
    public int val;

    @OnEvent
    public void update() {
        count++;
    }

    public void setVal(int val) {
        this.val = val * 10;
    }

}
