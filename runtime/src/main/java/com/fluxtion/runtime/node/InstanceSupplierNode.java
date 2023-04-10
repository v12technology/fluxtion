package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.annotations.builder.SepNode;

@SepNode
public class InstanceSupplierNode<T> extends SingleNamedNode implements NamedNode, InstanceSupplier<T> {

    @Inject
    private final EventProcessorContext context;
    private final boolean failFast;
    private final Object contextKey;
    private T instanceFromEventProcessorContext;

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
        super(name.replace(".", "_"));
        this.contextKey = contextKey;
        this.failFast = failFast;
        this.context = context;
    }

    @Override
    public T get() {
        instanceFromEventProcessorContext = context.getContextProperty(contextKey);
        if (instanceFromEventProcessorContext == null && failFast) {
            throw new RuntimeException("missing context property for key:'" + contextKey + "'");
        }
        return instanceFromEventProcessorContext;
    }

    @Initialise
    public void init() {
        instanceFromEventProcessorContext = null;
        get();
    }

    @TearDown
    public void tearDown() {
        instanceFromEventProcessorContext = null;
    }
}
