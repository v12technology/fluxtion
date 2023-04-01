package com.fluxtion.runtime.util;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.ArrayList;
import java.util.List;

public class ObjectPool<T> {

    private final SerializableSupplier<T> supplier;
    private transient final List<T> freeList = new ArrayList<>();

    public ObjectPool(SerializableSupplier<T> supplier) {
        this.supplier = supplier;
    }

    public T checkOut() {
        if (freeList.isEmpty()) {
            return supplier.get();
        }
        return freeList.remove(freeList.size() - 1);
    }

    public void checkIn(T returnedInstance) {
        freeList.add(returnedInstance);
    }

    public void reset() {
        freeList.clear();
    }
}
