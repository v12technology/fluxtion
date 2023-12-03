package com.fluxtion.compiler.generation.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ExportFunctionData {

    private final Method exportedmethod;
    private final List<CbMethodHandle> functionCallBackList = new ArrayList<>();

    public ExportFunctionData(Method exportedmethod) {
        this.exportedmethod = exportedmethod;
    }

    public Method getExportedmethod() {
        return exportedmethod;
    }

    public List<CbMethodHandle> getFunctionCallBackList() {
        return functionCallBackList;
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
