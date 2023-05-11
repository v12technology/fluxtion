package com.fluxtion.runtime.dataflow.lookup;

import com.fluxtion.runtime.node.InstanceSupplier;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.function.ToLongFunction;

/**
 * Lookup a long value on a function supplied at runtime using a String key. Compares the look up long against a value
 * supplied in the {@link #isEqual(long)} method
 *
 * <pre>
 *
 * var eventProcessor = Fluxtion.interpret(c -> {
 *     EventFlow.subscribe(MarketUpdate.class)
 *             .filterByProperty(
 *                     MarketUpdate::id,
 *                     LongLookupPredicate.buildPredicate("EURUSD", "marketRefData"))
 *             .console("Filtered :{}");
 * });
 *
 * eventProcessor.injectNamedInstance((ToLongFunction<String>)new MarketReferenceData()::toId, ToLongFunction.class, "marketRefData");
 * eventProcessor.init();
 * </pre>
 */
public class LongLookupPredicate {

    private final String lookupString;
    private final InstanceSupplier<LongLookup> longLookupFunction;

    /**
     * See {@link InstanceSupplier} for injecting runtime instance of the lookup function
     *
     * @param lookupString       the String to apply at runtime to lookup the long value
     * @param longLookupFunction The lookup function provided at runtime ready for injection
     */
    public LongLookupPredicate(String lookupString, InstanceSupplier<LongLookup> longLookupFunction) {
        this.lookupString = lookupString;
        this.longLookupFunction = longLookupFunction;
    }

    /**
     * See {@link InstanceSupplier} for injecting runtime instance of the lookup function
     *
     * @param lookupString         the String to apply at runtime to lookup the long value
     * @param longLookupFunctionId The name of the lookup function provided at runtime ready for injection
     */
    public LongLookupPredicate(String lookupString, String longLookupFunctionId) {
        this(lookupString, InstanceSupplier.build(LongLookup.class, longLookupFunctionId));
    }

    /**
     * Build a LongLookupPredicate, supplying the functionId to use at runtime
     * <p>
     * See {@link InstanceSupplier} for injecting runtime instance of the lookup function
     *
     * @param lookupString         the String to apply at runtime to lookup the long value
     * @param longLookupFunctionId The lookup function provided at runtime
     * @return
     */
    public static SerializableFunction<Long, Boolean> buildPredicate(String lookupString, String longLookupFunctionId) {
        return new LongLookupPredicate(lookupString, longLookupFunctionId)::isEqual;
    }

    public interface LongLookup extends ToLongFunction<String> {
    }

    public boolean isEqual(long longToCompare) {
        return longToCompare == longLookupFunction.get().applyAsLong(lookupString);
    }

}
