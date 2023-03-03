package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.annotations.builder.SepNode;

@SepNode
public class ContextValueSupplierNode<T> implements NamedNode, ContextValueSupplier<T> {

    @Inject
    private final EventProcessorContext context;
    private final boolean failFast;
    private final String contextKey;
    private transient final String name;

    public ContextValueSupplierNode(String contextKey) {
        this(contextKey, false, null);
    }

    public ContextValueSupplierNode(
            String contextKey,
            boolean failFast) {
        this(contextKey, failFast, null);
    }

    public ContextValueSupplierNode(
            String contextKey,
            boolean failFast,
            EventProcessorContext context) {
        this.context = context;
        this.failFast = failFast;
        this.contextKey = contextKey;
        name = "contextLookup_" + contextKey;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T get() {
        T contextProperty = context.getContextProperty(contextKey);
        if (contextProperty == null && failFast) {
            throw new RuntimeException("missing context property for key:'" + contextKey + "'");
        }
        return contextProperty;
    }
}
