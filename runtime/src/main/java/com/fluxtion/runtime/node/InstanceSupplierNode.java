package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.annotations.builder.SepNode;

@SepNode
public class InstanceSupplierNode<T> implements NamedNode, InstanceSupplier<T> {

    @Inject
    private final EventProcessorContext context;
    private final boolean failFast;
    private final Object contextKey;
    private final String name;
    private T contextProperty;

    public InstanceSupplierNode(Object contextKey) {
        this(contextKey, false, null);
    }

    public InstanceSupplierNode(
            Object contextKey,
            boolean failFast) {
        this(contextKey, failFast, null);
    }

    public InstanceSupplierNode(
            Object contextKey,
            boolean failFast,
            EventProcessorContext context) {
        this(contextKey, failFast, context, "contextLookup_" + contextKey);
    }

    public InstanceSupplierNode(
            @AssignToField("contextKey") Object contextKey,
            @AssignToField("failFast") boolean failFast,
            @AssignToField("context") EventProcessorContext context,
            @AssignToField("name") String name) {
        this.contextKey = contextKey;
        this.failFast = failFast;
        this.context = context;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T get() {
        if (contextProperty == null) {
            contextProperty = context.getContextProperty(contextKey);
            if (contextProperty == null && failFast) {
                throw new RuntimeException("missing context property for key:'" + contextKey + "'");
            }
        }
        return contextProperty;
    }

    @Initialise
    public void init() {
        contextProperty = null;
        get();
    }

    @TearDown
    public void tearDown() {
        contextProperty = null;
    }
}
