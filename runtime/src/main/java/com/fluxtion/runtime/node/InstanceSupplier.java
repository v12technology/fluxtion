package com.fluxtion.runtime.node;

import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.StaticEventProcessor;

import java.util.function.Supplier;

/**
 * <pre>
 *
 * Lookups and injects a value from the {@link EventProcessorContext#getContextProperty(Object)} at runtime. The key
 * for the lookup is taken from the generic type T. Makes the value available as {@link InstanceSupplier}.
 *
 * Instances can be injected via {@link StaticEventProcessor#injectInstance(Object)}
 *
 * <pre>
 *
 * public static class InjectContextByType {
 *    {@literal @}Inject
 *     public InstanceSupplier{@literal <}MyService{@literal >} myService;
 *    {@literal @}Inject
 *     public InstanceSupplier{@literal <}MyInterface{@literal >} myInterface;
 *    {@literal @}OnEventHandler
 *     public boolean updated(String in) {
 *         return true;
 *     }
 * }
 *
 * var sep = Fluxtion.compile(c -> {
 *     c.addNode(new InjectContextByType(), "injectionHolder");
 * });
 * sep.injectInstance(new MyService("injectedService"));
 *
 * </pre>
 * <p>
 * Can optionally fail fast if there is no value in the context mapped to the specified key. Failure is lazy and occurs
 * at the first get() call.
 * </pre>
 *
 * @param <T>
 */
public interface InstanceSupplier<T> extends Supplier<T> {

    static <S> InstanceSupplier<S> build(String contextKey) {
        return new InstanceSupplierNode<>(contextKey);
    }

    static <S> InstanceSupplier<S> buildFailFast(String contextKey) {
        return new InstanceSupplierNode<>(contextKey, true);
    }

}
