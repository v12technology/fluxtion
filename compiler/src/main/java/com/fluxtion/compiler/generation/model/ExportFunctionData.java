package com.fluxtion.compiler.generation.model;

import java.util.ArrayList;
import java.util.List;

public class ExportFunctionData {

    private final String publicMethodName;
    private final List<CbMethodHandle> functionCallBackList = new ArrayList<>();

    public ExportFunctionData(String publicMethodName) {
        this.publicMethodName = publicMethodName;
    }

    public String getPublicMethodName() {
        return publicMethodName;
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
