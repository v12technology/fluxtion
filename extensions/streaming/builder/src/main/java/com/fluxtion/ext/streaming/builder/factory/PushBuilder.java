package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.partition.LambdaReflection.MethodReferenceReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.streaming.api.Anchor;
import com.fluxtion.ext.streaming.api.PushNotifier;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler;

/**
 * Factory for building {@link PushNotifier} instances.
 *
 * @author V12 Technology Ltd.
 */
public class PushBuilder {

    /**
     * Adds and event graph dependency from the source to the target. The target will be notified if the source is on
     * the currently executing event path.
     *
     * @param <S>
     * @param <T>
     * @param source
     * @param target
     * @return
     */
    public static <S, T> S pushNotification(S source, T target) {
        PushNotifier p = SepContext.service().addOrReuse(new PushNotifier(source, target));
        return source;
    }

    /**
     * Creates a happens before relationship between two nodes even though there is no dependency relationship
     * between the nodes. This can happen if both nodes are siblings depending upon a common parent.
     *
     * @param <S> The type of the happens after node
     * @param <T> The type of the happens before node
     * @param anchor The anchor node that will be invoked first
     * @param afterAnchor The node that will be notified after the anchor node
     * @return The after anchor node
     */
    public static <S, T> S anchor(T anchor, S afterAnchor) {
        SepContext.service().addOrReuse(new Anchor( anchor, afterAnchor));
        return afterAnchor;
    }
    
    public static void anchor(Object anchor, Object... afterAnchors) {
        for (Object afterAnchor : afterAnchors) {
            anchor(anchor, afterAnchor);
            anchor = afterAnchor;
        }
    }

    public static <S extends T, T> void pushSource(T source, SerializableConsumer<S> consumer) {
        Object targetInstance = null;
        if (consumer.captured().length > 0) {
            targetInstance = consumer.captured()[0];
        }
        StreamFunctionCompiler.push(targetInstance, consumer.method(), source, null, true).build();
    }

    public static <T, S> void push(SerializableFunction<T, S> supplier, SerializableConsumer<? extends S> consumer) {
        Object sourceInstance = null;//unWrap(supplier);
        if (supplier.captured().length == 0) {
            sourceInstance = select(supplier.getContainingClass());
        } else {
            sourceInstance = supplier.captured()[0];
        }
        final Object targetInstance = consumer.captured()[0];//unWrap(consumer);
        StreamFunctionCompiler.push(targetInstance, consumer.method(), sourceInstance, supplier.method(), true).build();
    }

    /**
     * Pushes data from the source method to the target method when the source is on the executing event path.
     *
     * @param <S>
     * @param <D>
     * @param supplier
     * @param consumer
     */
    public static <D> void push(SerializableSupplier<D> supplier, SerializableConsumer<? extends D> consumer) {
        Object sourceInstance = supplier.captured()[0];//unWrap(supplier);
        if (sourceInstance == null) {
            sourceInstance = select(supplier.getContainingClass());
        }
        final Object targetInstance = consumer.captured()[0];//unWrap(consumer);
        StreamFunctionCompiler.push(targetInstance, consumer.method(), sourceInstance, supplier.method(), true).build();
    }

    public static <S, D> Wrapper<S> push(SerializableSupplier<D> supplier, SerializableFunction<? extends D, S> consumer) {
        Object sourceInstance = supplier.captured()[0];//unWrap(supplier);
        if (sourceInstance == null) {
            sourceInstance = select(supplier.getContainingClass());
        }
        final Object targetInstance = consumer.captured()[0];//unWrap(consumer);
        return StreamFunctionCompiler.push(targetInstance, consumer.method(), sourceInstance, supplier.method(), true).build();
    }

    public static Object unWrap(MethodReferenceReflection supplier) {
        final Object sourceInstance = supplier.captured()[0];
        if (sourceInstance instanceof Wrapper) {
            return ((Wrapper) sourceInstance).event();
        } else {
            return sourceInstance;
        }
    }

}
