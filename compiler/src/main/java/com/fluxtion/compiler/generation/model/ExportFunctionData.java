package com.fluxtion.compiler.generation.model;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ExportFunctionData {

    private final Method exportedmethod;
    private final boolean propagateMethod;
    private final List<CbMethodHandle> functionCallBackList = new ArrayList<>();

    public ExportFunctionData(Method exportedmethod, boolean propagateMethod) {
        this.exportedmethod = exportedmethod;
        this.propagateMethod = propagateMethod;
    }

    public void addCbMethodHandle(CbMethodHandle cbMethodHandle) {
        functionCallBackList.add(cbMethodHandle);
    }

    public boolean isBooleanReturn() {
        for (int i = 0, functionCallBackListSize = functionCallBackList.size(); i < functionCallBackListSize; i++) {
            CbMethodHandle cbMethodHandle = functionCallBackList.get(i);
            if (cbMethodHandle.getMethod().getReturnType() == boolean.class) {
                return true;
            }
        }
        return false;
    }
}
