package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;
import lombok.Value;

/**
 * Creates a happens before relationship between two nodes even if there is no dependency relationship between the
 * nodes.
 *
 * @author 2024 gregory higgins.
 */
@Value
public class Anchor {

    Object anchor;
    Object afterAnchor;

    /**
     * Creates a happens before relationship between two nodes even though there is no dependency relationship between
     * the nodes. This can happen if both nodes are siblings depending upon a common parent.
     *
     * @param <S>         The type of the happens after node
     * @param <T>         The type of the happens before node
     * @param anchor      The anchor node that will be invoked first
     * @param afterAnchor The node that will be notified after the anchor node
     * @return The after anchor node
     */
    public static <S, T> S anchor(T anchor, S afterAnchor) {
        EventProcessorBuilderService.service().addOrReuse(
                new Anchor(
                        EventProcessorBuilderService.service().addOrReuse(anchor),
                        EventProcessorBuilderService.service().addOrReuse(afterAnchor)
                )
        );
        return afterAnchor;
    }

    public static <S> S anchorToCaptured(MethodReferenceReflection methodReference, S afterAnchor) {
        if (methodReference != null && methodReference.captured().length > 0) {
            return anchor(methodReference.captured()[0], afterAnchor);
        }
        return afterAnchor;
    }

    public static <S> MethodReferenceReflection anchorCaptured(S anchor, MethodReferenceReflection methodReference) {
        if (methodReference != null && methodReference.captured().length > 0) {
            anchor(anchor, methodReference.captured()[0]);
        }
        return methodReference;
    }

    /**
     * Anchor multiple instances to a single anchor, {@link Anchor#anchor}
     *
     * @param anchor       The anchor node that will be invoked first
     * @param afterAnchors The list of nodes that will be notified after the anchor node
     */
    public static void anchorMany(Object anchor, Object... afterAnchors) {
        for (Object afterAnchor : afterAnchors) {
            anchor(anchor, afterAnchor);
            anchor = afterAnchor;
        }
    }
}
