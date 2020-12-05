package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.PushReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Creates a happens before relationship between two nodes even if there is no dependency relationship between the 
 * nodes.
 *
 * @author V12 Technology Ltd.
 */
@NoArgsConstructor
@AllArgsConstructor
public class Anchor {

    @NoEventReference
    public Object anchor;
    @PushReference
    public Object afterAnchor;

    @OnEvent
    public boolean push() {
        return false;
    }
}
