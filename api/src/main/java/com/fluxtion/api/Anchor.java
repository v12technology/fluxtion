package com.fluxtion.api;

import com.fluxtion.api.annotations.ExcludeNode;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.PushReference;
import lombok.Value;

/**
 * Creates a happens before relationship between two nodes even if there is no dependency relationship between the
 * nodes.
 *
 * @author V12 Technology Ltd.
 */
@Value
@ExcludeNode
public class Anchor {

    @NoEventReference
    Object anchor;
    @PushReference
    Object afterAnchor;

    /**
     * Creates a happens before relationship between two nodes even though there is no dependency relationship between
     * the nodes. This can happen if both nodes are siblings depending upon a common parent.
     *
     * @param <S> The type of the happens after node
     * @param <T> The type of the happens before node
     * @param anchor The anchor node that will be invoked first
     * @param afterAnchor The node that will be notified after the anchor node
     * @return The after anchor node
     */
    public static <S, T> S anchor(T anchor, S afterAnchor) {
        SepContext.service().addOrReuse(new Anchor(anchor, afterAnchor));
        return afterAnchor;
    }

    /**
     * Anchor multiple instances to a single anchor, {@link Anchor#anchor}
     * @param anchor
     * @param afterAnchors 
     */
    public static void anchor(Object anchor, Object... afterAnchors) {
        for (Object afterAnchor : afterAnchors) {
            anchor(anchor, afterAnchor);
            anchor = afterAnchor;
        }
    }
}
