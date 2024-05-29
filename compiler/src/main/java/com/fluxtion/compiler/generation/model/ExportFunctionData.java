package com.fluxtion.compiler.generation.model;

import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.dataflow.groupby.MutableTuple;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ExportFunctionData {

    private final Method exportedmethod;
    private final List<Tuple<CbMethodHandle, Boolean>> functionCallBackList = new ArrayList<>();

    public ExportFunctionData(Method exportedmethod) {
        this.exportedmethod = exportedmethod;
    }

    public void addCbMethodHandle(CbMethodHandle cbMethodHandle, boolean propagateClass) {
        functionCallBackList.add(new MutableTuple<>(cbMethodHandle, propagateClass));
    }

    public boolean isBooleanReturn() {
        for (int i = 0, functionCallBackListSize = functionCallBackList.size(); i < functionCallBackListSize; i++) {
            CbMethodHandle cbMethodHandle = functionCallBackList.get(i).getFirst();
            if (cbMethodHandle.getMethod().getReturnType() == boolean.class) {
                return true;
            }
        }
        return false;
    }
}
